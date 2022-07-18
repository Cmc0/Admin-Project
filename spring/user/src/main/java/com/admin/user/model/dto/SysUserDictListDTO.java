package com.admin.user.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SysUserDictListDTO {

    @ApiModelProperty(value = "是否追加 admin账号")
    private boolean addAdminFlag;

}
