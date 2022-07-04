package com.admin.user.controller;

import com.admin.common.model.dto.MyCodeToKeyDTO;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.user.model.dto.UserSelfDeleteDTO;
import com.admin.user.model.dto.UserSelfUpdateBaseInfoDTO;
import com.admin.user.model.dto.UserSelfUpdateEmailDTO;
import com.admin.user.model.dto.UserSelfUpdatePasswordDTO;
import com.admin.user.model.vo.UserSelfBaseInfoVO;
import com.admin.user.service.UserSelfService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/userSelf")
@Api(tags = "用户-自我-管理")
public class UserSelfController {

    @Resource
    UserSelfService baseService;

    @ApiOperation(value = "当前用户：退出登录")
    @PostMapping(value = "/selfLogout")
    public ApiResultVO<String> selfLogout() {
        return ApiResultVO.ok(baseService.selfLogout());
    }

    @ApiOperation(value = "获取：当前用户，基本信息")
    @PostMapping(value = "/selfBaseInfo")
    public ApiResultVO<UserSelfBaseInfoVO> selfBaseInfo() {
        return ApiResultVO.ok(baseService.selfBaseInfo());
    }

    @ApiOperation(value = "当前用户：基本信息：修改")
    @PostMapping(value = "/selfUpdateBaseInfo")
    public ApiResultVO<String> selfUpdateBaseInfo(@RequestBody @Valid UserSelfUpdateBaseInfoDTO dto) {
        return ApiResultVO.ok(baseService.selfUpdateBaseInfo(dto));
    }

    @ApiOperation(value = "当前用户：修改密码")
    @PostMapping(value = "/selfUpdatePassword")
    public ApiResultVO<String> selfUpdatePassword(@RequestBody @Valid UserSelfUpdatePasswordDTO dto) {
        return ApiResultVO.ok(baseService.selfUpdatePassword(dto));
    }

    @ApiOperation(value = "当前用户：修改密码，发送，邮箱验证码")
    @PostMapping(value = "/selfUpdatePassword/sendEmailCode")
    public ApiResultVO<String> selfUpdatePasswordSendEmailCode() {
        return ApiResultVO.ok(baseService.selfUpdatePasswordSendEmailCode());
    }

    @ApiOperation(value = "当前用户：修改邮箱")
    @PostMapping(value = "/selfUpdateEmail")
    public ApiResultVO<String> selfUpdateEmail(@RequestBody @Valid UserSelfUpdateEmailDTO dto) {
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
    public ApiResultVO<String> selfDelete(UserSelfDeleteDTO dto) {
        return ApiResultVO.ok(baseService.selfDelete(dto));
    }

    @ApiOperation(value = "当前用户：注销，发送，邮箱验证码")
    @PostMapping(value = "/selfDelete/sendEmailCode")
    public ApiResultVO<String> selfDeleteSendEmailCode() {
        return ApiResultVO.ok(baseService.selfDeleteSendEmailCode());
    }

}
