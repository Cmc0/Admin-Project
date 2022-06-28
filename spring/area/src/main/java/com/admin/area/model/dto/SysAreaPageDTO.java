package com.admin.area.model.dto;

import com.admin.common.model.dto.MyPageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * {@link com.admin.area.model.entity.SysAreaDO, SysAreaInsertOrUpdateDTO}
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysAreaPageDTO extends MyPageDTO {

    @ApiModelProperty(value = "启用/禁用")
    private Boolean enableFlag;

    @ApiModelProperty(value = "区域名")
    private String name;

    @ApiModelProperty(value = "描述/备注")
    private String remark;

}
