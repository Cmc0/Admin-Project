package com.cmc.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@TableName(value = "role_ref_user")
@Data
@ApiModel(description = "角色，用户关联表")
public class RoleRefUserDO {

    @TableId
    @ApiModelProperty(value = "角色id")
    private Long roleId;

    @ApiModelProperty(value = "用户id")
    private Long userId;

}
