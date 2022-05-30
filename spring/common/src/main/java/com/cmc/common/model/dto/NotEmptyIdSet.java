package com.cmc.common.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
public class NotEmptyIdSet {

    @NotEmpty
    @ApiModelProperty(value = "主键 idSet")
    private Set<Long> idSet;

}
