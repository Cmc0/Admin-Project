package com.admin.job.controller;

import com.admin.common.model.dto.AddOrderNoDTO;
import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullId;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.job.model.dto.SysJobInsertOrUpdateDTO;
import com.admin.job.model.dto.SysJobPageDTO;
import com.admin.job.model.entity.SysJobDO;
import com.admin.job.model.vo.SysJobInfoByIdVO;
import com.admin.job.service.SysJobService;
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
import java.util.List;

@RestController
@RequestMapping("/sysJob")
@Api(tags = "岗位-管理")
public class SysJobController {

    @Resource
    SysJobService baseService;

    @ApiOperation(value = "新增/修改")
    @PostMapping("/insertOrUpdate")
    @PreAuthorize("hasAuthority('sysJob:insertOrUpdate')")
    public ApiResultVO<String> insertOrUpdate(@RequestBody @Valid SysJobInsertOrUpdateDTO dto) {
        return ApiResultVO.ok(baseService.insertOrUpdate(dto));
    }

    @ApiOperation(value = "分页排序查询")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('sysJob:page')")
    public ApiResultVO<Page<SysJobDO>> myPage(@RequestBody @Valid SysJobPageDTO dto) {
        return ApiResultVO.ok(baseService.myPage(dto));
    }

    @ApiOperation(value = "查询：树结构")
    @PostMapping("/tree")
    @PreAuthorize("hasAuthority('sysJob:page')")
    public ApiResultVO<List<SysJobDO>> tree(@RequestBody @Valid SysJobPageDTO dto) {
        return ApiResultVO.ok(baseService.tree(dto));
    }

    @ApiOperation(value = "批量删除")
    @PostMapping("/deleteByIdSet")
    @PreAuthorize("hasAuthority('sysJob:deleteByIdSet')")
    public ApiResultVO<String> deleteByIdSet(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.ok(baseService.deleteByIdSet(notEmptyIdSet));
    }

    @ApiOperation(value = "通过主键id，查看详情")
    @PostMapping("/infoById")
    @PreAuthorize("hasAuthority('sysJob:infoById')")
    public ApiResultVO<SysJobInfoByIdVO> infoById(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.ok(baseService.infoById(notNullId));
    }

    @ApiOperation(value = "通过主键 idSet，加减排序号")
    @PostMapping("/addOrderNo")
    @PreAuthorize("hasAuthority('sysJob:insertOrUpdate')")
    public ApiResultVO<String> addOrderNo(@RequestBody @Valid AddOrderNoDTO dto) {
        return ApiResultVO.ok(baseService.addOrderNo(dto));
    }

}
