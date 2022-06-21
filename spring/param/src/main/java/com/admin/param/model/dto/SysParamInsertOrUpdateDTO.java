package com.admin.param.model.dto;

import com.admin.common.model.dto.BaseInsertOrUpdateDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * {@link com.admin.common.model.entity.SysParamDO}
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysParamInsertOrUpdateDTO extends BaseInsertOrUpdateDTO {

    @NotBlank
    @ApiModelProperty(value = "配置名，以 id为不变值进行使用，不要用此属性")
    private String name;

    @NotBlank
    @ApiModelProperty(value = "值")
    private String value;

    @ApiModelProperty(value = "描述/备注")
    private String remark;

    @ApiModelProperty(value = "启用/禁用")
    private boolean enableFlag;

}
