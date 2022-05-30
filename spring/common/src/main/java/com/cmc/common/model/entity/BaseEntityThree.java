package com.cmc.common.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "实体类基类 Three")
public class BaseEntityThree extends BaseEntityTwo {

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "启用/禁用")
    private Boolean enableFlag;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "是否逻辑删除")
    private Boolean delFlag;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "描述/备注")
    private String remark;

}
