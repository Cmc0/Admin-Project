package com.admin.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@TableName(value = "sys_user_login")
@Data
@ApiModel(description = "用户登录表（user_id 子表）")
public class BaseUserLoginDO {

    @TableId(type = IdType.INPUT)
    @ApiModelProperty(value = "用户主键 id")
    private Long userId;

    @ApiModelProperty(value = "账号邮箱")
    private String email;

    @ApiModelProperty(value = "密码，可为空，如果为空，则登录时需要提示【进行忘记密码操作】")
    private String password;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateId;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Version
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "乐观锁")
    private Integer version;

    @ApiModelProperty(value = "手机号")
    private String phone;

}
