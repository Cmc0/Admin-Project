package com.admin.im.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.model.constant.BaseElasticsearchIndexConstant;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.common.util.UserUtil;
import com.admin.im.model.document.ImElasticsearchBaseDocument;
import com.admin.im.model.document.ImElasticsearchMsgDocument;
import com.admin.im.model.dto.ImPageDTO;
import com.admin.im.model.dto.ImSendDTO;
import com.admin.im.model.enums.ImContentTypeEnum;
import com.admin.im.model.enums.ImToTypeEnum;
import com.admin.im.model.vo.ImPageVO;
import com.admin.im.service.ImService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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

        elasticsearchClient.index(i -> i.index(BaseElasticsearchIndexConstant.IM_MSG_INDEX).id(currentUserId.toString())
            .document(imElasticsearchMsgDocument));
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

        GetResponse<ImElasticsearchBaseDocument> imElasticsearchBaseDocumentGetResponse = elasticsearchClient
            .get(i -> i.index(BaseElasticsearchIndexConstant.IM_BASE_INDEX).id(currentUserId.toString()),
                ImElasticsearchBaseDocument.class);

        ImElasticsearchBaseDocument imElasticsearchBaseDocument = imElasticsearchBaseDocumentGetResponse.source();

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
                i -> i.index(BaseElasticsearchIndexConstant.IM_BASE_INDEX).id(finalImElasticsearchBaseDocument.get_id())
                    .doc(finalImElasticsearchBaseDocument), ImElasticsearchBaseDocument.class);
        }

        return imToTypeEnum;
    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<ImPageVO> myPage(ImPageDTO dto) {
        return null;
    }

}
