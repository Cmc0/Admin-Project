package com.admin.common.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotEmptyIdSet {

    @NotEmpty
    @ApiModelProperty(value = "主键 idSet")
    private Set<Long> idSet;

}
