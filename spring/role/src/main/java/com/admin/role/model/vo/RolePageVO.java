package com.admin.role.model.vo;

import com.admin.common.model.entity.BaseRoleDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class RolePageVO extends BaseRoleDO {

    @ApiModelProperty(value = "用户 idSet")
    private Set<Long> userIdSet;

    @ApiModelProperty(value = "菜单 idSet")
    private Set<Long> menuIdSet;
}
