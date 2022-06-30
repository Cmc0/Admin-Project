package com.admin.user.model.dto;

import com.admin.common.model.constant.BaseRegexConstant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class SysUserSelfUpdatePasswordDTO {

    @NotBlank
    @ApiModelProperty(value = "前端加密之后的，新密码")
    private String newPassword;

    @NotBlank
    @ApiModelProperty(value = "前端加密之后的原始密码，新密码")
    private String newOrigPassword;

    @Pattern(regexp = BaseRegexConstant.CODE_6_REGEXP)
    @NotBlank
    @ApiModelProperty(value = "邮箱验证码")
    private String code;

}
