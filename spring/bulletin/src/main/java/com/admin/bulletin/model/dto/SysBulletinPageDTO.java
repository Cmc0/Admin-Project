package com.admin.bulletin.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * {@link com.admin.bulletin.model.entity.SysBulletinDO}
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysBulletinPageDTO extends SysBulletinUserSelfPageDTO {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "公告状态：1 草稿 2 公示")
    private Byte status;

    @ApiModelProperty(value = "xxlJobId")
    private Long xxlJobId;

    @ApiModelProperty(value = "描述/备注")
    private String remark;

    @ApiModelProperty(value = "创建人id")
    private Long createId;

}
