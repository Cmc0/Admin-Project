package com.admin.request.controller;

import com.admin.common.model.vo.ApiResultVO;
import com.admin.request.model.dto.SysRequestPageDTO;
import com.admin.request.model.dto.SysRequestSelfLoginRecordPageDTO;
import com.admin.request.model.vo.SysRequestAllAvgVO;
import com.admin.request.model.vo.SysRequestPageVO;
import com.admin.request.service.SysRequestService;
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
@RequestMapping("/sysRequest")
@Api(tags = "接口请求-管理")
public class SysRequestController {

    @Resource
    SysRequestService baseService;

    @PreAuthorize("hasAuthority('sysRequest:page')")
    @PostMapping("/page")
    @ApiOperation(value = "分页排序查询")
    public ApiResultVO<Page<SysRequestPageVO>> myPage(@RequestBody @Valid SysRequestPageDTO dto) {
        return ApiResultVO.ok(baseService.myPage(dto));
    }

    @PostMapping("/allAvgPro")
    @ApiOperation(value = "所有请求的平均耗时-增强：增加筛选项")
    public ApiResultVO<SysRequestAllAvgVO> allAvgPro(@RequestBody @Valid SysRequestPageDTO dto) {
        return ApiResultVO.ok(baseService.allAvgPro(dto));
    }

    @PostMapping("/allAvg")
    @ApiOperation(value = "所有请求的平均耗时")
    public ApiResultVO<SysRequestAllAvgVO> allAvg() {
        return ApiResultVO.ok(baseService.allAvg());
    }

    @PostMapping("/selfLoginRecord")
    @ApiOperation(value = "当前用户：登录记录")
    public ApiResultVO<Page<SysRequestPageVO>> selfLoginRecord(
        @RequestBody @Valid SysRequestSelfLoginRecordPageDTO dto) {
        return ApiResultVO.ok(baseService.selfLoginRecord(dto));
    }

}
