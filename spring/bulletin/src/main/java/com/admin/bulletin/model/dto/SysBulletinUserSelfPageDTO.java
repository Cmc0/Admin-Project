package com.admin.bulletin.model.dto;

import com.admin.common.model.dto.MyPageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * {@link com.admin.bulletin.model.entity.SysBulletinDO}
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysBulletinUserSelfPageDTO extends MyPageDTO {

    @ApiModelProperty(value = "公告类型（字典值）")
    private Byte type;

    @ApiModelProperty(value = "公告内容（富文本）")
    private String content;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "发布时间范围查询：起始时间")
    private Date ptBeginTime;

    @ApiModelProperty(value = "发布时间范围查询：结束时间")
    private Date ptEndTime;

}
