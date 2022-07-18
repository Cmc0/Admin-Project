package com.admin.user.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Set;

/**
 * {@link com.admin.common.model.entity.SysUserDO}
 */
@Data
public class SysUserPageVO {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "头像url")
    private String avatarUrl;

    @ApiModelProperty(value = "邮箱，备注：会脱敏")
    private String email;

    @ApiModelProperty(value = "正常/冻结")
    private boolean enableFlag;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "最近活跃时间")
    private Date lastActiveTime;

    @ApiModelProperty(value = "是否有密码")
    private boolean passwordFlag;

    @ApiModelProperty(value = "角色 idSet")
    private Set<Long> roleIdSet;

    @ApiModelProperty(value = "部门 idSet")
    private Set<Long> deptIdSet;

    @ApiModelProperty(value = "岗位 idSet")
    private Set<Long> jobIdSet;

}
