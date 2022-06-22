package com.admin.job.model.entity;

import com.admin.common.model.entity.BaseEntityFour;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_job")
@Data
@ApiModel(description = "岗位主表")
public class SysJobDO extends BaseEntityFour<SysJobDO> {

    @ApiModelProperty(value = "岗位名称")
    private String name;

}
