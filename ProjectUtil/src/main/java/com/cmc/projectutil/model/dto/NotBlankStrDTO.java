package com.cmc.projectutil.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class NotBlankStrDTO {

    @NotBlank
    @ApiModelProperty(value = "值")
    private String value;

}
