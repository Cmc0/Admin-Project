package com.cmc.projectutil.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class MyOrderItemDTO {

    @ApiModelProperty(value = "排序的字段名")
    private String name;

    @ApiModelProperty(value = "ascend（升序，默认） descend（降序）")
    private String value;

}
