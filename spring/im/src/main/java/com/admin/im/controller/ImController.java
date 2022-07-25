package com.admin.im.controller;

import com.admin.common.model.vo.ApiResultVO;
import com.admin.im.model.document.ImFriendRequestDocument;
import com.admin.im.model.dto.ImFriendRequestDTO;
import com.admin.im.model.dto.ImFriendRequestHandlerDTO;
import com.admin.im.model.dto.ImFriendRequestPageDTO;
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

}
