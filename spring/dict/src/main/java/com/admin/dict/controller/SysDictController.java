package com.admin.dict.controller;

import com.admin.common.model.dto.AddOrderNoDTO;
import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullId;
import com.admin.common.model.entity.SysDictDO;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.dict.model.dto.SysDictInsertOrUpdateDTO;
import com.admin.dict.model.dto.SysDictPageDTO;
import com.admin.dict.model.vo.SysDictTreeVO;
import com.admin.dict.service.SysDictService;
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

@Api(tags = "字典控制器")
@RestController
@RequestMapping("/sysDict")
public class SysDictController {

    @Resource
    SysDictService baseService;

    @ApiOperation(value = "新增/修改")
    @PostMapping("/insertOrUpdate")
    @PreAuthorize("hasAuthority('sysDict:insertOrUpdate')")
    public ApiResultVO<String> insertOrUpdate(@RequestBody @Valid SysDictInsertOrUpdateDTO dto) {
        return ApiResultVO.ok(baseService.insertOrUpdate(dto));
    }

    @ApiOperation(value = "分页排序查询")
    @PostMapping("/page")
    public ApiResultVO<Page<SysDictDO>> myPage(@RequestBody @Valid SysDictPageDTO dto) {
        return ApiResultVO.ok(baseService.myPage(dto));
    }

    @ApiOperation(value = "查询：树结构")
    @PostMapping("/tree")
    public ApiResultVO<List<SysDictTreeVO>> tree(@RequestBody @Valid SysDictPageDTO dto) {
        return ApiResultVO.ok(baseService.tree(dto));
    }

    @ApiOperation(value = "删除字典")
    @PostMapping("/deleteByIdSet")
    @PreAuthorize("hasAuthority('sysDict:deleteByIdSet')")
    public ApiResultVO<String> deleteByIdSet(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.ok(baseService.deleteByIdSet(notEmptyIdSet));
    }

    @ApiOperation(value = "通过主键id，查看详情")
    @PostMapping("/infoById")
    @PreAuthorize("hasAuthority('sysDict:infoById')")
    public ApiResultVO<SysDictDO> infoById(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.ok(baseService.infoById(notNullId));
    }

    @ApiOperation(value = "通过主键 idSet，加减排序号")
    @PostMapping("/addOrderNo")
    @PreAuthorize("hasAuthority('sysDict:insertOrUpdate')")
    public ApiResultVO<String> addOrderNo(@RequestBody @Valid AddOrderNoDTO dto) {
        return ApiResultVO.ok(baseService.addOrderNo(dto));
    }

}
