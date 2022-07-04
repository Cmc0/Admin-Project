package com.admin.user.model.dto;

import com.admin.common.model.constant.BaseRegexConstant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * {@link com.admin.common.model.entity.SysUserDO}
 */
@Data
public class UserSelfUpdateBaseInfoDTO {

    @ApiModelProperty(value = "头像url")
    private String avatarUrl;

    @NotBlank
    @Pattern(regexp = BaseRegexConstant.NICK_NAME_REGEXP)
    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "个人简介")
    private String bio;

}
