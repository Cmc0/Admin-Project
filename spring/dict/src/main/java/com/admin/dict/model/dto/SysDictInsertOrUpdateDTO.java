package com.admin.dict.model.dto;

import com.admin.common.model.dto.BaseInsertOrUpdateDTO;
import com.admin.common.model.enums.SysDictTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * {@link com.admin.common.model.entity.SysDictDO}
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysDictInsertOrUpdateDTO extends BaseInsertOrUpdateDTO {

    @NotBlank
    @ApiModelProperty(value = "字典 key（不能重复），字典项要冗余这个 key，目的：方便操作")
    private String dictKey;

    @NotBlank
    @ApiModelProperty(value = "字典/字典项 名")
    private String name;

    @NotNull
    @ApiModelProperty(value = "字典类型：1 字典 2 字典项")
    private SysDictTypeEnum type;

    @ApiModelProperty(value = "字典项 value（数字 123...）备注：字典为 -1")
    private Byte value;

    @ApiModelProperty(value = "排序号（值越大越前面，默认为 0）")
    private Integer orderNo;

    @ApiModelProperty(value = "启用/禁用")
    private boolean enableFlag;

    @ApiModelProperty(value = "描述/备注")
    private String remark;

}
