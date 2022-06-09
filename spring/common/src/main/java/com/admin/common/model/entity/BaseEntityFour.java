package com.admin.common.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "实体类基类 Four")
public class BaseEntityFour<T> extends BaseEntityThree {

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "排序号（值越大越前面，默认为 0）")
    private Integer orderNo;

    @ApiModelProperty(value = "父节点id（顶级则为0）")
    private Long parentId;

    @TableField(exist = false)
    @ApiModelProperty(value = "子节点")
    private List<T> children;

}
