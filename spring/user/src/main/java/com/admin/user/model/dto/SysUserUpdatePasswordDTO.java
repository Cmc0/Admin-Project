package com.admin.user.model.dto;

import com.admin.common.model.dto.NotEmptyIdSet;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserUpdatePasswordDTO extends NotEmptyIdSet {

    @NotBlank
    @ApiModelProperty(value = "前端加密之后的，新密码")
    private String newPassword;

    @NotBlank
    @ApiModelProperty(value = "前端加密之后的原始密码，新密码")
    private String newOrigPassword;

}
