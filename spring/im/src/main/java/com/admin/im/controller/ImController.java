package com.admin.im.controller;

import com.admin.common.model.vo.ApiResultVO;
import com.admin.im.model.document.ImFriendRequestDocument;
import com.admin.im.model.document.ImGroupRequestDocument;
import com.admin.im.model.document.ImMessageDocument;
import com.admin.im.model.dto.*;
import com.admin.im.model.vo.ImSessionPageVO;
import com.admin.im.service.ImService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/im")
@Api(tags = "即时通讯-管理")
public class ImController {

    @Resource
    ImService baseService;

    @ApiOperation(value = "好友申请：发送")
    @PostMapping(value = "/friendRequest")
    public ApiResultVO<String> friendRequest(@RequestBody @Valid ImFriendRequestDTO dto) {
        return ApiResultVO.ok(baseService.friendRequest(dto));
    }

    @ApiOperation(value = "好友申请：处理")
    @PostMapping(value = "/friendRequestHandler")
    public ApiResultVO<String> friendRequestHandler(@RequestBody @Valid ImFriendRequestHandlerDTO dto) {
        return ApiResultVO.ok(baseService.friendRequestHandler(dto));
    }

    @ApiOperation(value = "好友申请：分页排序查询")
    @PostMapping(value = "/friendRequestPage")
    public ApiResultVO<Page<ImFriendRequestDocument>> friendRequestPage(
        @RequestBody @Valid ImFriendRequestPageDTO dto) {
        return ApiResultVO.ok(baseService.friendRequestPage(dto));
    }

    @ApiOperation(value = "发送消息")
    @PostMapping(value = "/send")
    public ApiResultVO<String> send(@RequestBody @Valid ImSendDTO dto) {
        return ApiResultVO.ok(baseService.send(dto));
    }

    @ApiOperation(value = "会话：分页排序查询")
    @PostMapping(value = "/sessionPage")
    public ApiResultVO<Page<ImSessionPageVO>> sessionPage(@RequestBody @Valid ImSessionPageDTO dto) {
        return ApiResultVO.ok(baseService.sessionPage(dto));
    }

    @ApiOperation(value = "聊天记录：分页排序查询")
    @PostMapping(value = "/messagePage")
    public ApiResultVO<Page<ImMessageDocument>> messagePage(@RequestBody @Valid ImMessagePageDTO dto) {
        return ApiResultVO.ok(baseService.messagePage(dto));
    }

    @ApiOperation(value = "新增/修改 群组")
    @PostMapping(value = "/insertOrUpdateGroup")
    public ApiResultVO<String> insertOrUpdateGroup(@RequestBody @Valid ImInsertOrUpdateGroupDTO dto) {
        return ApiResultVO.ok(baseService.insertOrUpdateGroup(dto));
    }

    @ApiOperation(value = "群组申请：发送")
    @PostMapping(value = "/groupRequest")
    public ApiResultVO<String> groupRequest(@RequestBody @Valid ImGroupRequestDTO dto) {
        return ApiResultVO.ok(baseService.groupRequest(dto));
    }

    @ApiOperation(value = "群组申请：处理")
    @PostMapping(value = "/groupRequestHandler")
    public ApiResultVO<String> groupRequestHandler(@RequestBody @Valid ImGroupRequestHandlerDTO dto) {
        return ApiResultVO.ok(baseService.groupRequestHandler(dto));
    }

    @ApiOperation(value = "群组申请：分页排序查询")
    @PostMapping(value = "/groupRequestPage")
    public ApiResultVO<Page<ImGroupRequestDocument>> friendRequestPage(@RequestBody @Valid ImGroupRequestPageDTO dto) {
        return ApiResultVO.ok(baseService.groupRequestPage(dto));
    }

}
