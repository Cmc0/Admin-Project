package com.admin.menu.model.vo;

import com.admin.common.model.entity.SysMenuDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class MenuInfoByIdVO extends SysMenuDO {

    @ApiModelProperty(value = "角色 idSet")
    private Set<Long> roleIdSet;

}
