package com.admin.role.model.dto;

import com.admin.common.model.dto.MyPageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * {@link SysRoleInsertOrUpdateDTO}
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysRolePageDTO extends MyPageDTO {

    @ApiModelProperty(value = "角色名（不能重复）")
    private String name;

    @ApiModelProperty(value = "是否是默认角色，备注：只会有一个默认角色")
    private Boolean defaultFlag;

    @ApiModelProperty(value = "启用/禁用")
    private Boolean enableFlag;

    @ApiModelProperty(value = "描述/备注")
    private String remark;

}
