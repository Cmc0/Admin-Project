package com.admin.dept.model.entity;

import com.admin.common.model.entity.BaseEntityFour;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_dept")
@Data
@ApiModel(description = "部门主表")
public class SysDeptDO extends BaseEntityFour<SysDeptDO> {

    @ApiModelProperty(value = "部门名称")
    private String name;

}
