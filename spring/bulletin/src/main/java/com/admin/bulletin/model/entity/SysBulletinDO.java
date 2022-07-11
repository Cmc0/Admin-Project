package com.admin.bulletin.model.entity;

import com.admin.bulletin.model.enums.SysBulletinStatusEnum;
import com.admin.common.model.entity.BaseEntityThree;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_bulletin")
@Data
@ApiModel(description = "公告主表")
public class SysBulletinDO extends BaseEntityThree {

    @ApiModelProperty(value = "公告类型（字典值）")
    private Byte type;

    @ApiModelProperty(value = "公告内容（富文本）")
    private String content;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "发布时间")
    private Date publishTime;

    @ApiModelProperty(value = "公告状态：1 草稿 2 公示")
    private SysBulletinStatusEnum status;

    @ApiModelProperty(value = "xxlJobId")
    private Long xxlJobId;

}
