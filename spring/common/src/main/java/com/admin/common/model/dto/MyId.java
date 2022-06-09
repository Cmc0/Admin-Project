package com.admin.common.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MyId {

    @Min(1)
    @ApiModelProperty(value = "主键id")
    private Long id;

}
