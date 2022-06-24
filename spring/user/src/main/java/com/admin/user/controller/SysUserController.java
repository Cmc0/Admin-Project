package com.admin.user.controller;

import com.admin.common.model.vo.ApiResultVO;
import com.admin.user.model.dto.SysUserPageDTO;
import com.admin.user.model.vo.SysUserPageVO;
import com.admin.user.model.vo.UserBaseInfoVO;
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

    @ApiOperation(value = "退出登录")
    @PostMapping(value = "/logout")
    public ApiResultVO<String> logout() {
        return ApiResultVO.ok(baseService.logout());
    }

    @ApiOperation(value = "用户基本信息")
    @PostMapping(value = "/baseInfo")
    public ApiResultVO<UserBaseInfoVO> baseInfo() {
        return ApiResultVO.ok(baseService.baseInfo());
    }

    @ApiOperation(value = "分页排序查询")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('sysUser:page')")
    public ApiResultVO<Page<SysUserPageVO>> myPage(@RequestBody @Valid SysUserPageDTO dto) {
        return ApiResultVO.ok(baseService.myPage(dto));
    }

}
