package com.admin.common.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MyOrderDTO {

    @ApiModelProperty(value = "排序的字段名")
    private String name;

    @ApiModelProperty(value = "ascend（升序，默认） descend（降序）")
    private String value;

}
