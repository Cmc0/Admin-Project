package com.admin.bulletin.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@TableName(value = "sys_bulletin_read_time_ref_user")
@Data
@ApiModel(description = "公告，用户关联表")
public class SysBulletinReadTimeRefUserDO {

    @TableId
    @ApiModelProperty(value = "用户主键 id")
    private Long userId;

    @ApiModelProperty(value = "用户最近查看公告的时间，目的：统计公告数量时，根据这个时间和公告发布时间来过滤")
    private Date bulletinReadTime;

}
