package com.admin.im.service.impl;

import cn.hutool.core.collection.CollUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.model.constant.BaseElasticsearchIndexConstant;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.common.util.UserUtil;
import com.admin.im.model.document.ImElasticsearchBaseDocument;
import com.admin.im.model.document.ImElasticsearchMsgDocument;
import com.admin.im.model.dto.ImSendDTO;
import com.admin.im.model.dto.ImSessionPageDTO;
import com.admin.im.model.enums.ImContentTypeEnum;
import com.admin.im.model.enums.ImToTypeEnum;
import com.admin.im.model.vo.ImSessionPageVO;
import com.admin.im.service.ImService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ImServiceImpl implements ImService {

    @Resource
    ElasticsearchClient elasticsearchClient;

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
     * 分页排序查询：即时通讯会话
     */
    /**
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

        for (StringTermsBucket item : stringTermsBucketList) {

            String key = item.key(); // sId

            List<Hit<JsonData>> hitList = item.aggregations().get(lastContentAggs).topHits().hits().hits();
            if (hitList.size() != 0) {
                Hit<JsonData> jsonDataHit = hitList.get(0);
                String id = jsonDataHit.id();
                JsonData source = jsonDataHit.source();
                if (source != null) {
                    ImElasticsearchMsgDocument imElasticsearchMsgDocument = source.to(ImElasticsearchMsgDocument.class);
                    System.out.println(imElasticsearchMsgDocument);
                }
            }
        }

        return null;
    }

}
