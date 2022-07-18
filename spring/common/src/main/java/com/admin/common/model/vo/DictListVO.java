package com.admin.common.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DictListVO {

    @ApiModelProperty(value = "显示用")
    private String label;

    @ApiModelProperty(value = "传值用")
    private String value;

}
