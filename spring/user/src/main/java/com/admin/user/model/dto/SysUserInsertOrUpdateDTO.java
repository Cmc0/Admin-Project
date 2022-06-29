package com.admin.user.model.dto;

import com.admin.common.model.constant.BaseRegexConstant;
import com.admin.common.model.dto.BaseInsertOrUpdateDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * {@link com.admin.common.model.entity.SysUserDO,com.admin.common.model.dto.EmailNotBlankDTO,UserRegisterByEmailDTO}
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserInsertOrUpdateDTO extends BaseInsertOrUpdateDTO {

    @Size(max = 200)
    @NotBlank
    @Pattern(regexp = BaseRegexConstant.EMAIL)
    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "前端加密之后的密码")
    private String password;

    @ApiModelProperty(value = "前端加密之后的原始密码")
    private String origPassword;

    @NotBlank
    @Pattern(regexp = BaseRegexConstant.NICK_NAME_REGEXP)
    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "个人简介")
    private String bio;

    @ApiModelProperty(value = "头像url")
    private String avatarUrl;

    @ApiModelProperty(value = "正常/冻结")
    private boolean enableFlag;

    @ApiModelProperty(value = "角色 idSet")
    private Set<Long> roleIdSet;

    @ApiModelProperty(value = "部门 idSet")
    private Set<Long> deptIdSet;

    @ApiModelProperty(value = "岗位 idSet")
    private Set<Long> jobIdSet;

}
