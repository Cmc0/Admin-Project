package com.admin.im.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.MgetResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.get.GetResult;
import co.elastic.clients.elasticsearch.core.mget.MultiGetResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.mapper.SysUserMapper;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.constant.BaseElasticsearchIndexConstant;
import com.admin.common.model.dto.NotEmptyStrIdSet;
import com.admin.common.model.entity.BaseEntityTwo;
import com.admin.common.model.entity.SysUserDO;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.common.util.ElasticsearchUtil;
import com.admin.common.util.MyEntityUtil;
import com.admin.common.util.UserUtil;
import com.admin.im.model.document.*;
import com.admin.im.model.dto.*;
import com.admin.im.model.enums.*;
import com.admin.im.model.vo.ImFriendPageVO;
import com.admin.im.model.vo.ImSessionPageVO;
import com.admin.im.service.ImService;
import com.admin.im.util.ImHelpUtil;
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
                Query.of(q -> q.term(qt -> qt.field("uid").value(dto.getToId()))));

        long searchTotal = ElasticsearchUtil
            .autoCreateIndexAndGetSearchTotal(BaseElasticsearchIndexConstant.IM_FRIEND_INDEX,
                s -> s.index(BaseElasticsearchIndexConstant.IM_FRIEND_INDEX)
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

        elasticsearchClient.index(i -> i.index(BaseElasticsearchIndexConstant.IM_FRIEND_REQUEST_INDEX)
            .id(ImHelpUtil.getFriendRequestId(currentUserId, dto.getToId())).document(imFriendRequestDocument));

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
            ApiResultVO.error("操作失败：好友请求不存在，请刷新重试");
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
            imFriendDocumentFrom.setUid(imFriendRequestDocument.getToId());

            bulkOperationList.add(new BulkOperation.Builder().index(
                i -> i.index(BaseElasticsearchIndexConstant.IM_FRIEND_INDEX).id(ImHelpUtil
                    .getFriendId(imFriendRequestDocument.getCreateId(), imFriendRequestDocument.getToId()))
                    .document(imFriendDocumentFrom)).build());

            List<Query> queryList = CollUtil.newArrayList(
                Query.of(q -> q.term(qt -> qt.field("createId").value(imFriendRequestDocument.getToId()))),
                Query.of(q -> q.term(qt -> qt.field("uid").value(imFriendRequestDocument.getCreateId()))));

            long searchTotal = ElasticsearchUtil
                .autoCreateIndexAndGetSearchTotal(BaseElasticsearchIndexConstant.IM_FRIEND_INDEX,
                    s -> s.index(BaseElasticsearchIndexConstant.IM_FRIEND_INDEX)
                        .query(sq -> sq.bool(sqb -> sqb.must(queryList))));

            if (searchTotal == 0) {
                ImFriendDocument imFriendDocumentTo = new ImFriendDocument();
                imFriendDocumentTo.setCreateId(imFriendRequestDocument.getToId());
                imFriendDocumentTo.setCreateTime(date);
                imFriendDocumentTo.setUid(imFriendRequestDocument.getCreateId());

                bulkOperationList.add(new BulkOperation.Builder().index(
                    i -> i.index(BaseElasticsearchIndexConstant.IM_FRIEND_INDEX).id(ImHelpUtil
                        .getFriendId(imFriendRequestDocument.getToId(), imFriendRequestDocument.getCreateId()))
                        .document(imFriendDocumentTo)).build());
            }

            doSend(imFriendRequestDocument.getToId(), "", imFriendRequestDocument.getCreateId().toString(),
                ImToTypeEnum.FRIEND, ImMessageCreateTypeEnum.REQUEST_RESULT, bulkOperationList, date, null);

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
                Query.of(q -> q.term(qt -> qt.field("toId.keyword").value(currentUserId))));

        SearchResponse<ImFriendRequestDocument> searchResponse = ElasticsearchUtil
            .autoCreateIndexAndSearch(BaseElasticsearchIndexConstant.IM_FRIEND_REQUEST_INDEX,
                s -> s.index(BaseElasticsearchIndexConstant.IM_FRIEND_REQUEST_INDEX).from((current - 1) * pageSize)
                    .size(pageSize).sort(ss -> ss.field(ssf -> ssf.field("createTime").order(SortOrder.Desc)))
                    .query(sq -> sq.bool(sqb -> sqb.should(queryList))), ImFriendRequestDocument.class);

        if (searchResponse == null) {
            return dto.getPage(false);
        }

        List<ImFriendRequestDocument> imFriendRequestDocumentList =
            searchResponse.hits().hits().stream().filter(it -> it.source() != null).map(it -> {
                it.source().setId(it.id());
                return it.source();
            }).collect(Collectors.toList());

        Page<ImFriendRequestDocument> page = dto.getPage(false);

        page.setRecords(imFriendRequestDocumentList);
        if (searchResponse.hits().total() != null) {
            page.setTotal(searchResponse.hits().total().value());
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
            null, null);

        return BaseBizCodeEnum.API_RESULT_SEND_OK.getMsg();
    }

    /**
     * bulkOperationList：为 null，则会执行：elasticsearchClient.bulk，反之 不执行
     */
    @SneakyThrows
    private void doSend(Long createId, String content, String toId, ImToTypeEnum toType,
        ImMessageCreateTypeEnum createType, List<BulkOperation> bulkOperationList, Date date,
        Set<Long> joinGroupUserIdSetTemp) {

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
        imMessageDocument.setHIdSet(new HashSet<>());

        bulkOperationList.add(new BulkOperation.Builder().index(
            i -> i.index(BaseElasticsearchIndexConstant.IM_MESSAGE_INDEX).id(IdUtil.simpleUUID())
                .document(imMessageDocument)).build());

        if (ImToTypeEnum.FRIEND.equals(toType)) {

            doSendAddToBulkOperationList(content, createId, toId, toType, date, bulkOperationList, CollUtil
                .newArrayList(Query.of(q -> q.term(qt -> qt.field("createId").value(createId))),
                    Query.of(q -> q.term(qt -> qt.field("toId.keyword").value(toId))),
                    Query.of(q -> q.term(qt -> qt.field("type").value(toType.getCode())))), false, createType);

            doSendAddToBulkOperationList(content, Convert.toLong(toId), createId.toString(), toType, date,
                bulkOperationList, CollUtil.newArrayList(Query.of(q -> q.term(qt -> qt.field("createId").value(toId))),
                    Query.of(q -> q.term(qt -> qt.field("toId.keyword").value(createId.toString()))),
                    Query.of(q -> q.term(qt -> qt.field("type").value(toType.getCode())))), true, createType);

        } else {
            doSendAddToBulkOperationListForGroup(createId, toId, toType, content, date, bulkOperationList,
                joinGroupUserIdSetTemp, createType);
        }

        if (bulkOperationListNullFlag) {
            List<BulkOperation> finalBulkOperationList = bulkOperationList;
            elasticsearchClient.bulk(b -> b.operations(finalBulkOperationList));
        }
    }

    private void doSendAddToBulkOperationListForGroup(Long createId, String toId, ImToTypeEnum toType, String content,
        Date date, List<BulkOperation> bulkOperationList, Set<Long> joinGroupUserIdSetTemp,
        ImMessageCreateTypeEnum lastContentCreateType) {

        List<Query> queryList = CollUtil.newArrayList(Query.of(q -> q.term(qt -> qt.field("gid.keyword").value(toId))),
            Query.of(q -> q.term(qt -> qt.field("outFlag").value(false))));

        // 获取：加入群组的用户
        SearchResponse<ImGroupJoinDocument> groupJoinDocumentSearchResponse = ElasticsearchUtil
            .autoCreateIndexAndSearch(BaseElasticsearchIndexConstant.IM_GROUP_JOIN_INDEX,
                s -> s.index(BaseElasticsearchIndexConstant.IM_GROUP_JOIN_INDEX)
                    .query(sq -> sq.bool(sqb -> sqb.must(queryList))), ImGroupJoinDocument.class);

        if (groupJoinDocumentSearchResponse == null) {
            return;
        }

        Set<Long> joinGroupUserIdSet = groupJoinDocumentSearchResponse.hits().hits().stream().map(it -> {
            if (it.source() != null) {
                return it.source().getCreateId();
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toSet());

        if (CollUtil.isNotEmpty(joinGroupUserIdSetTemp)) {
            joinGroupUserIdSet.addAll(joinGroupUserIdSetTemp); // 添加额外的：joinGroupUserId
        }

        if (joinGroupUserIdSet.size() == 0) {
            return;
        }

        List<FieldValue> fieldValueList = joinGroupUserIdSet.stream().map(FieldValue::of).collect(Collectors.toList());

        // 获取：会话
        SearchResponse<ImSessionDocument> sessionDocumentSearchResponse =
            ElasticsearchUtil.autoCreateIndexAndSearch(BaseElasticsearchIndexConstant.IM_SESSION_INDEX,
                s -> s.index(BaseElasticsearchIndexConstant.IM_SESSION_INDEX).query(sq -> sq.bool(sqb -> sqb.must(
                    CollUtil.newArrayList(Query.of(q -> q.term(qt -> qt.field("toId.keyword").value(toId))),
                        Query.of(q -> q.term(qt -> qt.field("type").value(toType.getCode()))),
                        Query.of(q -> q.terms(qt -> qt.field("createId").terms(qtt -> qtt.value(fieldValueList)))))))),
                ImSessionDocument.class);

        if (sessionDocumentSearchResponse == null) {
            // 如果全部不存在，则都新增
            doSendAddToBulkOperationListForGroupAddBulkOperationList(createId, toId, toType, content, date,
                bulkOperationList, joinGroupUserIdSet, lastContentCreateType);
        } else {
            // 存在会话的，更新，不存在会话的，新增
            List<ImSessionDocument> imSessionDocumentList =
                sessionDocumentSearchResponse.hits().hits().stream().map(it -> {
                    if (it.source() != null) {
                        it.source().setId(it.id());
                        return it.source();
                    }
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toList());

            for (ImSessionDocument item : imSessionDocumentList) {

                String id = item.getId();
                item.setId(null); // // 取消：id的设置

                item.setUnreadTotal(item.getUnreadTotal() + 1L);
                item.setLastContent(content);
                item.setLastContentCreateTime(date);
                item.setLastContentCreateType(lastContentCreateType);

                // 更新
                bulkOperationList.add(new BulkOperation.Builder().update(
                    u -> u.index(BaseElasticsearchIndexConstant.IM_SESSION_INDEX).id(id).action(ua -> ua.doc(item)))
                    .build());
            }

            Set<Long> createIdSet =
                imSessionDocumentList.stream().map(ImSessionDocument::getCreateId).collect(Collectors.toSet());

            Set<Long> addSessionUserIdSet =
                joinGroupUserIdSet.stream().filter(it -> !createIdSet.contains(it)).collect(Collectors.toSet());

            if (addSessionUserIdSet.size() == 0) {
                return;
            }

            doSendAddToBulkOperationListForGroupAddBulkOperationList(createId, toId, toType, content, date,
                bulkOperationList, addSessionUserIdSet, lastContentCreateType);
        }
    }

    private void doSendAddToBulkOperationListForGroupAddBulkOperationList(Long createId, String toId,
        ImToTypeEnum toType, String content, Date date, List<BulkOperation> bulkOperationList,
        Set<Long> addSessionUserIdSet, ImMessageCreateTypeEnum lastContentCreateType) {

        for (Long item : addSessionUserIdSet) {

            ImSessionDocument imSessionDocument = new ImSessionDocument();
            imSessionDocument.setCreateId(item);
            imSessionDocument.setToId(toId);
            imSessionDocument.setType(toType);
            imSessionDocument.setUnreadTotal(item.equals(createId) ? 0L : 1L);
            imSessionDocument.setLastContent(content);
            imSessionDocument.setLastContentCreateTime(date);
            imSessionDocument.setLastContentCreateType(lastContentCreateType);

            // 新增
            bulkOperationList.add(new BulkOperation.Builder().index(
                i -> i.index(BaseElasticsearchIndexConstant.IM_SESSION_INDEX)
                    .id(ImHelpUtil.getSessionId(toType, item, toId)).document(imSessionDocument)).build());
        }
    }

    private void doSendAddToBulkOperationList(String content, Long createId, String toId, ImToTypeEnum toType,
        Date date, List<BulkOperation> bulkOperationList, List<Query> queryList, boolean addUnreadTotalFlag,
        ImMessageCreateTypeEnum lastContentCreateType) {

        SearchResponse<ImSessionDocument> searchResponse = ElasticsearchUtil
            .autoCreateIndexAndSearch(BaseElasticsearchIndexConstant.IM_SESSION_INDEX,
                s -> s.index(BaseElasticsearchIndexConstant.IM_SESSION_INDEX)
                    .query(sq -> sq.bool(sqb -> sqb.must(queryList))), ImSessionDocument.class);

        Hit<ImSessionDocument> imSessionDocumentHit = ElasticsearchUtil.searchGetSourceHit(searchResponse);

        ImSessionDocument imSessionDocument = null;

        if (imSessionDocumentHit != null) {
            imSessionDocument = imSessionDocumentHit.source();
        }

        if (imSessionDocument == null) {
            imSessionDocument = new ImSessionDocument();
            imSessionDocument.setCreateId(createId);
            imSessionDocument.setToId(toId);
            imSessionDocument.setType(toType);
            imSessionDocument.setUnreadTotal(addUnreadTotalFlag ? 1L : 0L);
            imSessionDocument.setLastContent(content);
            imSessionDocument.setLastContentCreateTime(date);
            imSessionDocument.setLastContentCreateType(lastContentCreateType);

            ImSessionDocument finalImSessionDocument = imSessionDocument;
            bulkOperationList.add(new BulkOperation.Builder().index(
                i -> i.index(BaseElasticsearchIndexConstant.IM_SESSION_INDEX)
                    .id(ImHelpUtil.getSessionId(toType, createId, toId)).document(finalImSessionDocument)).build());
        } else {
            if (addUnreadTotalFlag) {
                imSessionDocument.setUnreadTotal(imSessionDocument.getUnreadTotal() + 1L);
            }
            imSessionDocument.setLastContent(content);
            imSessionDocument.setLastContentCreateTime(date);
            imSessionDocument.setLastContentCreateType(lastContentCreateType);

            ImSessionDocument finalImSessionDocument = imSessionDocument;
            bulkOperationList.add(new BulkOperation.Builder().update(
                u -> u.index(BaseElasticsearchIndexConstant.IM_SESSION_INDEX).id(imSessionDocumentHit.id())
                    .action(ua -> ua.doc(finalImSessionDocument))).build());
        }

    }

    private void sendCheck(ImSendDTO dto, Long currentUserId) {

        if (ImToTypeEnum.FRIEND.equals(dto.getToType())) {

            if (currentUserId.toString().equals(dto.getToId())) {
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
                    Query.of(q -> q.term(qt -> qt.field("gid.keyword").value(dto.getToId()))),
                    Query.of(q -> q.term(qt -> qt.field("outFlag").value(false))));

            long searchTotal = ElasticsearchUtil
                .autoCreateIndexAndGetSearchTotal(BaseElasticsearchIndexConstant.IM_GROUP_JOIN_INDEX,
                    s -> s.index(BaseElasticsearchIndexConstant.IM_GROUP_JOIN_INDEX)
                        .query(sq -> sq.bool(sqb -> sqb.must(queryList))));

            if (searchTotal == 0) {
                ApiResultVO.error("操作失败：您不在群组里，无法发送消息");
            }

            GetResponse<ImGroupDocument> getResponse = ElasticsearchUtil
                .autoCreateIndexAndGet(BaseElasticsearchIndexConstant.IM_GROUP_INDEX,
                    g -> g.index(BaseElasticsearchIndexConstant.IM_GROUP_INDEX).id(dto.getToId()),
                    ImGroupDocument.class);

            if (getResponse.source() == null) {
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
                    .size(pageSize)
                    .sort(ss -> ss.field(ssf -> ssf.field("lastContentCreateTime").order(SortOrder.Desc)))
                    .query(sq -> sq.term(sqt -> sqt.field("createId").value(currentUserId))), ImSessionPageVO.class);

        if (searchResponse == null) {
            return dto.getPage(false);
        }

        List<ImSessionPageVO> imSessionPageVOList =
            searchResponse.hits().hits().stream().filter(it -> it.source() != null).map(it -> {
                it.source().setId(it.id());
                return it.source();
            }).collect(Collectors.toList());

        if (imSessionPageVOList.size() != 0) {

            Set<String> friendIdSet =
                imSessionPageVOList.stream().filter(it -> ImToTypeEnum.FRIEND.equals(it.getType()))
                    .map(ImSessionDocument::getToId).collect(Collectors.toSet());

            Map<String, SysUserDO> friendGroupMap = null;

            if (friendIdSet.size() != 0) {
                List<SysUserDO> sysUserDOList =
                    ChainWrappers.lambdaQueryChain(sysUserMapper).in(BaseEntityTwo::getId, friendIdSet)
                        .select(SysUserDO::getNickname, SysUserDO::getAvatarUrl, BaseEntityTwo::getId).list();
                friendGroupMap =
                    sysUserDOList.stream().collect(Collectors.toMap(it -> it.getId().toString(), it -> it));
            }

            List<String> groupIdStrList =
                imSessionPageVOList.stream().filter(it -> ImToTypeEnum.GROUP.equals(it.getType()))
                    .map(ImSessionDocument::getToId).collect(Collectors.toList());

            MgetResponse<ImGroupDocument> mgetResponse = ElasticsearchUtil
                .autoCreateIndexAndMget(BaseElasticsearchIndexConstant.IM_GROUP_INDEX,
                    g -> g.index(BaseElasticsearchIndexConstant.IM_GROUP_INDEX).ids(groupIdStrList),
                    ImGroupDocument.class);

            Map<String, ImGroupDocument> groupGroupMap = null;
            if (mgetResponse != null) {
                List<MultiGetResponseItem<ImGroupDocument>> docList = mgetResponse.docs();

                groupGroupMap = MapUtil.newHashMap(docList.size());

                for (MultiGetResponseItem<ImGroupDocument> item : docList) {
                    GetResult<ImGroupDocument> result = item.result();
                    groupGroupMap.put(result.id(), result.source());
                }
            }

            Map<String, SysUserDO> finalFriendGroupMap = friendGroupMap;
            Map<String, ImGroupDocument> finalGroupGroupMap = groupGroupMap;
            imSessionPageVOList.forEach(item -> {
                if (ImToTypeEnum.FRIEND.equals(item.getType())) {
                    if (finalFriendGroupMap != null) {
                        SysUserDO sysUserDO = finalFriendGroupMap.get(item.getToId());
                        if (sysUserDO != null) {
                            item.setTargetName(sysUserDO.getNickname());
                            item.setTargetAvatarUrl(sysUserDO.getAvatarUrl());
                        }
                    }
                } else {
                    if (finalGroupGroupMap != null) {
                        ImGroupDocument imGroupDocument = finalGroupGroupMap.get(item.getToId());
                        if (imGroupDocument != null) {
                            item.setTargetName(imGroupDocument.getName());
                            item.setTargetAvatarUrl(imGroupDocument.getAvatarUrl());
                        }
                    }
                }
            });
        }

        Page<ImSessionPageVO> page = dto.getPage(false);

        page.setRecords(imSessionPageVOList);
        if (searchResponse.hits().total() != null) {
            page.setTotal(searchResponse.hits().total().value());
        }

        return page;
    }

    /**
     * 聊天记录：分页排序查询
     */
    @Override
    public Page<ImMessageDocument> messagePage(ImMessagePageDTO dto) {

        Integer current = Convert.toInt(dto.getCurrent());
        Integer pageSize = Convert.toInt(dto.getPageSize());

        if (current == null || pageSize == null) {
            return dto.getPage(false);
        }

        Long currentUserId = UserUtil.getCurrentUserId();

        if (currentUserId.toString().equals(dto.getToId())) {
            return dto.getPage(false);
        }

        List<Query> queryList;

        if (ImToTypeEnum.FRIEND.equals(dto.getToType())) {

            List<FieldValue> fieldValueList =
                CollUtil.newArrayList(FieldValue.of(dto.getToId()), FieldValue.of(currentUserId));

            queryList = CollUtil.newArrayList(
                Query.of(q -> q.terms(qt -> qt.field("createId").terms(qtt -> qtt.value(fieldValueList)))),
                Query.of(q -> q.terms(qt -> qt.field("toId.keyword").terms(qtt -> qtt.value(fieldValueList)))),
                Query.of(q -> q.term(qt -> qt.field("toType").value(dto.getToType().getCode()))));

        } else {

            // 获取：加入群组的时间
            GetResponse<ImGroupJoinDocument> getResponse = ElasticsearchUtil
                .autoCreateIndexAndGet(BaseElasticsearchIndexConstant.IM_GROUP_JOIN_INDEX,
                    g -> g.index(BaseElasticsearchIndexConstant.IM_GROUP_JOIN_INDEX)
                        .id(ImHelpUtil.getGroupJoinId(currentUserId, dto.getToId())), ImGroupJoinDocument.class);

            ImGroupJoinDocument imGroupJoinDocument = getResponse.source();
            if (imGroupJoinDocument == null) {
                ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);
            }

            Date createTime = imGroupJoinDocument.getCreateTime();
            Date outTime = imGroupJoinDocument.getOutTime();

            queryList = CollUtil
                .newArrayList(Query.of(q -> q.term(qt -> qt.field("toId.keyword").value(dto.getToId()))),
                    Query.of(q -> q.term(qt -> qt.field("toType").value(dto.getToType().getCode()))), Query.of(q -> q
                        .range(qr -> qr.field("createTime").gte(JsonData.of(createTime))
                            .lte(outTime == null ? null : JsonData.of(outTime)))));
        }

        SearchResponse<ImMessageDocument> searchResponse = ElasticsearchUtil
            .autoCreateIndexAndSearch(BaseElasticsearchIndexConstant.IM_MESSAGE_INDEX,
                s -> s.index(BaseElasticsearchIndexConstant.IM_MESSAGE_INDEX).from((current - 1) * pageSize)
                    .size(pageSize).sort(ss -> ss.field(ssf -> ssf.field("createTime").order(SortOrder.Asc)))
                    .query(sq -> sq.bool(sqb -> sqb.must(queryList) //
                        .mustNot(sqbm -> sqbm.term(sqbmt -> sqbmt.field("hIdSet").value(currentUserId)) // 不显示，对自己隐藏的的消息
                        ))), ImMessageDocument.class);

        if (searchResponse == null) {
            return dto.getPage(false);
        }

        List<ImMessageDocument> imMessageDocumentList =
            searchResponse.hits().hits().stream().filter(it -> it.source() != null).map(it -> {
                it.source().setId(it.id());
                return it.source();
            }).collect(Collectors.toList());

        Page<ImMessageDocument> page = dto.getPage(false);

        page.setRecords(imMessageDocumentList);
        if (searchResponse.hits().total() != null) {
            page.setTotal(searchResponse.hits().total().value());
        }

        // 已读消息：扣除未读消息数量
        ThreadUtil.execute(() -> messageRead(imMessageDocumentList, currentUserId, dto.getToType(), dto.getToId()));

        return page;
    }

    @SneakyThrows
    private void messageRead(List<ImMessageDocument> imMessageDocumentList, Long currentUserId, ImToTypeEnum toType,
        String toId) {

        if (imMessageDocumentList.size() == 0) {
            return;
        }

        List<BulkOperation> bulkOperationList = new ArrayList<>();

        long unreadTotal = 0; // 待扣除的，未读消息数量

        for (ImMessageDocument item : imMessageDocumentList) {

            if (!currentUserId.equals(item.getCreateId()) && !item.getRIdSet().contains(currentUserId)) {

                // 属性拷贝
                ImMessageDocument imMessageDocument = BeanUtil.copyProperties(item, ImMessageDocument.class);

                imMessageDocument.setId(null); // 取消：id的设置
                imMessageDocument.getRIdSet().add(currentUserId);
                unreadTotal++;

                // 增加：此条消息已读数量
                bulkOperationList.add(new BulkOperation.Builder().update(
                    u -> u.index(BaseElasticsearchIndexConstant.IM_MESSAGE_INDEX).id(item.getId())
                        .action(ua -> ua.doc(imMessageDocument))).build());
            }
        }

        if (unreadTotal != 0) {
            GetResponse<ImSessionDocument> getResponse = ElasticsearchUtil
                .autoCreateIndexAndGet(BaseElasticsearchIndexConstant.IM_SESSION_INDEX,
                    g -> g.index(BaseElasticsearchIndexConstant.IM_SESSION_INDEX)
                        .id(ImHelpUtil.getSessionId(toType, currentUserId, toId)), ImSessionDocument.class);

            ImSessionDocument imSessionDocument = getResponse.source();

            if (imSessionDocument != null) {

                unreadTotal = imSessionDocument.getUnreadTotal() - unreadTotal;

                imSessionDocument.setUnreadTotal(unreadTotal < 0 ? 0 : unreadTotal);

                // 减少：未读数量
                bulkOperationList.add(new BulkOperation.Builder().update(
                    u -> u.index(BaseElasticsearchIndexConstant.IM_SESSION_INDEX).id(getResponse.id())
                        .action(ua -> ua.doc(imSessionDocument))).build());
            }

        }

        elasticsearchClient.bulk(b -> b.operations(bulkOperationList));
    }

    /**
     * 新增/修改 群组
     */
    @SneakyThrows
    @Override
    public String insertOrUpdateGroup(ImInsertOrUpdateGroupDTO dto) {

        Long currentUserId = UserUtil.getCurrentUserId();
        Date date = new Date();

        ImGroupDocument imGroupDocument = new ImGroupDocument();
        imGroupDocument.setName(MyEntityUtil.getNotNullStr(dto.getName()));
        imGroupDocument.setAvatarUrl(MyEntityUtil.getNotNullStr(dto.getAvatarUrl()));

        if (StrUtil.isBlank(dto.getId())) {

            imGroupDocument.setCreateId(currentUserId);
            imGroupDocument.setCreateTime(date);

            String uuid = IdUtil.simpleUUID();

            List<BulkOperation> bulkOperationList = new ArrayList<>();

            bulkOperationList.add(new BulkOperation.Builder()
                .index(i -> i.index(BaseElasticsearchIndexConstant.IM_GROUP_INDEX).id(uuid).document(imGroupDocument))
                .build());

            ImGroupJoinDocument imGroupJoinDocument = new ImGroupJoinDocument();
            imGroupJoinDocument.setCreateId(currentUserId);
            imGroupJoinDocument.setCreateTime(date);
            imGroupJoinDocument.setGid(uuid);
            imGroupJoinDocument.setRemark("");
            imGroupJoinDocument.setRole(ImGroupJoinRoleEnum.CREATOR);
            imGroupJoinDocument.setOutFlag(false);
            imGroupJoinDocument.setOutTime(null);

            bulkOperationList.add(new BulkOperation.Builder().index(
                i -> i.index(BaseElasticsearchIndexConstant.IM_GROUP_JOIN_INDEX)
                    .id(ImHelpUtil.getGroupJoinId(currentUserId, uuid)).document(imGroupJoinDocument)).build());

            doSend(BaseConstant.SYS_ID, "", uuid, ImToTypeEnum.GROUP, ImMessageCreateTypeEnum.CREATE_COMPLETE,
                bulkOperationList, date, CollUtil.newHashSet(currentUserId));

            elasticsearchClient.bulk(b -> b.operations(bulkOperationList));

        } else {

            // 判断是否是管理员级别的用户
            if (!groupManagerFlag(dto.getId(), currentUserId)) {
                ApiResultVO.error(BaseBizCodeEnum.INSUFFICIENT_PERMISSIONS);
            }

            elasticsearchClient.update(
                u -> u.index(BaseElasticsearchIndexConstant.IM_GROUP_INDEX).id(dto.getId()).doc(imGroupDocument),
                ImGroupDocument.class);
        }

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 群组申请：发送
     */
    @SneakyThrows
    @Override
    public String groupRequest(ImGroupRequestDTO dto) {

        Long currentUserId = UserUtil.getCurrentUserId();

        List<Query> queryList = CollUtil
            .newArrayList(Query.of(q -> q.term(qt -> qt.field("createId").value(currentUserId))),
                Query.of(q -> q.term(qt -> qt.field("gid.keyword").value(dto.getGid()))),
                Query.of(q -> q.term(qt -> qt.field("outFlag").value(false))));

        long searchTotal = ElasticsearchUtil
            .autoCreateIndexAndGetSearchTotal(BaseElasticsearchIndexConstant.IM_GROUP_JOIN_INDEX,
                s -> s.index(BaseElasticsearchIndexConstant.IM_GROUP_JOIN_INDEX)
                    .query(sq -> sq.bool(sqb -> sqb.must(queryList))));

        if (searchTotal > 0) {
            ApiResultVO.error("操作失败：您已加入该群，请勿重复添加");
        }

        GetResponse<ImGroupDocument> getResponse = ElasticsearchUtil
            .autoCreateIndexAndGet(BaseElasticsearchIndexConstant.IM_GROUP_INDEX,
                g -> g.index(BaseElasticsearchIndexConstant.IM_GROUP_INDEX).id(dto.getGid()), ImGroupDocument.class);

        if (getResponse.source() == null) {
            ApiResultVO.error("操作失败：加入的群不存在，请刷新重试");
        }

        Date date = new Date();

        ImGroupRequestDocument imGroupRequestDocument = new ImGroupRequestDocument();

        imGroupRequestDocument.setContent(MyEntityUtil.getNotNullStr(dto.getContent()));
        imGroupRequestDocument.setGid(dto.getGid());
        imGroupRequestDocument.setCreateId(currentUserId);
        imGroupRequestDocument.setCreateTime(date);
        imGroupRequestDocument.setResultId(BaseConstant.SYS_ID);
        imGroupRequestDocument.setResult(ImRequestResultEnum.PENDING);
        imGroupRequestDocument.setResultTime(date);

        elasticsearchClient.index(i -> i.index(BaseElasticsearchIndexConstant.IM_GROUP_REQUEST_INDEX)
            .id(ImHelpUtil.getGroupRequestId(currentUserId, dto.getGid())).document(imGroupRequestDocument));

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 群组申请：处理
     */
    @SneakyThrows
    @Override
    public String groupRequestHandler(ImGroupRequestHandlerDTO dto) {

        if (ImRequestResultEnum.PENDING.equals(dto.getResult())) {
            ApiResultVO.error(BaseBizCodeEnum.PARAMETER_CHECK_ERROR);
        }

        GetResponse<ImGroupRequestDocument> getResponse = ElasticsearchUtil
            .autoCreateIndexAndGet(BaseElasticsearchIndexConstant.IM_GROUP_REQUEST_INDEX,
                g -> g.index(BaseElasticsearchIndexConstant.IM_GROUP_REQUEST_INDEX).id(dto.getId()),
                ImGroupRequestDocument.class);

        ImGroupRequestDocument imGroupRequestDocument = getResponse.source();

        if (imGroupRequestDocument == null) {
            ApiResultVO.error("操作失败：群组申请不存在，请刷新重试");
        }

        Long currentUserId = UserUtil.getCurrentUserId();

        if (!groupManagerFlag(imGroupRequestDocument.getGid(), currentUserId)) {
            ApiResultVO.error(BaseBizCodeEnum.INSUFFICIENT_PERMISSIONS);
        }

        if (!ImRequestResultEnum.PENDING.equals(imGroupRequestDocument.getResult())) {
            ApiResultVO.error("操作失败：已经处理过了，请刷新重试");
        }

        Date date = new Date();

        imGroupRequestDocument.setResultId(currentUserId);
        imGroupRequestDocument.setResult(dto.getResult());
        imGroupRequestDocument.setResultTime(date);

        List<BulkOperation> bulkOperationList = new ArrayList<>();

        bulkOperationList.add(new BulkOperation.Builder().update(
            u -> u.index(BaseElasticsearchIndexConstant.IM_GROUP_REQUEST_INDEX).id(dto.getId())
                .action(ua -> ua.doc(imGroupRequestDocument))).build());

        if (ImRequestResultEnum.AGREED.equals(dto.getResult())) {

            ImGroupJoinDocument imGroupJoinDocument = new ImGroupJoinDocument();
            imGroupJoinDocument.setCreateId(imGroupRequestDocument.getCreateId());
            imGroupJoinDocument.setCreateTime(date);
            imGroupJoinDocument.setGid(imGroupRequestDocument.getGid());
            imGroupJoinDocument.setRemark("");
            imGroupJoinDocument.setRole(ImGroupJoinRoleEnum.USER);
            imGroupJoinDocument.setOutFlag(false);
            imGroupJoinDocument.setOutTime(null);

            bulkOperationList.add(new BulkOperation.Builder().index(
                i -> i.index(BaseElasticsearchIndexConstant.IM_GROUP_JOIN_INDEX).id(ImHelpUtil
                    .getGroupJoinId(imGroupRequestDocument.getCreateId(), imGroupRequestDocument.getGid()))
                    .document(imGroupJoinDocument)).build());

            doSend(BaseConstant.SYS_ID, "", imGroupRequestDocument.getGid(), ImToTypeEnum.GROUP,
                ImMessageCreateTypeEnum.REQUEST_RESULT, bulkOperationList, date,
                CollUtil.newHashSet(imGroupRequestDocument.getCreateId()));
        }

        elasticsearchClient.bulk(b -> b.operations(bulkOperationList));

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 群组申请：分页排序查询
     */
    @Override
    public Page<ImGroupRequestDocument> groupRequestPage(ImGroupRequestPageDTO dto) {

        Integer current = Convert.toInt(dto.getCurrent());
        Integer pageSize = Convert.toInt(dto.getPageSize());

        if (current == null || pageSize == null) {
            return dto.getPage(false);
        }

        Long currentUserId = UserUtil.getCurrentUserId();

        List<Query> queryList;

        if (StrUtil.isNotBlank(dto.getGid())) {

            // 如果是：群组管理员级别，在查看入群申请

            // 判断是不是管理员
            if (!groupManagerFlag(dto.getGid(), currentUserId)) {
                ApiResultVO.error(BaseBizCodeEnum.INSUFFICIENT_PERMISSIONS);
            }

            queryList = CollUtil.newArrayList(Query.of(q -> q.term(qt -> qt.field("gid.keyword").value(dto.getGid()))));

        } else {
            queryList = CollUtil.newArrayList(Query.of(q -> q.term(qt -> qt.field("createId").value(currentUserId))));
        }

        List<Query> finalQueryList = queryList;
        SearchResponse<ImGroupRequestDocument> searchResponse = ElasticsearchUtil
            .autoCreateIndexAndSearch(BaseElasticsearchIndexConstant.IM_GROUP_REQUEST_INDEX,
                s -> s.index(BaseElasticsearchIndexConstant.IM_GROUP_REQUEST_INDEX).from((current - 1) * pageSize)
                    .size(pageSize).sort(ss -> ss.field(ssf -> ssf.field("createTime").order(SortOrder.Desc)))
                    .query(sq -> sq.bool(sqb -> sqb.should(finalQueryList))), ImGroupRequestDocument.class);

        if (searchResponse == null) {
            return dto.getPage(false);
        }

        List<ImGroupRequestDocument> imGroupRequestDocumentList =
            searchResponse.hits().hits().stream().filter(it -> it.source() != null).map(it -> {
                it.source().setId(it.id());
                return it.source();
            }).collect(Collectors.toList());

        Page<ImGroupRequestDocument> page = dto.getPage(false);

        page.setRecords(imGroupRequestDocumentList);
        if (searchResponse.hits().total() != null) {
            page.setTotal(searchResponse.hits().total().value());
        }

        return page;
    }

    /**
     * 获取：用户是不是 该群里的管理员
     */
    private boolean groupManagerFlag(String gid, Long currentUserId) {

        List<Query> queryList = CollUtil
            .newArrayList(Query.of(q -> q.term(qt -> qt.field("createId").value(currentUserId))),
                Query.of(q -> q.term(qt -> qt.field("gid.keyword").value(gid))),
                Query.of(q -> q.term(qt -> qt.field("outFlag").value(false))), Query.of(q -> q.terms(
                    qt -> qt.field("role").terms(qtt -> qtt.value(CollUtil
                        .newArrayList(FieldValue.of(ImGroupJoinRoleEnum.CREATOR.getCode()),
                            FieldValue.of(ImGroupJoinRoleEnum.MANAGER.getCode())))))));

        return ElasticsearchUtil.autoCreateIndexAndGetSearchTotal(BaseElasticsearchIndexConstant.IM_GROUP_JOIN_INDEX,
            s -> s.index(BaseElasticsearchIndexConstant.IM_GROUP_JOIN_INDEX)
                .query(sq -> sq.bool(sqb -> sqb.must(queryList)))) > 0;
    }

    /**
     * 会话：批量删除
     */
    @SneakyThrows
    @Override
    public String sessionDeleteByIdSet(NotEmptyStrIdSet notEmptyStrIdSet) {

        Long currentUserId = UserUtil.getCurrentUserId();

        List<Query> queryList = CollUtil
            .newArrayList(Query.of(q -> q.term(qt -> qt.field("createId").value(currentUserId))),
                Query.of(q -> q.ids(qi -> qi.values(CollUtil.newArrayList(notEmptyStrIdSet.getIdSet())))));

        elasticsearchClient.deleteByQuery(d -> d.index(BaseElasticsearchIndexConstant.IM_SESSION_INDEX)
            .query(dq -> dq.bool(dqb -> dqb.must(queryList))));

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 聊天记录：批量删除
     */
    @Override
    public String messageDeleteByIdSet(NotEmptyStrIdSet notEmptyStrIdSet) {

        // TODO：

        return null;
    }

    /**
     * 好友：批量删除
     */
    @Override
    public String friendDeleteByIdSet(NotEmptyStrIdSet notEmptyStrIdSet) {

        // TODO：

        return null;
    }

    /**
     * 群组：批量退出
     */
    @Override
    public String groupOutByIdSet(NotEmptyStrIdSet notEmptyStrIdSet) {

        // TODO：

        return null;
    }

    /**
     * 群组：解散
     */
    @Override
    public String groupDeleteByIdSet(NotEmptyStrIdSet notEmptyStrIdSet) {

        // TODO：

        return null;
    }

    /**
     * 好友：分页排序查询
     */
    @Override
    public Page<ImFriendPageVO> friendPage(ImFriendPageDTO dto) {

        Integer current = Convert.toInt(dto.getCurrent());
        Integer pageSize = Convert.toInt(dto.getPageSize());

        if (current == null || pageSize == null) {
            return dto.getPage(false);
        }

        Long currentUserId = UserUtil.getCurrentUserId();

        SearchResponse<ImFriendPageVO> searchResponse = ElasticsearchUtil
            .autoCreateIndexAndSearch(BaseElasticsearchIndexConstant.IM_FRIEND_INDEX,
                s -> s.index(BaseElasticsearchIndexConstant.IM_FRIEND_INDEX).from((current - 1) * pageSize)
                    .size(pageSize).sort(ss -> ss.field(ssf -> ssf.field("createTime").order(SortOrder.Desc)))
                    .query(sq -> sq.term(sqt -> sqt.field("createId").value(currentUserId))), ImFriendPageVO.class);

        if (searchResponse == null) {
            return dto.getPage(false);
        }

        List<ImFriendPageVO> imFriendPageVOList =
            searchResponse.hits().hits().stream().filter(it -> it.source() != null).map(it -> {
                it.source().setId(it.id());
                return it.source();
            }).collect(Collectors.toList());

        Page<ImFriendPageVO> page = dto.getPage(false);

        page.setRecords(imFriendPageVOList);
        if (searchResponse.hits().total() != null) {
            page.setTotal(searchResponse.hits().total().value());
        }

        if (imFriendPageVOList.size() != 0) {

            Set<Long> uidSet = imFriendPageVOList.stream().map(ImFriendDocument::getUid).collect(Collectors.toSet());

            List<SysUserDO> sysUserDOList =
                ChainWrappers.lambdaQueryChain(sysUserMapper).in(BaseEntityTwo::getId, uidSet)
                    .select(SysUserDO::getNickname, SysUserDO::getAvatarUrl, BaseEntityTwo::getId).list();

            Map<Long, SysUserDO> userGroupMap =
                sysUserDOList.stream().collect(Collectors.toMap(BaseEntityTwo::getId, it -> it));

            imFriendPageVOList.forEach(item -> {
                SysUserDO sysUserDO = userGroupMap.get(item.getUid());
                if (sysUserDO != null) {
                    item.setTargetName(sysUserDO.getNickname());
                    item.setTargetAvatarUrl(sysUserDO.getAvatarUrl());
                }
            });
        }

        return page;
    }

    /**
     * 群组：分页排序查询
     */
    @Override
    public Page<ImGroupDocument> groupPage(ImGroupPageDTO dto) {

        Integer current = Convert.toInt(dto.getCurrent());
        Integer pageSize = Convert.toInt(dto.getPageSize());

        if (current == null || pageSize == null) {
            return dto.getPage(false);
        }

        Long currentUserId = UserUtil.getCurrentUserId();

        List<Query> queryList = CollUtil
            .newArrayList(Query.of(q -> q.term(qt -> qt.field("createId").value(currentUserId))),
                Query.of(q -> q.term(qt -> qt.field("outFlag").value(false))));

        SearchResponse<ImGroupJoinDocument> searchResponse = ElasticsearchUtil
            .autoCreateIndexAndSearch(BaseElasticsearchIndexConstant.IM_GROUP_JOIN_INDEX,
                s -> s.index(BaseElasticsearchIndexConstant.IM_GROUP_JOIN_INDEX).from((current - 1) * pageSize)
                    .size(pageSize).sort(ss -> ss.field(ssf -> ssf.field("createTime").order(SortOrder.Desc)))
                    .query(sq -> sq.bool(sqb -> sqb.must(queryList))), ImGroupJoinDocument.class);

        if (searchResponse == null) {
            return dto.getPage(false);
        }

        List<ImGroupJoinDocument> imGroupJoinDocumentList =
            searchResponse.hits().hits().stream().filter(it -> it.source() != null).map(it -> {
                it.source().setId(it.id());
                return it.source();
            }).collect(Collectors.toList());

        if (imGroupJoinDocumentList.size() == 0) {
            return dto.getPage(false);
        }

        List<String> gidList =
            imGroupJoinDocumentList.stream().map(ImGroupJoinDocument::getGid).collect(Collectors.toList());

        MgetResponse<ImGroupDocument> mgetResponse = ElasticsearchUtil
            .autoCreateIndexAndMget(BaseElasticsearchIndexConstant.IM_GROUP_INDEX,
                g -> g.index(BaseElasticsearchIndexConstant.IM_GROUP_INDEX).ids(gidList), ImGroupDocument.class);

        List<ImGroupDocument> imGroupDocumentList = new ArrayList<>();

        if (mgetResponse != null) {

            Map<String, ImGroupDocument> groupMap =
                mgetResponse.docs().stream().filter(it -> it.result().source() != null).map(it -> {
                    it.result().source().setId(it.result().id());
                    return it.result().source();
                }).collect(Collectors.toMap(ImGroupDocument::getId, it -> it));

            imGroupDocumentList = imGroupJoinDocumentList.stream().map(item -> {
                ImGroupDocument imGroupDocument = groupMap.get(item.getGid());
                if (imGroupDocument != null) {
                    return imGroupDocument;
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toList());
        }

        Page<ImGroupDocument> page = dto.getPage(false);

        page.setRecords(imGroupDocumentList);
        if (searchResponse.hits().total() != null) {
            page.setTotal(searchResponse.hits().total().value());
        }

        return page;
    }

}
