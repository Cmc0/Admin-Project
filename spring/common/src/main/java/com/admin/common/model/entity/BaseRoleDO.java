package com.admin.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_role")
@Data
@ApiModel(description = "角色主表")
public class BaseRoleDO extends BaseEntityThree {

    @ApiModelProperty(value = "角色名（不能重复）")
    private String name;

    @ApiModelProperty(value = "是否是默认角色，备注：只会有一个默认角色")
    private Boolean defaultFlag;

}
