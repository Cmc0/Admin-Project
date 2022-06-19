package com.admin.user.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserBaseInfoVO {

    @ApiModelProperty(value = "头像url")
    private String avatarUrl;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "个人简介")
    private String bio;

    @ApiModelProperty(value = "邮箱，会被脱敏")
    private String email;

    @ApiModelProperty(value = "是否有密码，用于前端显示，修改密码/设置密码")
    private boolean passwordFlag;

}
