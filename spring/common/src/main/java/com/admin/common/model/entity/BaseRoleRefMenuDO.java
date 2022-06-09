package com.admin.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@TableName(value = "sys_role_ref_menu")
@Data
@ApiModel(description = "角色，菜单关联表")
public class BaseRoleRefMenuDO {

    @TableId
    @ApiModelProperty(value = "角色id")
    private Long roleId;

    @ApiModelProperty(value = "菜单id")
    private Long menuId;

}
