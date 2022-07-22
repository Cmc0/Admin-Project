package com.admin.im.controller;

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
    @ApiOperation(value = "分页排序查询：即时通讯会话，备注：暂时不支持分页")
    public ApiResultVO<Page<ImSessionPageVO>> sessionPage(@RequestBody @Valid ImSessionPageDTO dto) {
        return ApiResultVO.ok(baseService.sessionPage(dto));
    }

    @PostMapping(value = "/contentPage")
    @ApiOperation(value = "分页排序查询：即时通讯内容")
    public ApiResultVO<Page<ImContentPageVO>> contentPage(@RequestBody @Valid ImContentPageDTO dto) {
        return ApiResultVO.ok(baseService.contentPage(dto));
    }

    @PostMapping(value = "/friendRequest")
    @ApiOperation(value = "好友申请")
    public ApiResultVO<String> friendRequest(@RequestBody @Valid ImFriendRequestDTO dto) {
        return ApiResultVO.ok(baseService.friendRequest(dto));
    }

    @PostMapping(value = "/friendRequestPage")
    @ApiOperation(value = "分页排序查询：好友申请，备注：包含我的申请，以及对我的申请")
    public ApiResultVO<Page<ImFriendRequestPageVO>> friendRequestPage(@RequestBody @Valid ImFriendRequestPageDTO dto) {
        return ApiResultVO.ok(baseService.friendRequestPage(dto));
    }

}
