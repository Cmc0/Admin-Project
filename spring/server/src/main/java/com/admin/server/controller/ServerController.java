package com.admin.server.controller;

import com.admin.common.model.vo.ApiResultVO;
import com.admin.server.model.vo.ServerWorkInfoVO;
import com.admin.server.service.ServerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/server")
@Api(tags = "服务器-管理")
public class ServerController {

    @Resource
    ServerService baseService;

    @PreAuthorize("hasAuthority('server:workInfo')")
    @PostMapping("/workInfo")
    @ApiOperation(value = "服务器运行情况")
    public ApiResultVO<ServerWorkInfoVO> workInfo() {
        return ApiResultVO.ok(baseService.workInfo());
    }

}
