package com.cmc.common.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@TableName(value = "notify_ref_user")
@Data
@ApiModel(description = "通知，用户关联表")
public class NotifyRefUserDO {

    @TableId
    @ApiModelProperty(value = "通知主键 id")
    private Long notifyId;

    @ApiModelProperty(value = "用户主键 id")
    private Long userId;

    @ApiModelProperty(value = "是否已读")
    private Boolean readFlag;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "已读时间")
    private Date updateTime;

}
