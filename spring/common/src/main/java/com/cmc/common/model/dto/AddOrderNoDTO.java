package com.cmc.common.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class AddOrderNoDTO extends NotEmptyIdSet {

    @NotNull
    @ApiModelProperty(value = "统一加减的数值")
    private Integer number;

}
