package com.admin.area.controller;

import com.admin.area.model.dto.SysAreaInsertOrUpdateDTO;
import com.admin.area.model.dto.SysAreaPageDTO;
import com.admin.area.model.entity.SysAreaDO;
import com.admin.area.model.vo.SysAreaInfoByIdVO;
import com.admin.area.service.SysAreaService;
import com.admin.common.model.dto.AddOrderNoDTO;
import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullId;
import com.admin.common.model.vo.ApiResultVO;
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
@RequestMapping("/sysArea")
@Api(tags = "区域控制器")
public class SysAreaController {

    @Resource
    SysAreaService baseService;

    @ApiOperation(value = "新增/修改")
    @PostMapping("/insertOrUpdate")
    @PreAuthorize("hasAuthority('sysArea:insertOrUpdate')")
    public ApiResultVO<String> insertOrUpdate(@RequestBody @Valid SysAreaInsertOrUpdateDTO dto) {
        return ApiResultVO.ok(baseService.insertOrUpdate(dto));
    }

    @ApiOperation(value = "分页排序查询")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('sysArea:page')")
    public ApiResultVO<Page<SysAreaDO>> myPage(@RequestBody @Valid SysAreaPageDTO dto) {
        return ApiResultVO.ok(baseService.myPage(dto));
    }

    @ApiOperation(value = "查询：树结构")
    @PostMapping("/tree")
    @PreAuthorize("hasAuthority('sysArea:page')")
    public ApiResultVO<List<SysAreaDO>> tree(@RequestBody @Valid SysAreaPageDTO dto) {
        return ApiResultVO.ok(baseService.tree(dto));
    }

    @ApiOperation(value = "批量删除")
    @PostMapping("/deleteByIdSet")
    @PreAuthorize("hasAuthority('sysArea:deleteByIdSet')")
    public ApiResultVO<String> deleteByIdSet(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.ok(baseService.deleteByIdSet(notEmptyIdSet));
    }

    @ApiOperation(value = "通过主键id，查看详情")
    @PostMapping("/infoById")
    @PreAuthorize("hasAuthority('sysArea:infoById')")
    public ApiResultVO<SysAreaInfoByIdVO> infoById(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.ok(baseService.infoById(notNullId));
    }

    @ApiOperation(value = "通过主键 idSet，加减排序号")
    @PostMapping("/addOrderNo")
    @PreAuthorize("hasAuthority('sysArea:insertOrUpdate')")
    public ApiResultVO<String> addOrderNo(@RequestBody @Valid AddOrderNoDTO dto) {
        return ApiResultVO.ok(baseService.addOrderNo(dto));
    }

}
