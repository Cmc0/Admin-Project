package com.admin.common.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class NotNullByte {

    @Min(1)
    @NotNull
    @ApiModelProperty(value = "值")
    private Byte value;

}
