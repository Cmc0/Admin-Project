package com.admin.bulletin.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * {@link com.admin.bulletin.model.entity.SysBulletinDO}
 */
@Data
public class SysBulletinUserSelfPageVO {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "公告类型（字典值）")
    private Byte type;

    @ApiModelProperty(value = "公告内容（富文本）")
    private String content;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "发布时间")
    private Date publishTime;

}
