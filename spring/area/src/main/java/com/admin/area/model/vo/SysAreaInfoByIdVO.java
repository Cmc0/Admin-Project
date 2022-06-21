package com.admin.area.model.vo;

import com.admin.area.model.entity.SysAreaDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysAreaInfoByIdVO extends SysAreaDO {

    @ApiModelProperty(value = "部门 idSet")
    private Set<Long> deptIdSet;

}
