package com.admin.user.controller;

import com.admin.common.model.dto.EmailNotBlankDTO;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.user.model.dto.UserForgotPasswordDTO;
import com.admin.user.service.UserForgotPasswordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/userForgotPassword")
@Api(tags = "用户-忘记密码")
public class UserForgotPasswordController {

    @Resource
    UserForgotPasswordService baseService;

    @ApiOperation(value = "忘记密码，重置密码")
    @PostMapping
    public ApiResultVO<String> userForgotPassword(@RequestBody @Valid UserForgotPasswordDTO dto) {
        return ApiResultVO.ok(baseService.userForgotPassword(dto));
    }

    @ApiOperation(value = "忘记密码，发送，邮箱验证码")
    @PostMapping(value = "/sendEmailCode")
    public ApiResultVO<String> userForgotPasswordSendEmailCode(@RequestBody @Valid EmailNotBlankDTO dto) {
        return ApiResultVO.ok(baseService.userForgotPasswordSendEmailCode(dto));
    }

}
