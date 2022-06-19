package com.admin.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_param")
@Data
@ApiModel(description = "系统参数设置主表")
public class SysParamDO extends BaseEntityThree {

    @ApiModelProperty(value = "配置名，以 id为不变值进行使用，不要用此属性")
    private String name;

    @ApiModelProperty(value = "值")
    private String value;

}
