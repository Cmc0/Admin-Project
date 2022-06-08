package com.cmc.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@TableName(value = "user_info")
@Data
@ApiModel(description = "用户基本信息（user_id 子表）")
public class BaseUserInfoDO {

    @TableId(type = IdType.INPUT)
    @ApiModelProperty(value = "用户主键 id")
    private Long userId;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "修改人id")
    private Long updateId;

    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Version
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "乐观锁")
    private Integer version;

}
