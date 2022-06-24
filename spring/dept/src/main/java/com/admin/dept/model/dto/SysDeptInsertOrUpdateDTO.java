package com.admin.dept.model.dto;

import com.admin.common.model.dto.BaseInsertOrUpdateDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.Set;

/**
 * {@link com.admin.dept.model.entity.SysDeptDO}
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysDeptInsertOrUpdateDTO extends BaseInsertOrUpdateDTO {

    @ApiModelProperty(value = "排序号（值越大越前面，默认为 0）")
    private Integer orderNo;

    @ApiModelProperty(value = "启用/禁用")
    private boolean enableFlag;

    @ApiModelProperty(value = "父节点id（顶级则为0）")
    private Long parentId;

    @NotBlank
    @ApiModelProperty(value = "部门名")
    private String name;

    @ApiModelProperty(value = "描述/备注")
    private String remark;

    @ApiModelProperty(value = "区域 idSet")
    private Set<Long> areaIdSet;

    @ApiModelProperty(value = "用户 idSet")
    private Set<Long> userIdSet;

}
