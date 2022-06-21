package com.admin.user.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserRegisterByEmailDTO extends UserUpdateEmailDTO {

    @NotBlank
    @ApiModelProperty(value = "前端加密之后的原始密码")
    private String origPassword;

}
