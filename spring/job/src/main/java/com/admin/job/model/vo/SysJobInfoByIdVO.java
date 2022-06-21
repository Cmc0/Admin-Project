package com.admin.job.model.vo;

import com.admin.job.model.entity.SysJobDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysJobInfoByIdVO extends SysJobDO {

    @ApiModelProperty(value = "用户 idSet")
    private Set<Long> userIdSet;

}
