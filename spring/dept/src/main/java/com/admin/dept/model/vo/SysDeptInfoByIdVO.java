package com.admin.dept.model.vo;

import com.admin.dept.model.entity.SysDeptDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysDeptInfoByIdVO extends SysDeptDO {

    @ApiModelProperty(value = "区域 idSet")
    private Set<Long> areaIdSet;

    @ApiModelProperty(value = "用户 idSet")
    private Set<Long> userIdSet;

}
