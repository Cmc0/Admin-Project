package com.admin.dict.model.dto;

import com.admin.common.model.dto.MyPageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * {@link com.admin.common.model.entity.SysDictDO}
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysDictPageDTO extends MyPageDTO {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "字典 key（不能重复），字典项要冗余这个 key，目的：方便操作")
    private String dictKey;

    @ApiModelProperty(value = "字典/字典项 名")
    private String name;

    @ApiModelProperty(value = "类型：1 字典 2 字典项")
    private Byte type;

    @ApiModelProperty(value = "描述/备注")
    private String remark;

    @ApiModelProperty(value = "启用/禁用")
    private Boolean enableFlag;
}
