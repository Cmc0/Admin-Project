package com.admin.im.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.mapper.SysUserMapper;
import com.admin.common.model.constant.BaseElasticsearchIndexConstant;
import com.admin.common.model.entity.BaseEntityTwo;
import com.admin.common.model.entity.SysUserDO;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.common.util.ElasticsearchUtil;
import com.admin.common.util.MyEntityUtil;
import com.admin.common.util.UserUtil;
import com.admin.im.model.document.ImFriendDocument;
import com.admin.im.model.document.ImFriendRequestDocument;
import com.admin.im.model.document.ImMessageDocument;
import com.admin.im.model.document.ImSessionDocument;
import com.admin.im.model.dto.*;
import com.admin.im.model.enums.ImContentTypeEnum;
import com.admin.im.model.enums.ImMessageCreateTypeEnum;
import com.admin.im.model.enums.ImRequestResultEnum;
import com.admin.im.model.enums.ImToTypeEnum;
import com.admin.im.model.vo.ImSessionPageVO;
import com.admin.im.service.ImService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ImServiceImpl implements ImService {

    @Resource
    ElasticsearchClient elasticsearchClient;
    @Resource
    SysUserMapper sysUserMapper;

    /**
     * 好友申请：发送
     */
    @SneakyThrows
    @Override
    public String friendRequest(ImFriendRequestDTO dto) {

        Long currentUserId = UserUtil.getCurrentUserId();

        if (currentUserId.equals(dto.getToId())) {
            ApiResultVO.error("操作失败：不能添加自己为好友");
        }

        List<Query> queryList = CollUtil
            .newArrayList(Query.of(q -> q.term(qt -> qt.field("createId").value(currentUserId))),
                Query.of(q -> q.term(qt -> qt.field("uId").value(dto.getToId()))));

        long searchTotal = ElasticsearchUtil
            .autoCreateIndexAndGetSearchTotal(BaseElasticsearchIndexConstant.IM_FRIEND_REQUEST_INDEX,
                s -> s.index(BaseElasticsearchIndexConstant.IM_FRIEND_REQUEST_INDEX)
                    .query(sq -> sq.bool(sqb -> sqb.must(queryList))));

        if (searchTotal > 0) {
            ApiResultVO.error("操作失败：对方已经是您的好友");
        }

        boolean exists = ChainWrappers.lambdaQueryChain(sysUserMapper).eq(BaseEntityTwo::getId, dto.getToId())
            .eq(SysUserDO::getDelFlag, false).exists();

        if (!exists) {
            ApiResultVO.error("操作失败：用户不存在");
        }

        Date date = new Date();

        ImFriendRequestDocument imFriendRequestDocument = new ImFriendRequestDocument();
        imFriendRequestDocument.setContent(MyEntityUtil.getNotNullStr(dto.getContent()));
        imFriendRequestDocument.setToId(dto.getToId());
        imFriendRequestDocument.setCreateId(currentUserId);
        imFriendRequestDocument.setCreateTime(date);
        imFriendRequestDocument.setResult(ImRequestResultEnum.PENDING);
        imFriendRequestDocument.setResultTime(date);

        elasticsearchClient.index(
            i -> i.index(BaseElasticsearchIndexConstant.IM_FRIEND_REQUEST_INDEX).id(IdUtil.simpleUUID())
                .document(imFriendRequestDocument));

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 好友申请：处理
     */
    @SneakyThrows
    @Override
    public String friendRequestHandler(ImFriendRequestHandlerDTO dto) {

        if (ImRequestResultEnum.PENDING.equals(dto.getResult())) {
            ApiResultVO.error(BaseBizCodeEnum.PARAMETER_CHECK_ERROR);
        }

        GetResponse<ImFriendRequestDocument> getResponse = ElasticsearchUtil
            .autoCreateIndexAndGet(BaseElasticsearchIndexConstant.IM_FRIEND_REQUEST_INDEX,
                g -> g.index(BaseElasticsearchIndexConstant.IM_FRIEND_REQUEST_INDEX).id(dto.getId()),
                ImFriendRequestDocument.class);

        ImFriendRequestDocument imFriendRequestDocument = getResponse.source();

        if (imFriendRequestDocument == null) {
            ApiResultVO.error("操作失败：好友请求不存在");
        }

        Long currentUserId = UserUtil.getCurrentUserId();

        if (!currentUserId.equals(imFriendRequestDocument.getToId())) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);
        }

        if (!ImRequestResultEnum.PENDING.equals(imFriendRequestDocument.getResult())) {
            ApiResultVO.error("操作失败：已经处理过了，请刷新重试");
        }

        Date date = new Date();

        imFriendRequestDocument.setResult(dto.getResult());
        imFriendRequestDocument.setResultTime(date);

        List<BulkOperation> bulkOperationList = new ArrayList<>();

        bulkOperationList.add(new BulkOperation.Builder().update(
            u -> u.index(BaseElasticsearchIndexConstant.IM_FRIEND_REQUEST_INDEX).id(dto.getId())
                .action(ua -> ua.doc(imFriendRequestDocument))).build());

        if (ImRequestResultEnum.AGREED.equals(dto.getResult())) {

            ImFriendDocument imFriendDocumentFrom = new ImFriendDocument();
            imFriendDocumentFrom.setCreateId(imFriendRequestDocument.getCreateId());
            imFriendDocumentFrom.setCreateTime(date);
            imFriendDocumentFrom.setUId(imFriendRequestDocument.getToId());
            imFriendDocumentFrom.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));

            bulkOperationList.add(new BulkOperation.Builder().index(
                i -> i.index(BaseElasticsearchIndexConstant.IM_FRIEND_INDEX).id(IdUtil.simpleUUID())
                    .document(imFriendDocumentFrom)).build());

            List<Query> queryList = CollUtil.newArrayList(
                Query.of(q -> q.term(qt -> qt.field("createId").value(imFriendRequestDocument.getToId()))),
                Query.of(q -> q.term(qt -> qt.field("uId").value(imFriendRequestDocument.getCreateId()))));

            long searchTotal = ElasticsearchUtil
                .autoCreateIndexAndGetSearchTotal(BaseElasticsearchIndexConstant.IM_FRIEND_INDEX,
                    s -> s.index(BaseElasticsearchIndexConstant.IM_FRIEND_INDEX)
                        .query(sq -> sq.bool(sqb -> sqb.must(queryList))));

            if (searchTotal == 0) {
                ImFriendDocument imFriendDocumentTo = new ImFriendDocument();
                imFriendDocumentTo.setCreateId(imFriendRequestDocument.getToId());
                imFriendDocumentTo.setCreateTime(date);
                imFriendDocumentTo.setUId(imFriendRequestDocument.getCreateId());
                imFriendDocumentTo.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));

                bulkOperationList.add(new BulkOperation.Builder().index(
                    i -> i.index(BaseElasticsearchIndexConstant.IM_FRIEND_INDEX).id(IdUtil.simpleUUID())
                        .document(imFriendDocumentTo)).build());
            }

            doSend(imFriendRequestDocument.getToId(), "", imFriendRequestDocument.getCreateId(), ImToTypeEnum.FRIEND,
                ImMessageCreateTypeEnum.REQUEST_RESULT, bulkOperationList, date);

        }

        elasticsearchClient.bulk(b -> b.operations(bulkOperationList));

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 好友申请：分页排序查询
     */
    @Override
    public Page<ImFriendRequestDocument> friendRequestPage(ImFriendRequestPageDTO dto) {

        Integer current = Convert.toInt(dto.getCurrent());
        Integer pageSize = Convert.toInt(dto.getPageSize());

        if (current == null || pageSize == null) {
            return dto.getPage(false);
        }

        Long currentUserId = UserUtil.getCurrentUserId();

        List<Query> queryList = CollUtil
            .newArrayList(Query.of(q -> q.term(qt -> qt.field("createId").value(currentUserId))),
                Query.of(q -> q.term(qt -> qt.field("toId").value(currentUserId))));

        SearchResponse<ImFriendRequestDocument> searchResponse = ElasticsearchUtil
            .autoCreateIndexAndSearch(BaseElasticsearchIndexConstant.IM_FRIEND_REQUEST_INDEX,
                s -> s.index(BaseElasticsearchIndexConstant.IM_FRIEND_REQUEST_INDEX).from((current - 1) * pageSize)
                    .size(pageSize).sort(ss -> ss.field(ssf -> ssf.field("createTime").order(SortOrder.Desc)))
                    .query(sq -> sq.bool(sqb -> sqb.should(queryList))), ImFriendRequestDocument.class);

        if (searchResponse == null) {
            return dto.getPage(false);
        }

        HitsMetadata<ImFriendRequestDocument> hits = searchResponse.hits();

        List<Hit<ImFriendRequestDocument>> hitList = hits.hits();

        List<ImFriendRequestDocument> imFriendRequestDocumentList = new ArrayList<>();

        for (Hit<ImFriendRequestDocument> item : hitList) {
            ImFriendRequestDocument imFriendRequestDocument = item.source();
            if (imFriendRequestDocument != null) {
                imFriendRequestDocument.setId(item.id());
                imFriendRequestDocumentList.add(imFriendRequestDocument);
            }
        }

        Page<ImFriendRequestDocument> page = dto.getPage(false);

        page.setRecords(imFriendRequestDocumentList);
        if (hits.total() != null) {
            page.setTotal(hits.total().value());
        }

        return page;
    }

    /**
     * 发送消息
     */
    @SneakyThrows
    @Override
    public String send(ImSendDTO dto) {

        Long currentUserId = UserUtil.getCurrentUserId();

        sendCheck(dto, currentUserId);

        doSend(currentUserId, dto.getContent(), dto.getToId(), dto.getToType(), ImMessageCreateTypeEnum.USER, null,
            null);

        return BaseBizCodeEnum.API_RESULT_SEND_OK.getMsg();
    }

    /**
     * bulkOperationList：为 null，则会执行：elasticsearchClient.bulk，反之 不执行
     */
    @SneakyThrows
    private void doSend(Long createId, String content, Long toId, ImToTypeEnum toType,
        ImMessageCreateTypeEnum createType, List<BulkOperation> bulkOperationList, Date date) {

        boolean bulkOperationListNullFlag = bulkOperationList == null;

        if (date == null) {
            date = new Date();
        }

        if (bulkOperationListNullFlag) {
            bulkOperationList = new ArrayList<>();
        }

        ImMessageDocument imMessageDocument = new ImMessageDocument();
        imMessageDocument.setCreateId(createId);
        imMessageDocument.setCreateTime(date);
        imMessageDocument.setContentType(ImContentTypeEnum.TEXT); // TODO：检测消息类型
        imMessageDocument.setContent(content);
        imMessageDocument.setToId(toId);
        imMessageDocument.setToType(toType);
        imMessageDocument.setCreateType(createType);
        imMessageDocument.setRIdSet(new HashSet<>());
        imMessageDocument.setQId(ImToTypeEnum.getQId(toType, toId, createId));

        bulkOperationList.add(new BulkOperation.Builder().index(
            i -> i.index(BaseElasticsearchIndexConstant.IM_MESSAGE_INDEX).id(IdUtil.simpleUUID())
                .document(imMessageDocument)).build());

        List<Query> queryOneList = CollUtil
            .newArrayList(Query.of(q -> q.term(qt -> qt.field("createId").value(createId))),
                Query.of(q -> q.term(qt -> qt.field("toId").value(toId))),
                Query.of(q -> q.term(qt -> qt.field("type").value(toType.getCode()))));

        if (ImToTypeEnum.FRIEND.equals(toType)) {

            doSendAddToBulkOperationList(createId, toId, toType, date, bulkOperationList, queryOneList);

            List<Query> queryTwoList = CollUtil
                .newArrayList(Query.of(q -> q.term(qt -> qt.field("createId").value(toId))),
                    Query.of(q -> q.term(qt -> qt.field("toId").value(createId))),
                    Query.of(q -> q.term(qt -> qt.field("type").value(toType.getCode()))));

            doSendAddToBulkOperationList(toId, createId, toType, date, bulkOperationList, queryTwoList);

        } else {
            doSendAddToBulkOperationList(createId, toId, toType, date, bulkOperationList, queryOneList);
        }

        if (bulkOperationListNullFlag) {
            List<BulkOperation> finalBulkOperationList = bulkOperationList;
            elasticsearchClient.bulk(b -> b.operations(finalBulkOperationList));
        }
    }

    private void doSendAddToBulkOperationList(Long createId, Long toId, ImToTypeEnum toType, Date date,
        List<BulkOperation> bulkOperationList, List<Query> queryList) {

        long searchTotal = ElasticsearchUtil
            .autoCreateIndexAndGetSearchTotal(BaseElasticsearchIndexConstant.IM_SESSION_INDEX,
                s -> s.query(sq -> sq.bool(sqb -> sqb.must(queryList))));

        if (searchTotal == 0) {
            ImSessionDocument imSessionDocument = new ImSessionDocument();
            imSessionDocument.setCreateId(createId);
            imSessionDocument.setToId(toId);
            imSessionDocument.setType(toType);
            imSessionDocument.setLastTime(date);
            imSessionDocument.setQId(ImToTypeEnum.getQId(toType, toId, createId));

            bulkOperationList.add(new BulkOperation.Builder().index(
                i -> i.index(BaseElasticsearchIndexConstant.IM_SESSION_INDEX).id(IdUtil.simpleUUID())
                    .document(imSessionDocument)).build());
        }

    }

    private void sendCheck(ImSendDTO dto, Long currentUserId) {

        if (ImToTypeEnum.FRIEND.equals(dto.getToType())) {

            if (currentUserId.equals(dto.getToId())) {
                ApiResultVO.error("操作失败：对方不是您的好友，无法发送消息");
            }

            List<Query> queryList = CollUtil
                .newArrayList(Query.of(q -> q.term(qt -> qt.field("createId").value(currentUserId))),
                    Query.of(q -> q.term(qt -> qt.field("uid").value(dto.getToId()))));

            long searchTotal = ElasticsearchUtil
                .autoCreateIndexAndGetSearchTotal(BaseElasticsearchIndexConstant.IM_FRIEND_INDEX,
                    s -> s.index(BaseElasticsearchIndexConstant.IM_FRIEND_INDEX)
                        .query(sq -> sq.bool(sqb -> sqb.must(queryList))));

            if (searchTotal == 0) {
                ApiResultVO.error("操作失败：对方不是您的好友，无法发送消息");
            }

            boolean exists = ChainWrappers.lambdaQueryChain(sysUserMapper).eq(BaseEntityTwo::getId, dto.getToId())
                .eq(SysUserDO::getDelFlag, false).exists();

            if (!exists) {
                ApiResultVO.error("操作失败：对方账号已注销");
            }
        } else {

            List<Query> queryList = CollUtil
                .newArrayList(Query.of(q -> q.term(qt -> qt.field("createId").value(currentUserId))),
                    Query.of(q -> q.term(qt -> qt.field("gid").value(dto.getToId()))));

            long searchTotal = ElasticsearchUtil
                .autoCreateIndexAndGetSearchTotal(BaseElasticsearchIndexConstant.IM_GROUP_JOIN_INDEX,
                    s -> s.index(BaseElasticsearchIndexConstant.IM_GROUP_JOIN_INDEX)
                        .query(sq -> sq.bool(sqb -> sqb.must(queryList))));

            if (searchTotal == 0) {
                ApiResultVO.error("操作失败：您不在群组里，无法发送消息");
            }

            searchTotal = ElasticsearchUtil
                .autoCreateIndexAndGetSearchTotal(BaseElasticsearchIndexConstant.IM_GROUP_JOIN_INDEX,
                    s -> s.index(BaseElasticsearchIndexConstant.IM_GROUP_JOIN_INDEX)
                        .query(sq -> sq.term(sqt -> sqt.field("gid").value(dto.getToId()))));

            if (searchTotal == 0) {
                ApiResultVO.error("操作失败：群组已解散");
            }

        }
    }

    /**
     * 会话：分页排序查询
     */
    @Override
    public Page<ImSessionPageVO> sessionPage(ImSessionPageDTO dto) {

        Integer current = Convert.toInt(dto.getCurrent());
        Integer pageSize = Convert.toInt(dto.getPageSize());

        if (current == null || pageSize == null) {
            return dto.getPage(false);
        }

        Long currentUserId = UserUtil.getCurrentUserId();

        SearchResponse<ImSessionPageVO> searchResponse = ElasticsearchUtil
            .autoCreateIndexAndSearch(BaseElasticsearchIndexConstant.IM_SESSION_INDEX,
                s -> s.index(BaseElasticsearchIndexConstant.IM_SESSION_INDEX).from((current - 1) * pageSize)
                    .size(pageSize).query(sq -> sq.term(sqt -> sqt.field("createId").value(currentUserId))),
                ImSessionPageVO.class);

        if (searchResponse == null) {
            return dto.getPage(false);
        }

        HitsMetadata<ImSessionPageVO> hits = searchResponse.hits();

        List<Hit<ImSessionPageVO>> hitList = hits.hits();

        List<ImSessionPageVO> imSessionPageVOList = new ArrayList<>();

        for (Hit<ImSessionPageVO> item : hitList) {
            ImSessionPageVO imSessionPageVO = item.source();
            if (imSessionPageVO != null) {
                imSessionPageVO.setId(item.id());
                imSessionPageVOList.add(imSessionPageVO);
            }
        }

        if (hitList.size() != 0) {
            getSessionPageOther(imSessionPageVOList, currentUserId);
        }

        Page<ImSessionPageVO> page = dto.getPage(false);

        page.setRecords(imSessionPageVOList);
        if (hits.total() != null) {
            page.setTotal(hits.total().value());
        }

        return page;
    }

    private void getSessionPageOther(List<ImSessionPageVO> imSessionPageVOList, Long currentUserId) {

        // 获取：最后一次聊天的内容
        String lastContentAggs = "last_content";

        Set<String> qIdSet = imSessionPageVOList.stream().map(ImSessionDocument::getQId).collect(Collectors.toSet());

        //        SearchResponse<ImMessageDocument> searchResponse = ElasticsearchUtil
        //            .autoCreateIndexAndSearch(BaseElasticsearchIndexConstant.IM_MESSAGE_INDEX,
        //                s -> s.index(BaseElasticsearchIndexConstant.IM_MESSAGE_INDEX).size(0)
        //                    .query(sq -> sq.terms(sqt -> sqt.field("").terms(sqtt -> sqtt.value(fieldValueList))))
        //                    .aggregations(lastContentAggs, sa -> sa.topHits(sat -> sat.size(1)
        //                        .sort(sats -> sats.field(satsf -> satsf.field("createTime").order(SortOrder.Desc))))),
        //                ImMessageDocument.class);

        // 获取：未读消息的数量

    }

}
