package com.admin.im.controller;

import com.admin.common.model.entity.SysUserDO;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.im.model.dto.*;
import com.admin.im.model.vo.ImContentPageVO;
import com.admin.im.model.vo.ImFriendRequestPageVO;
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

    @PostMapping(value = "/send")
    @ApiOperation(value = "发送消息")
    public ApiResultVO<String> send(@RequestBody @Valid ImSendDTO dto) {
        return ApiResultVO.ok(baseService.send(dto));
    }

    @PostMapping(value = "/sessionPage")
    @ApiOperation(value = "即时通讯会话，分页排序查询，备注：暂时不支持分页")
    public ApiResultVO<Page<ImSessionPageVO>> sessionPage(@RequestBody @Valid ImSessionPageDTO dto) {
        return ApiResultVO.ok(baseService.sessionPage(dto));
    }

    @PostMapping(value = "/contentPage")
    @ApiOperation(value = "即时通讯内容，分页排序查询")
    public ApiResultVO<Page<ImContentPageVO>> contentPage(@RequestBody @Valid ImContentPageDTO dto) {
        return ApiResultVO.ok(baseService.contentPage(dto));
    }

    @PostMapping(value = "/friendRequest")
    @ApiOperation(value = "好友申请")
    public ApiResultVO<String> friendRequest(@RequestBody @Valid ImFriendRequestDTO dto) {
        return ApiResultVO.ok(baseService.friendRequest(dto));
    }

    @PostMapping(value = "/friendRequestPage")
    @ApiOperation(value = "好友申请，分页排序查询，备注：包含我的申请，以及对我的申请")
    public ApiResultVO<Page<ImFriendRequestPageVO>> friendRequestPage(@RequestBody @Valid ImFriendRequestPageDTO dto) {
        return ApiResultVO.ok(baseService.friendRequestPage(dto));
    }

    @PostMapping(value = "/friendRequestHandler")
    @ApiOperation(value = "好友申请，结果处理")
    public ApiResultVO<String> friendRequestHandler(@RequestBody @Valid ImFriendRequestHandlerDTO dto) {
        return ApiResultVO.ok(baseService.friendRequestHandler(dto));
    }

    @PostMapping(value = "/contactPage")
    @ApiOperation(value = "联系人，分页排序查询")
    public ApiResultVO<Page<SysUserDO>> contactPage(@RequestBody @Valid ImContactPageDTO dto) {
        return ApiResultVO.ok(baseService.contactPage(dto));
    }

}
