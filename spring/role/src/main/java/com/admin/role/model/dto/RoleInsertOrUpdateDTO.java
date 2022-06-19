package com.admin.role.model.dto;

import com.admin.common.model.dto.BaseInsertOrUpdateDTO;
import com.admin.common.model.entity.SysRoleDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.Set;

/**
 * {@link SysRoleDO}
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RoleInsertOrUpdateDTO extends BaseInsertOrUpdateDTO {

    @NotBlank
    @ApiModelProperty(value = "角色名，不能重复")
    private String name;

    @ApiModelProperty(value = "启用/禁用")
    private boolean enableFlag;

    @ApiModelProperty(value = "是否是默认角色，备注：只会有一个默认角色")
    private boolean defaultFlag;

    @ApiModelProperty(value = "菜单 idSet")
    private Set<Long> menuIdSet;

    @ApiModelProperty(value = "用户 idSet")
    private Set<Long> userIdSet;

    @ApiModelProperty(value = "描述/备注")
    private String remark;
}
