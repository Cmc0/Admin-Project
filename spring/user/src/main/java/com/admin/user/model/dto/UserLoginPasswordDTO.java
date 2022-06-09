package com.admin.user.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserLoginPasswordDTO {

    @NotBlank
    @ApiModelProperty(value = "账号：邮箱")
    private String account;

    @NotBlank
    @ApiModelProperty(value = "密码")
    private String password;

}
