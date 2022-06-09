package com.cmc.user.controller;

import com.cmc.common.model.vo.ApiResultVO;
import com.cmc.user.model.dto.UserLoginPasswordDTO;
import com.cmc.user.service.UserLoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/userLogin")
@Api(tags = "用户登录")
public class UserLoginController {

    @Resource
    UserLoginService baseService;

    @PostMapping(value = "/password")
    @ApiOperation(value = "账号密码登录")
    public ApiResultVO<String> password(@RequestBody @Valid UserLoginPasswordDTO dto) {
        return ApiResultVO.ok("登录成功", baseService.password(dto));
    }

}
