package com.admin.im.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.mapper.SysUserMapper;
import com.admin.common.model.constant.BaseElasticsearchIndexConstant;
import com.admin.common.model.entity.BaseEntityTwo;
import com.admin.common.model.entity.SysUserDO;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.common.util.UserUtil;
import com.admin.im.model.document.ImElasticsearchBaseDocument;
import com.admin.im.model.document.ImElasticsearchMsgDocument;
import com.admin.im.model.dto.ImContentPageDTO;
import com.admin.im.model.dto.ImSendDTO;
import com.admin.im.model.dto.ImSessionPageDTO;
import com.admin.im.model.enums.ImContentTypeEnum;
import com.admin.im.model.enums.ImToTypeEnum;
import com.admin.im.model.vo.ImContentPageVO;
import com.admin.im.model.vo.ImSessionPageVO;
import com.admin.im.service.ImService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ImServiceImpl implements ImService {

    @Resource
    ElasticsearchClient elasticsearchClient;
    @Resource
    SysUserMapper sysUserMapper;

    /**
     * 发送消息
     */
    @SneakyThrows
    @Override
    public String send(ImSendDTO dto) {

        Long currentUserId = UserUtil.getCurrentUserId();

        ImToTypeEnum imToTypeEnum = insertOrUpdateBaseDocument(dto, currentUserId);// 新增/修改：即时通讯功能的 基础index

        insertOrUpdateMsgDocument(dto, imToTypeEnum, currentUserId); // 新增/修改：即时通讯功能的 消息index

        return BaseBizCodeEnum.API_RESULT_SEND_OK.getMsg();
    }

    /**
     * 新增/修改：即时通讯功能的 消息index
     */
    @SneakyThrows
    private void insertOrUpdateMsgDocument(ImSendDTO dto, ImToTypeEnum imToTypeEnum, Long currentUserId) {

        ImElasticsearchMsgDocument imElasticsearchMsgDocument = new ImElasticsearchMsgDocument();
        imElasticsearchMsgDocument.setCreateTime(new Date());
        imElasticsearchMsgDocument.setCreateId(currentUserId);
        imElasticsearchMsgDocument.setContent(dto.getContent());
        imElasticsearchMsgDocument.setContentType(ImContentTypeEnum.TEXT); // TODO：暂时写成：文本
        imElasticsearchMsgDocument.setToId(dto.getToId());
        imElasticsearchMsgDocument.setToType(imToTypeEnum);
        imElasticsearchMsgDocument.setSId(imToTypeEnum.getSId(dto.getToId()));

        String userImMsgIndex = BaseElasticsearchIndexConstant.IM_MSG_INDEX_ + currentUserId;

        checkAndCreateIndex(userImMsgIndex);

        elasticsearchClient.index(i -> i.index(userImMsgIndex).document(imElasticsearchMsgDocument));
    }

    /**
     * 新增/修改：即时通讯功能的 基础index
     */
    @SneakyThrows
    private ImToTypeEnum insertOrUpdateBaseDocument(ImSendDTO dto, Long currentUserId) {

        ImToTypeEnum imToTypeEnum = ImToTypeEnum.getByCode(dto.getToType());
        if (imToTypeEnum == null) {
            ApiResultVO.error(BaseBizCodeEnum.PARAMETER_CHECK_ERROR);
        }

        checkAndCreateIndex(BaseElasticsearchIndexConstant.IM_BASE_INDEX);

        GetResponse<ImElasticsearchBaseDocument> getResponse = elasticsearchClient
            .get(i -> i.index(BaseElasticsearchIndexConstant.IM_BASE_INDEX).id(currentUserId.toString()),
                ImElasticsearchBaseDocument.class);

        ImElasticsearchBaseDocument imElasticsearchBaseDocument = getResponse.source();

        boolean imBaseDocumentNullFlag = imElasticsearchBaseDocument == null;

        if (imBaseDocumentNullFlag) {
            imElasticsearchBaseDocument = new ImElasticsearchBaseDocument();
        }

        imElasticsearchBaseDocument.getSIdSet().add(imToTypeEnum.getSId(dto.getToId()));

        ImElasticsearchBaseDocument finalImElasticsearchBaseDocument = imElasticsearchBaseDocument;
        if (imBaseDocumentNullFlag) {
            elasticsearchClient.index(
                i -> i.index(BaseElasticsearchIndexConstant.IM_BASE_INDEX).id(currentUserId.toString())
                    .document(finalImElasticsearchBaseDocument));
        } else {
            elasticsearchClient.update(
                i -> i.index(BaseElasticsearchIndexConstant.IM_BASE_INDEX).id(currentUserId.toString())
                    .doc(finalImElasticsearchBaseDocument), ImElasticsearchBaseDocument.class);
        }

        return imToTypeEnum;
    }

    @SneakyThrows
    private void checkAndCreateIndex(String index) {

        BooleanResponse booleanResponse = elasticsearchClient.indices().exists(e -> e.index(index));

        if (!booleanResponse.value()) {
            elasticsearchClient.indices().create(i -> i.index(index));
        }
    }

    /**
     * 分页排序查询：即时通讯会话，备注：暂时不支持分页
     * 示例：
     * GET im_msg_index_1/_search
     * {
     * "aggs": {
     * "group_by_sid": {
     * "aggs": {
     * "last_content": {
     * "top_hits": {
     * "size": 1,
     * "sort": [
     * {
     * "createTime": {
     * "order": "desc"
     * }
     * }
     * ]
     * }
     * }
     * },
     * "terms": {
     * "field": "sid.keyword"
     * }
     * }
     * },
     * "query": {
     * "terms": {
     * "sid.keyword": [
     * "1_1",
     * "1_2",
     * "2_1",
     * "2_2"
     * ]
     * }
     * },
     * "size": 0
     * }
     */
    @SneakyThrows
    @Override
    public Page<ImSessionPageVO> sessionPage(ImSessionPageDTO dto) {

        Long currentUserId = UserUtil.getCurrentUserId();

        checkAndCreateIndex(BaseElasticsearchIndexConstant.IM_BASE_INDEX);

        GetResponse<ImElasticsearchBaseDocument> getResponse = elasticsearchClient
            .get(i -> i.index(BaseElasticsearchIndexConstant.IM_BASE_INDEX).id(currentUserId.toString()),
                ImElasticsearchBaseDocument.class);

        ImElasticsearchBaseDocument imElasticsearchBaseDocument = getResponse.source();

        if (imElasticsearchBaseDocument == null) {
            return dto.getPage(false);
        }

        Set<String> sIdSet = imElasticsearchBaseDocument.getSIdSet(); // 获取：会话 idSet

        List<FieldValue> fieldValueList = sIdSet.stream().map(FieldValue::of).collect(Collectors.toList());

        if (CollUtil.isEmpty(sIdSet)) {
            return dto.getPage(false);
        }

        String userImMsgIndex = BaseElasticsearchIndexConstant.IM_MSG_INDEX_ + currentUserId;

        String groupBySidAggs = "group_by_sid";
        String lastContentAggs = "last_content";

        SearchResponse<ImElasticsearchMsgDocument> searchResponse =
            elasticsearchClient.search(i -> i.index(userImMsgIndex) //
                    .size(0) //
                    .query(q -> q.terms(qt -> qt.field("sid.keyword").terms(qtt -> qtt.value(fieldValueList)))) //
                    .aggregations(groupBySidAggs, a -> a.terms(at -> at.field("sid.keyword")) //
                        .aggregations(lastContentAggs, aa -> aa.topHits(aat -> aat.size(1)
                            .sort(aats -> aats.field(aatsf -> aatsf.field("createTime").order(SortOrder.Desc)))))) //
                , ImElasticsearchMsgDocument.class);

        List<StringTermsBucket> stringTermsBucketList =
            searchResponse.aggregations().get(groupBySidAggs).sterms().buckets().array();

        if (stringTermsBucketList.size() == 0) {
            return dto.getPage(false);
        }

        List<ImSessionPageVO> imSessionPageVOList = new ArrayList<>();

        Set<Long> toIdSet = new HashSet<>();

        for (StringTermsBucket item : stringTermsBucketList) {
            List<Hit<JsonData>> hitList = item.aggregations().get(lastContentAggs).topHits().hits().hits();
            if (hitList.size() != 0) {
                Hit<JsonData> jsonDataHit = hitList.get(0);
                JsonData source = jsonDataHit.source();
                if (source != null) {
                    ImSessionPageVO imSessionPageVO = source.to(ImSessionPageVO.class);
                    imSessionPageVO.setId(jsonDataHit.id());
                    imSessionPageVOList.add(imSessionPageVO);
                    toIdSet.add(imSessionPageVO.getToId());
                }
            }
        }

        Stream<ImSessionPageVO> stream = imSessionPageVOList.stream();

        if (toIdSet.size() != 0) {
            List<SysUserDO> sysUserDOList =
                ChainWrappers.lambdaQueryChain(sysUserMapper).in(BaseEntityTwo::getId, toIdSet)
                    .select(SysUserDO::getAvatarUrl, SysUserDO::getNickname, BaseEntityTwo::getId).list();

            Map<Long, SysUserDO> groupMap =
                sysUserDOList.stream().collect(Collectors.toMap(BaseEntityTwo::getId, it -> it));

            if (groupMap.size() != 0) {
                stream.forEach(item -> {
                    SysUserDO sysUserDO = groupMap.get(item.getToId());
                    if (sysUserDO != null) {
                        item.setToAvatarUrl(sysUserDO.getAvatarUrl()); // 设置：头像等
                        item.setToNickname(sysUserDO.getNickname());
                    }
                });
            }
        }

        // 根据：创建时间倒序
        imSessionPageVOList =
            stream.sorted(Comparator.comparing(ImElasticsearchMsgDocument::getCreateTime, Comparator.reverseOrder()))
                .collect(Collectors.toList());

        Page<ImSessionPageVO> page = dto.getPage(false);

        page.setRecords(imSessionPageVOList);
        page.setTotal(imSessionPageVOList.size());

        return page;
    }

    /**
     * 分页排序查询：即时通讯内容
     * 示例：
     * GET im_msg_index_1/_search
     * {
     * "query": {
     * "term": {
     * "sid": {
     * "value": "1_1"
     * }
     * }
     * },
     * "sort": [
     * {
     * "createTime": {
     * "order": "desc"
     * }
     * }
     * ],
     * "from": 0,
     * "size": 10
     * }
     */
    @SneakyThrows
    @Override
    public Page<ImContentPageVO> contentPage(ImContentPageDTO dto) {

        Long currentUserId = UserUtil.getCurrentUserId();

        String userImMsgIndex = BaseElasticsearchIndexConstant.IM_MSG_INDEX_ + currentUserId;

        checkAndCreateIndex(userImMsgIndex);

        Integer current = Convert.toInt(dto.getCurrent());
        Integer pageSize = Convert.toInt(dto.getPageSize());

        if (current == null || pageSize == null) {
            return dto.getPage(false);
        }

        SearchResponse<ImContentPageVO> searchResponse = elasticsearchClient.search(i -> i.index(userImMsgIndex) //
                .from((current - 1) * pageSize).size(pageSize) //
                .sort(s -> s.field(sf -> sf.field("createTime").order(SortOrder.Desc))) //
                .query(q -> q.term(qt -> qt.field("sid.keyword").value(dto.getSId()))) //
            , ImContentPageVO.class);

        HitsMetadata<ImContentPageVO> hits = searchResponse.hits();

        List<Hit<ImContentPageVO>> hitList = hits.hits();

        List<ImContentPageVO> imContentPageVOList = new ArrayList<>();

        for (Hit<ImContentPageVO> item : hitList) {
            ImContentPageVO imContentPageVO = item.source();
            if (imContentPageVO != null) {
                imContentPageVO.setId(item.id());
                imContentPageVOList.add(imContentPageVO);
            }
        }

        Page<ImContentPageVO> page = dto.getPage(false);

        page.setRecords(imContentPageVOList);
        page.setTotal(hits.total().value());

        return page;
    }

}
