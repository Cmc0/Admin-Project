package com.admin.common.model.entity;

import com.admin.common.model.enums.SysDictTypeEnum;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_dict")
@Data
@ApiModel(description = "字典主表")
public class SysDictDO extends BaseEntityThree {

    @ApiModelProperty(value = "字典 key（不能重复），字典项要冗余这个 key，目的：方便操作")
    private String dictKey;

    @ApiModelProperty(value = "字典/字典项 名")
    private String name;

    @ApiModelProperty(value = "类型：1 字典 2 字典项")
    private SysDictTypeEnum type;

    @ApiModelProperty(value = "字典项 value（数字 123...）备注：字典为 -1")
    private Byte value;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "排序号（值越大越前面，默认为 0）")
    private Integer orderNo;
}
