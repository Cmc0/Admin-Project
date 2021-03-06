package com.admin.user.controller;

import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullId;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.common.model.vo.DictLongListVO;
import com.admin.user.model.dto.SysUserInsertOrUpdateDTO;
import com.admin.user.model.dto.SysUserPageDTO;
import com.admin.user.model.dto.SysUserDictListDTO;
import com.admin.user.model.dto.SysUserUpdatePasswordDTO;
import com.admin.user.model.vo.SysUserInfoByIdVO;
import com.admin.user.model.vo.SysUserPageVO;
import com.admin.user.service.SysUserService;
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
@RequestMapping(value = "/sysUser")
@Api(tags = "用户-管理")
public class SysUserController {

    @Resource
    SysUserService baseService;

    @ApiOperation(value = "分页排序查询")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('sysUser:page')")
    public ApiResultVO<Page<SysUserPageVO>> myPage(@RequestBody @Valid SysUserPageDTO dto) {
        return ApiResultVO.ok(baseService.myPage(dto));
    }

    @ApiOperation(value = "下拉列表")
    @PostMapping("/dictList")
    @PreAuthorize("hasAuthority('sysUser:page')")
    public ApiResultVO<List<DictLongListVO>> dictList(@RequestBody @Valid SysUserDictListDTO dto) {
        return ApiResultVO.ok(baseService.dictList(dto));
    }

    @ApiOperation(value = "新增/修改")
    @PostMapping("/insertOrUpdate")
    @PreAuthorize("hasAuthority('sysUser:insertOrUpdate')")
    public ApiResultVO<String> insertOrUpdate(@RequestBody @Valid SysUserInsertOrUpdateDTO dto) {
        return ApiResultVO.ok(baseService.insertOrUpdate(dto));
    }

    @ApiOperation(value = "批量注销用户")
    @PostMapping("/deleteByIdSet")
    @PreAuthorize("hasAuthority('sysUser:deleteByIdSet')")
    public ApiResultVO<String> deleteByIdSet(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.ok(baseService.deleteByIdSet(notEmptyIdSet));
    }

    @ApiOperation(value = "通过主键id，查看详情")
    @PostMapping("/infoById")
    @PreAuthorize("hasAuthority('sysUser:infoById')")
    public ApiResultVO<SysUserInfoByIdVO> infoById(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.ok(baseService.infoById(notNullId));
    }

    @ApiOperation(value = "刷新用户 jwt私钥后缀")
    @PostMapping(value = "/refreshJwtSecretSuf")
    @PreAuthorize("hasAuthority('sysUser:insertOrUpdate')")
    public ApiResultVO<String> refreshJwtSecretSuf(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.ok(baseService.refreshJwtSecretSuf(notEmptyIdSet));
    }

    @ApiOperation(value = "批量重置头像")
    @PostMapping("/resetAvatar")
    @PreAuthorize("hasAuthority('sysUser:insertOrUpdate')")
    public ApiResultVO<String> resetAvatar(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.ok(baseService.resetAvatar(notEmptyIdSet));
    }

    @ApiOperation(value = "批量修改密码")
    @PostMapping("/updatePassword")
    @PreAuthorize("hasAuthority('sysUser:insertOrUpdate')")
    public ApiResultVO<String> updatePassword(@RequestBody @Valid SysUserUpdatePasswordDTO dto) {
        return ApiResultVO.ok(baseService.updatePassword(dto));
    }

}
