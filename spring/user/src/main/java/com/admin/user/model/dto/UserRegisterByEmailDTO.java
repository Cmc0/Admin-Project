package com.admin.user.model.dto;

import com.admin.common.model.constant.BaseRegexConstant;
import com.admin.common.model.dto.EmailNotBlankDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserRegisterByEmailDTO extends EmailNotBlankDTO {

    @Pattern(regexp = BaseRegexConstant.CODE_6_REGEXP)
    @NotBlank
    @ApiModelProperty(value = "邮箱验证码")
    private String code;

    @NotBlank
    @ApiModelProperty(value = "前端加密之后的密码")
    private String password;

    @NotBlank
    @ApiModelProperty(value = "前端加密之后的原始密码")
    private String origPassword;

}
