package com.admin.dept.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@TableName(value = "sys_dept_ref_user")
@Data
@ApiModel(description = "部门，用户关联表")
public class SysDeptRefUserDO {

    @TableId
    @ApiModelProperty(value = "部门主键 id")
    private Long deptId;

    @ApiModelProperty(value = "用户主键 id")
    private Long userId;

}
