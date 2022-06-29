package com.admin.user.model.vo;

import com.admin.user.model.dto.SysUserUpdateBaseInfoDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserBaseInfoVO extends SysUserUpdateBaseInfoDTO {

    @ApiModelProperty(value = "邮箱，会脱敏")
    private String email;

    @ApiModelProperty(value = "是否有密码，用于前端显示，修改密码/设置密码")
    private boolean passwordFlag;

}
