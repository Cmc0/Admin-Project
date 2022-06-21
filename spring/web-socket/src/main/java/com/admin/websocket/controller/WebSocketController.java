package com.admin.websocket.controller;

import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullByte;
import com.admin.common.model.dto.NotNullByteAndId;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.websocket.model.dto.WebSocketPageDTO;
import com.admin.websocket.model.vo.WebSocketPageVO;
import com.admin.websocket.model.vo.WebSocketRegisterVO;
import com.admin.websocket.service.WebSocketService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/webSocket")
@Api(tags = "webSocket")
public class WebSocketController {

    @Resource
    WebSocketService baseService;

    @PostMapping("/register")
    @ApiOperation(value = "获取 webSocket连接地址和随机码")
    public ApiResultVO<WebSocketRegisterVO> register(@RequestBody @Valid NotNullByte notNullByte) {
        return ApiResultVO.ok(baseService.register(notNullByte));
    }

    @PreAuthorize("hasAuthority('webSocket:page')")
    @PostMapping("/page")
    @ApiOperation(value = "分页排序查询")
    public ApiResultVO<Page<WebSocketPageVO>> myPage(@RequestBody @Valid WebSocketPageDTO dto) {
        return ApiResultVO.ok(baseService.myPage(dto));
    }

    @PostMapping("/changeType")
    @ApiOperation(value = "更改在线状态")
    public ApiResultVO<String> changeType(@RequestBody @Valid NotNullByteAndId notNullByteAndId) {
        return ApiResultVO.ok(baseService.changeType(notNullByteAndId));
    }

    @PreAuthorize("hasAuthority('webSocket:insertOrUpdate')")
    @PostMapping("/offlineByIdSet")
    @ApiOperation(value = "强退，通过 idSet")
    public ApiResultVO<String> offlineByIdSet(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.ok(baseService.retreatAndNoticeByIdSet(notEmptyIdSet));
    }

    @PreAuthorize("hasAuthority('webSocket:insertOrUpdate')")
    @PostMapping("/offlineAll")
    @ApiOperation(value = "全部强退")
    public ApiResultVO<String> offlineAll() {
        return ApiResultVO.ok(baseService.retreatAndNoticeAll());
    }

}
