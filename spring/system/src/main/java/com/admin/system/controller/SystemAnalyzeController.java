package com.admin.system.controller;

import com.admin.common.model.vo.ApiResultVO;
import com.admin.system.model.vo.SystemAnalyzeActiveUserTrendVO;
import com.admin.system.model.vo.SystemAnalyzeActiveUserVO;
import com.admin.system.model.vo.SystemAnalyzeTrafficUsageVO;
import com.admin.system.model.vo.SystemAnalyzeUserVO;
import com.admin.system.service.SystemAnalyzeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/systemAnalyze")
@Api(tags = "平台系统-分析")
public class SystemAnalyzeController {

    @Resource
    SystemAnalyzeService baseService;

    @PreAuthorize("hasAuthority('systemAnalyze:requset')")
    @PostMapping("/activeUser")
    @ApiOperation(value = "活跃人数分析")
    public ApiResultVO<SystemAnalyzeActiveUserVO> activeUser() {
        return ApiResultVO.ok(baseService.activeUser());
    }

    @PreAuthorize("hasAuthority('systemAnalyze:requset')")
    @PostMapping("/activeUserTrend")
    @ApiOperation(value = "活跃人数走势")
    public ApiResultVO<List<SystemAnalyzeActiveUserTrendVO>> activeUserTrend() {
        return ApiResultVO.ok(baseService.activeUserTrend());
    }

    @PreAuthorize("hasAuthority('systemAnalyze:user')")
    @PostMapping("/user")
    @ApiOperation(value = "用户分析")
    public ApiResultVO<SystemAnalyzeUserVO> user() {
        return ApiResultVO.ok(baseService.user());
    }

    @PreAuthorize("hasAuthority('systemAnalyze:requset')")
    @PostMapping("/trafficUsage")
    @ApiOperation(value = "流量占用情况")
    public ApiResultVO<List<SystemAnalyzeTrafficUsageVO>> trafficUsage() {
        return ApiResultVO.ok(baseService.trafficUsage());
    }

}
