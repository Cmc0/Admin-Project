package com.admin.area.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@TableName(value = "sys_area_ref_dept")
@Data
@ApiModel(description = "区域，部门关联表")
public class SysAreaRefDeptDO {

    @TableId
    @ApiModelProperty(value = "区域id")
    private Long areaId;

    @ApiModelProperty(value = "部门id")
    private Long deptId;
}
