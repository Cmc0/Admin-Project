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
public class SysUserSelfUpdateEmailDTO extends EmailNotBlankDTO {

    @Pattern(regexp = BaseRegexConstant.CODE_6_REGEXP)
    @NotBlank
    @ApiModelProperty(value = "新邮箱，验证码")
    private String newCode;

    @Pattern(regexp = BaseRegexConstant.CODE_6_REGEXP)
    @NotBlank
    @ApiModelProperty(value = "旧邮箱，验证码")
    private String oldCode;

    @NotBlank
    @ApiModelProperty(value = "前端加密之后的密码")
    private String password;

}
