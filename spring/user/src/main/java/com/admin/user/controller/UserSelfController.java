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
    @PostMapping(value = "/logout")
    public ApiResultVO<String> userSelfLogout() {
        return ApiResultVO.ok(baseService.userSelfLogout());
    }

    @ApiOperation(value = "获取：当前用户，基本信息")
    @PostMapping(value = "/baseInfo")
    public ApiResultVO<UserSelfBaseInfoVO> userSelfBaseInfo() {
        return ApiResultVO.ok(baseService.userSelfBaseInfo());
    }

    @ApiOperation(value = "当前用户：基本信息：修改")
    @PostMapping(value = "/updateBaseInfo")
    public ApiResultVO<String> userSelfUpdateBaseInfo(@RequestBody @Valid UserSelfUpdateBaseInfoDTO dto) {
        return ApiResultVO.ok(baseService.userSelfUpdateBaseInfo(dto));
    }

    @ApiOperation(value = "当前用户：修改密码")
    @PostMapping(value = "/updatePassword")
    public ApiResultVO<String> userSelfUpdatePassword(@RequestBody @Valid UserSelfUpdatePasswordDTO dto) {
        return ApiResultVO.ok(baseService.userSelfUpdatePassword(dto));
    }

    @ApiOperation(value = "当前用户：修改密码，发送，邮箱验证码")
    @PostMapping(value = "/updatePassword/sendEmailCode")
    public ApiResultVO<String> userSelfUpdatePasswordSendEmailCode() {
        return ApiResultVO.ok(baseService.userSelfUpdatePasswordSendEmailCode());
    }

    @ApiOperation(value = "当前用户：修改邮箱")
    @PostMapping(value = "/updateEmail")
    public ApiResultVO<String> userSelfUpdateEmail(@RequestBody @Valid UserSelfUpdateEmailDTO dto) {
        return ApiResultVO.ok(baseService.userSelfUpdateEmail(dto));
    }

    @ApiOperation(value = "当前用户：修改邮箱，发送，邮箱验证码")
    @PostMapping(value = "/updateEmail/sendEmailCode")
    public ApiResultVO<String> userSelfUpdateEmailSendEmailCode() {
        return ApiResultVO.ok(baseService.userSelfUpdateEmailSendEmailCode());
    }

    @ApiOperation(value = "当前用户：修改邮箱，发送，邮箱验证码，验证码兑换 key")
    @PostMapping(value = "/updateEmail/sendEmailCode/codeToKey")
    public ApiResultVO<String> userSelfUpdateEmailSendEmailCodeCodeToKey(@RequestBody @Valid MyCodeToKeyDTO dto) {
        return ApiResultVO.ok(baseService.userSelfUpdateEmailSendEmailCodeCodeToKey(dto));
    }

    @ApiOperation(value = "当前用户：刷新jwt私钥后缀")
    @PostMapping(value = "/refreshJwtSecretSuf")
    public ApiResultVO<String> userSelfRefreshJwtSecretSuf() {
        return ApiResultVO.ok(baseService.userSelfRefreshJwtSecretSuf());
    }

    @ApiOperation(value = "当前用户：注销")
    @PostMapping(value = "/delete")
    public ApiResultVO<String> userSelfDelete(UserSelfDeleteDTO dto) {
        return ApiResultVO.ok(baseService.userSelfDelete(dto));
    }

    @ApiOperation(value = "当前用户：注销，发送，邮箱验证码")
    @PostMapping(value = "/delete/sendEmailCode")
    public ApiResultVO<String> userSelfDeleteSendEmailCode() {
        return ApiResultVO.ok(baseService.userSelfDeleteSendEmailCode());
    }

}
