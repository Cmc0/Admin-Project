package com.admin.job.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@TableName(value = "sys_job_ref_user")
@Data
@ApiModel(description = "岗位，用户关联表")
public class SysJobRefUserDO {

    @TableId
    @ApiModelProperty(value = "岗位主键 id")
    private Long jobId;

    @ApiModelProperty(value = "用户主键 id")
    private Long userId;

}
