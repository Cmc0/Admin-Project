package com.admin.im.controller;

import com.admin.common.model.vo.ApiResultVO;
import com.admin.im.model.dto.ImFriendRequestDTO;
import com.admin.im.service.ImService;
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

    @ApiOperation(value = "发送好友申请")
    @PostMapping(value = "/friendRequest")
    public ApiResultVO<String> friendRequest(@RequestBody @Valid ImFriendRequestDTO dto) {
        return ApiResultVO.ok(baseService.friendRequest(dto));
    }

}
