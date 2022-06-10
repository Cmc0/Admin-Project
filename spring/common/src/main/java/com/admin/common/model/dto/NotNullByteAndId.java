package com.admin.common.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class NotNullByteAndId extends NotNullId {

    @Min(1)
    @NotNull
    @ApiModelProperty(value = "值")
    private Byte value;

}
