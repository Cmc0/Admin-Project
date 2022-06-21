package com.admin.job.model.dto;

import com.admin.common.model.dto.MyPageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * {@link com.admin.job.model.entity.SysJobDO, SysJobInsertOrUpdateDTO}
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysJobPageDTO extends MyPageDTO {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "启用/禁用")
    private Boolean enableFlag;

    @ApiModelProperty(value = "岗位名")
    private String name;

    @ApiModelProperty(value = "描述/备注")
    private String remark;

}
