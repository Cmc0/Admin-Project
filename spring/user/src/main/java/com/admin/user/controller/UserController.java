package com.admin.user.controller;

import com.admin.common.model.vo.ApiResultVO;
import com.admin.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/user")
@Api(tags = "用户-管理")
public class UserController {

    @Resource
    UserService baseService;

    @ApiOperation(value = "退出登录")
    @PostMapping(value = "/logout")
    public ApiResultVO<String> logout() {
        return ApiResultVO.ok(baseService.logout());
    }

}
