package com.admin.user.model.dto;

import com.admin.common.model.dto.MyPageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * {@link com.admin.common.model.entity.SysUserDO}
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserPageDTO extends MyPageDTO {

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "正常/冻结")
    private Boolean enableFlag;

    @ApiModelProperty(value = "创建开始时间")
    private Date beginCreateTime;

    @ApiModelProperty(value = "创建结束时间")
    private Date endCreateTime;

    @ApiModelProperty(value = "是否有密码")
    private Boolean passwordFlag;

    @ApiModelProperty(value = "最近活跃开始时间")
    private Date beginLastActiveTime;

    @ApiModelProperty(value = "最近活跃结束时间")
    private Date endLastActiveTime;

    @ApiModelProperty(value = "部门主键 id")
    private Long deptId;

    @ApiModelProperty(value = "岗位主键 id")
    private Long jobId;

    @ApiModelProperty(value = "角色主键 id")
    private Long roleId;

}
