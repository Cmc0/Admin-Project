package com.admin.user.controller;

import com.admin.common.model.dto.MyCodeToKeyDTO;
import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullId;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.user.model.dto.*;
import com.admin.user.model.vo.SysUserInfoByIdVO;
import com.admin.user.model.vo.SysUserPageVO;
import com.admin.user.model.vo.SysUserSelfBaseInfoVO;
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

@RestController
@RequestMapping(value = "/sysUser")
@Api(tags = "用户-管理")
public class SysUserController {

    @Resource
    SysUserService baseService;

    @ApiOperation(value = "当前用户：退出登录")
    @PostMapping(value = "/selfLogout")
    public ApiResultVO<String> selfLogout() {
        return ApiResultVO.ok(baseService.selfLogout());
    }

    @ApiOperation(value = "当前用户：用户基本信息")
    @PostMapping(value = "/selfBaseInfo")
    public ApiResultVO<SysUserSelfBaseInfoVO> selfBaseInfo() {
        return ApiResultVO.ok(baseService.selfBaseInfo());
    }

    @ApiOperation(value = "当前用户：修改用户基本信息")
    @PostMapping(value = "/selfUpdateBaseInfo")
    public ApiResultVO<String> selfUpdateBaseInfo(@RequestBody @Valid SysUserSelfUpdateBaseInfoDTO dto) {
        return ApiResultVO.ok(baseService.selfUpdateBaseInfo(dto));
    }

    @ApiOperation(value = "当前用户：修改密码")
    @PostMapping(value = "/selfUpdatePassword")
    public ApiResultVO<String> selfUpdatePassword(@RequestBody @Valid SysUserSelfUpdatePasswordDTO dto) {
        return ApiResultVO.ok(baseService.selfUpdatePassword(dto));
    }

    @ApiOperation(value = "当前用户：修改密码，发送，邮箱验证码")
    @PostMapping(value = "/selfUpdatePassword/sendEmailCode")
    public ApiResultVO<String> selfUpdatePasswordSendEmailCode() {
        return ApiResultVO.ok(baseService.selfUpdatePasswordSendEmailCode());
    }

    @ApiOperation(value = "当前用户：修改邮箱")
    @PostMapping(value = "/selfUpdateEmail")
    public ApiResultVO<String> selfUpdateEmail(@RequestBody @Valid SysUserSelfUpdateEmailDTO dto) {
        return ApiResultVO.ok(baseService.selfUpdateEmail(dto));
    }

    @ApiOperation(value = "当前用户：修改邮箱，发送，邮箱验证码")
    @PostMapping(value = "/selfUpdateEmail/sendEmailCode")
    public ApiResultVO<String> selfUpdateEmailSendEmailCode() {
        return ApiResultVO.ok(baseService.selfUpdateEmailSendEmailCode());
    }

    @ApiOperation(value = "当前用户：修改邮箱，发送，邮箱验证码，验证码兑换 key")
    @PostMapping(value = "/selfUpdateEmail/sendEmailCode/codeToKey")
    public ApiResultVO<String> selfUpdateEmailSendEmailCodeCodeToKey(@RequestBody @Valid MyCodeToKeyDTO dto) {
        return ApiResultVO.ok(baseService.selfUpdateEmailSendEmailCodeCodeToKey(dto));
    }

    @ApiOperation(value = "当前用户：刷新jwt私钥后缀")
    @PostMapping(value = "/selfRefreshJwtSecretSuf")
    public ApiResultVO<String> selfRefreshJwtSecretSuf() {
        return ApiResultVO.ok(baseService.selfRefreshJwtSecretSuf());
    }

    @ApiOperation(value = "当前用户：注销")
    @PostMapping(value = "/selfDelete")
    public ApiResultVO<String> selfDelete(SysUserSelfDeleteDTO dto) {
        return ApiResultVO.ok(baseService.selfDelete(dto));
    }

    @ApiOperation(value = "当前用户：注销，发送，邮箱验证码")
    @PostMapping(value = "/selfDelete/sendEmailCode")
    public ApiResultVO<String> selfDeleteSendEmailCode() {
        return ApiResultVO.ok(baseService.selfDeleteSendEmailCode());
    }

    @ApiOperation(value = "分页排序查询")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('sysUser:page')")
    public ApiResultVO<Page<SysUserPageVO>> myPage(@RequestBody @Valid SysUserPageDTO dto) {
        return ApiResultVO.ok(baseService.myPage(dto));
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
