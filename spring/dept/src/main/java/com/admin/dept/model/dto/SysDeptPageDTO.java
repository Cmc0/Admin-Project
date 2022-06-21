package com.admin.dept.model.dto;

import com.admin.common.model.dto.MyPageDTO;
import com.admin.dept.model.entity.SysDeptDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * {@link SysDeptDO , SysDeptInsertOrUpdateDTO}
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysDeptPageDTO extends MyPageDTO {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "部门名称")
    private String name;

    @ApiModelProperty(value = "描述/备注")
    private String remark;

    @ApiModelProperty(value = "启用/禁用")
    private Boolean enableFlag;

}
