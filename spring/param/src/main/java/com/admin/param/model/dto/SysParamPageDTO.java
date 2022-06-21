package com.admin.param.model.dto;

import com.admin.common.model.dto.MyPageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * {@link com.admin.common.model.entity.SysParamDO}
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysParamPageDTO extends MyPageDTO {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "配置名，以 id为不变值进行使用，不要用此属性")
    private String name;

    @ApiModelProperty(value = "启用/禁用")
    private Boolean enableFlag;

    @ApiModelProperty(value = "描述/备注")
    private String remark;

}
