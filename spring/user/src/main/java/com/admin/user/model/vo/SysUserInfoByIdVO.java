package com.admin.user.model.vo;

import com.admin.common.model.entity.SysUserDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserInfoByIdVO extends SysUserDO {

    @ApiModelProperty(value = "角色 idSet")
    private Set<Long> roleIdSet;

    @ApiModelProperty(value = "部门 idSet")
    private Set<Long> deptIdSet;

    @ApiModelProperty(value = "岗位 idSet")
    private Set<Long> jobIdSet;

}
