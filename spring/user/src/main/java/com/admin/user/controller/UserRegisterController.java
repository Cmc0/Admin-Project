package com.admin.user.controller;

import com.admin.common.model.dto.EmailNotBlankDTO;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.user.model.dto.UserRegisterByEmailDTO;
import com.admin.user.service.UserRegisterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/userRegister")
@Api(tags = "用户-注册")
public class UserRegisterController {

    @Resource
    UserRegisterService baseService;

    @PostMapping(value = "/email")
    @ApiOperation(value = "邮箱-注册")
    public ApiResultVO<String> userRegisterByEmail(@RequestBody @Valid UserRegisterByEmailDTO dto) {
        return ApiResultVO.ok(baseService.userRegisterByEmail(dto));
    }

    @PostMapping("/email/sendCode")
    @ApiOperation(value = "邮箱-注册-发送验证码")
    public ApiResultVO<String> userRegisterByEmailSendCode(@RequestBody @Valid EmailNotBlankDTO dto) {
        return ApiResultVO.ok(baseService.userRegisterByEmailSendCode(dto));
    }

}
