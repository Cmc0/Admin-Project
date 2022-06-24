package com.admin.role.controller;

import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullId;
import com.admin.common.model.entity.SysRoleDO;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.role.model.dto.SysRoleInsertOrUpdateDTO;
import com.admin.role.model.dto.SysRolePageDTO;
import com.admin.role.model.vo.SysRoleInfoByIdVO;
import com.admin.role.service.SysRoleService;
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

@Api(tags = "角色-管理")
@RequestMapping("/sysRole")
@RestController
public class SysRoleController {

    @Resource
    SysRoleService baseService;

    @ApiOperation(value = "新增/修改")
    @PostMapping("/insertOrUpdate")
    @PreAuthorize("hasAuthority('sysRole:insertOrUpdate')")
    public ApiResultVO<String> insertOrUpdate(@RequestBody @Valid SysRoleInsertOrUpdateDTO dto) {
        return ApiResultVO.ok(baseService.insertOrUpdate(dto));
    }

    @ApiOperation(value = "分页排序查询")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('sysRole:page')")
    public ApiResultVO<Page<SysRoleDO>> myPage(@RequestBody @Valid SysRolePageDTO dto) {
        return ApiResultVO.ok(baseService.myPage(dto));
    }

    @ApiOperation(value = "通过主键id，查看详情")
    @PostMapping("/infoById")
    @PreAuthorize("hasAuthority('sysRole:infoById')")
    public ApiResultVO<SysRoleInfoByIdVO> infoById(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.ok(baseService.infoById(notNullId));
    }

    @ApiOperation(value = "批量删除")
    @PostMapping("/deleteByIdSet")
    @PreAuthorize("hasAuthority('sysRole:deleteByIdSet')")
    public ApiResultVO<String> deleteByIdSet(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.ok(baseService.deleteByIdSet(notEmptyIdSet));
    }

}
