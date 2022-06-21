package com.admin.common.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_user")
@Data
@ApiModel(description = "用户主表")
public class SysUserDO extends BaseEntityThree {

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "正常/冻结")
    private Boolean enableFlag;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "是否注销")
    private Boolean delFlag;

    @ApiModelProperty(value = "该用户的 uuid，本系统使用 id，不使用 uuid")
    private String uuid;

    @ApiModelProperty(value = "用户 jwt私钥后缀（simple uuid）")
    private String jwtSecretSuf;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "个人简介")
    private String bio;

    @ApiModelProperty(value = "头像url")
    private String avatarUrl;

    @ApiModelProperty(value = "密码，可为空，如果为空，则登录时需要提示【进行忘记密码操作】")
    private String password;

    @ApiModelProperty(value = "邮箱")
    private String email;

}
