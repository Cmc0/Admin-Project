package com.admin.common.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@TableName(value = "sys_user_id")
@Data
@ApiModel(description = "用户主表（只存储用户id，创建信息，修改信息，账号状态，都以 user_security 表，为主）")
public class BaseUserIdDO {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "用户主键id")
    private Long id;

    @ApiModelProperty(value = "该用户的 uuid")
    private String uuid;

}
