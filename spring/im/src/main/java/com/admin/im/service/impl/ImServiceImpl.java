package com.admin.im.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.util.ObjectBuilder;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.mapper.SysUserMapper;
import com.admin.common.model.constant.BaseElasticsearchIndexConstant;
import com.admin.common.model.entity.BaseEntityTwo;
import com.admin.common.model.entity.SysUserDO;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.common.util.UserUtil;
import com.admin.im.model.document.ImElasticsearchBaseDocument;
import com.admin.im.model.document.ImElasticsearchFriendRequestDocument;
import com.admin.im.model.document.ImElasticsearchMsgDocument;
import com.admin.im.model.dto.*;
import com.admin.im.model.enums.ImContentTypeEnum;
import com.admin.im.model.enums.ImFriendRequestResultEnum;
import com.admin.im.model.enums.ImToTypeEnum;
import com.admin.im.model.vo.ImContentPageVO;
import com.admin.im.model.vo.ImFriendRequestPageVO;
import com.admin.im.model.vo.ImSessionPageVO;
import com.admin.im.service.ImService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ImServiceImpl implements ImService {

    @Resource
    ElasticsearchClient elasticsearchClient;
    @Resource
    SysUserMapper sysUserMapper;

    /**
     * 如果执行失败，则创建 index之后，再执行一次
     */
    @SneakyThrows
    @Nullable
    private <TDocument> GetResponse<TDocument> autoCreateIndexAndGet(String index,
        Function<GetRequest.Builder, ObjectBuilder<GetRequest>> fn, Class<TDocument> tDocumentClass) {

        try {
            return elasticsearchClient.get(fn, tDocumentClass);
        } catch (ElasticsearchException e) {
            if ("index_not_found_exception".equals(e.error().type())) {
                elasticsearchClient.indices().create(c -> c.index(index));
                return elasticsearchClient.get(fn, tDocumentClass);
            }
            throw e;
        }
    }

    /**
     * 如果执行失败，则创建 index之后，再执行一次
     */
    @SneakyThrows
    @Nullable
    private <TDocument> SearchResponse<TDocument> autoCreateIndexAndSearch(String index,
        Function<SearchRequest.Builder, ObjectBuilder<SearchRequest>> fn, Class<TDocument> tDocumentClass) {

        try {
            return elasticsearchClient.search(fn, tDocumentClass);
        } catch (ElasticsearchException e1) {
            if ("index_not_found_exception".equals(e1.error().type())) {
                elasticsearchClient.indices().create(c -> c.index(index));
                try {
                    return elasticsearchClient.search(fn, tDocumentClass);
                } catch (ElasticsearchException e2) {
                    if ("search_phase_execution_exception".equals(e2.error().type())) {
                        return null;
                    }
                }

            }
            e1.printStackTrace();
            return null;
        }
    }

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

        elasticsearchClient
            .index(i -> i.index(userImMsgIndex).id(IdUtil.simpleUUID()).document(imElasticsearchMsgDocument));
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

        doInsertOrUpdateBaseDocument(currentUserId, i -> i.getSIdSet().add(imToTypeEnum.getSId(dto.getToId())));

        return imToTypeEnum;
    }

    /**
     * 执行：新增/修改：即时通讯功能的 基础index
     */
    @SneakyThrows
    private <T> void doInsertOrUpdateBaseDocument(Long currentUserId,
        Function<ImElasticsearchBaseDocument, T> function) {

        GetResponse<ImElasticsearchBaseDocument> getResponse =
            autoCreateIndexAndGet(BaseElasticsearchIndexConstant.IM_BASE_INDEX,
                g -> g.index(BaseElasticsearchIndexConstant.IM_BASE_INDEX).id(currentUserId.toString()),
                ImElasticsearchBaseDocument.class);

        ImElasticsearchBaseDocument imElasticsearchBaseDocument = getResponse.source();

        if (imElasticsearchBaseDocument == null) {
            imElasticsearchBaseDocument = new ImElasticsearchBaseDocument();
        }

        function.apply(imElasticsearchBaseDocument);

        ImElasticsearchBaseDocument finalImElasticsearchBaseDocument = imElasticsearchBaseDocument;

        elasticsearchClient.index(
            i -> i.index(BaseElasticsearchIndexConstant.IM_BASE_INDEX).id(currentUserId.toString())
                .document(finalImElasticsearchBaseDocument));
    }

    /**
     * 即时通讯会话，分页排序查询，备注：暂时不支持分页
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

        GetResponse<ImElasticsearchBaseDocument> getResponse =
            autoCreateIndexAndGet(BaseElasticsearchIndexConstant.IM_BASE_INDEX,
                g -> g.index(BaseElasticsearchIndexConstant.IM_BASE_INDEX).id(currentUserId.toString()),
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
            autoCreateIndexAndSearch(userImMsgIndex, s -> s.index(userImMsgIndex) //
                    .size(0) //
                    .query(sq -> sq.terms(sqt -> sqt.field("sid.keyword").terms(sqtt -> sqtt.value(fieldValueList)))) //
                    .aggregations(groupBySidAggs, sa -> sa.terms(sat -> sat.field("sid.keyword")) //
                        .aggregations(lastContentAggs, saa -> saa.topHits(saat -> saat.size(1)
                            .sort(saats -> saats.field(saatsf -> saatsf.field("createTime").order(SortOrder.Desc)))))) //
                , ImElasticsearchMsgDocument.class);

        if (searchResponse == null) {
            return dto.getPage(false);
        }

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
     * 即时通讯内容，分页排序查询
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

        Integer current = Convert.toInt(dto.getCurrent());
        Integer pageSize = Convert.toInt(dto.getPageSize());

        if (current == null || pageSize == null) {
            return dto.getPage(false);
        }

        Long currentUserId = UserUtil.getCurrentUserId();

        String userImMsgIndex = BaseElasticsearchIndexConstant.IM_MSG_INDEX_ + currentUserId;

        SearchResponse<ImContentPageVO> searchResponse =
            autoCreateIndexAndSearch(userImMsgIndex, s -> s.index(userImMsgIndex) //
                    .from((current - 1) * pageSize).size(pageSize) //
                    .sort(ss -> ss.field(ssf -> ssf.field("createTime").order(SortOrder.Desc))) //
                    .query(sq -> sq.term(sqt -> sqt.field("sid.keyword").value(dto.getSId()))) //
                , ImContentPageVO.class);

        if (searchResponse == null) {
            return dto.getPage(false);
        }

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
        if (hits.total() != null) {
            page.setTotal(hits.total().value());
        }

        return page;
    }

    /**
     * 好友申请
     */
    @SneakyThrows
    @Override
    public String friendRequest(ImFriendRequestDTO dto) {

        Long currentUserId = UserUtil.getCurrentUserId();

        if (currentUserId.equals(dto.getToId())) {
            ApiResultVO.error("操作失败：不能给自己发送好友请求");
        }

        Date date = new Date();

        ImElasticsearchFriendRequestDocument imElasticsearchFriendRequestDocument =
            new ImElasticsearchFriendRequestDocument();
        imElasticsearchFriendRequestDocument.setCreateId(currentUserId);
        imElasticsearchFriendRequestDocument.setCreateTime(date);
        imElasticsearchFriendRequestDocument.setUpdateTime(date);
        imElasticsearchFriendRequestDocument.setContent(dto.getContent());
        imElasticsearchFriendRequestDocument.setToId(dto.getToId());
        imElasticsearchFriendRequestDocument.setResult(ImFriendRequestResultEnum.PENDING);

        elasticsearchClient.index(
            i -> i.index(BaseElasticsearchIndexConstant.IM_FRIEND_REQUEST_INDEX).id(IdUtil.simpleUUID())
                .document(imElasticsearchFriendRequestDocument));

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 好友申请，分页排序查询，备注：包含我的申请，以及对我的申请
     * 示例：
     * GET im_friend_request_index/_search
     * GET im_friend_request_index/_search
     * {
     * "query": {
     * "bool": {
     * "should": [
     * {
     * "term": {
     * "createId": {
     * "value": 1
     * }
     * }
     * },
     * {
     * "term": {
     * "toId": {
     * "value": 1
     * }
     * }
     * }
     * ]
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
    public Page<ImFriendRequestPageVO> friendRequestPage(ImFriendRequestPageDTO dto) {

        Integer current = Convert.toInt(dto.getCurrent());
        Integer pageSize = Convert.toInt(dto.getPageSize());

        if (current == null || pageSize == null) {
            return dto.getPage(false);
        }

        long currentUserId = UserUtil.getCurrentUserId();

        List<Query> queryList = CollUtil
            .newArrayList(Query.of(q -> q.term(qt -> qt.field("createId").value(currentUserId))),
                Query.of(q -> q.term(qt -> qt.field("toId").value(currentUserId))));

        SearchResponse<ImFriendRequestPageVO> searchResponse =
            autoCreateIndexAndSearch(BaseElasticsearchIndexConstant.IM_FRIEND_REQUEST_INDEX,
                s -> s.index(BaseElasticsearchIndexConstant.IM_FRIEND_REQUEST_INDEX) //
                    .from((current - 1) * pageSize).size(pageSize) //
                    .sort(ss -> ss.field(ssf -> ssf.field("createTime").order(SortOrder.Desc))) //
                    .query(sq -> sq.bool(sqb -> sqb.should(queryList))) //
                , ImFriendRequestPageVO.class);

        if (searchResponse == null) {
            return dto.getPage(false);
        }

        HitsMetadata<ImFriendRequestPageVO> hits = searchResponse.hits();

        List<Hit<ImFriendRequestPageVO>> hitList = hits.hits();

        List<ImFriendRequestPageVO> imFriendRequestPageVOList = new ArrayList<>();

        for (Hit<ImFriendRequestPageVO> item : hitList) {
            ImFriendRequestPageVO imFriendRequestPageVO = item.source();
            if (imFriendRequestPageVO != null) {
                imFriendRequestPageVO.setId(item.id());
                imFriendRequestPageVOList.add(imFriendRequestPageVO);
            }
        }

        Page<ImFriendRequestPageVO> page = dto.getPage(false);

        page.setRecords(imFriendRequestPageVOList);
        if (hits.total() != null) {
            page.setTotal(hits.total().value());
        }

        return page;
    }

    /**
     * 好友申请，结果处理
     */
    @SneakyThrows
    @Override
    public String friendRequestHandler(ImFriendRequestHandlerDTO dto) {

        GetResponse<ImElasticsearchFriendRequestDocument> getResponse =
            autoCreateIndexAndGet(BaseElasticsearchIndexConstant.IM_FRIEND_REQUEST_INDEX,
                g -> g.index(BaseElasticsearchIndexConstant.IM_FRIEND_REQUEST_INDEX).id(dto.getId()),
                ImElasticsearchFriendRequestDocument.class);

        ImElasticsearchFriendRequestDocument imElasticsearchFriendRequestDocument = getResponse.source();

        if (imElasticsearchFriendRequestDocument == null) {
            return BaseBizCodeEnum.ILLEGAL_REQUEST.getMsg();
        }

        imElasticsearchFriendRequestDocument = new ImElasticsearchFriendRequestDocument();
        imElasticsearchFriendRequestDocument.setResult(dto.getResult());
        imElasticsearchFriendRequestDocument.setUpdateTime(new Date());

        ImElasticsearchFriendRequestDocument finalImElasticsearchFriendRequestDocument =
            imElasticsearchFriendRequestDocument;

        elasticsearchClient.update(u -> u.index(BaseElasticsearchIndexConstant.IM_FRIEND_REQUEST_INDEX).id(dto.getId())
            .doc(finalImElasticsearchFriendRequestDocument), ImElasticsearchFriendRequestDocument.class);

        if (ImFriendRequestResultEnum.AGREED.equals(dto.getResult())) {
            // 如果同意了，则添加双方到联系人列表
            doInsertOrUpdateBaseDocument(finalImElasticsearchFriendRequestDocument.getCreateId(),
                i -> i.getCIdSet().add(finalImElasticsearchFriendRequestDocument.getToId()));
            doInsertOrUpdateBaseDocument(finalImElasticsearchFriendRequestDocument.getToId(),
                i -> i.getCIdSet().add(finalImElasticsearchFriendRequestDocument.getCreateId()));
        }

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

}
