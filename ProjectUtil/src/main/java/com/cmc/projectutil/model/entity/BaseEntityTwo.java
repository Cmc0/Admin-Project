package com.cmc.projectutil.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "实体类基类 Two")
public class BaseEntityTwo extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键id")
    private Long id;

}
