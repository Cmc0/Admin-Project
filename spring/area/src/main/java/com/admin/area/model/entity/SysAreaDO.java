package com.admin.area.model.entity;

import com.admin.common.model.entity.BaseEntityFour;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_area")
@Data
@ApiModel(description = "区域主表")
public class SysAreaDO extends BaseEntityFour<SysAreaDO> {

    @ApiModelProperty(value = "区域名")
    private String name;

}
