package com.admin.user.model.dto;

import com.admin.common.model.dto.MyPageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * {@link com.admin.common.model.entity.SysUserDO}
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserPageDTO extends MyPageDTO {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "正常/冻结")
    private Boolean enableFlag;

    @ApiModelProperty(value = "是否追加 admin账号，备注：pageSize == -1 时生效")
    private boolean addAdminFlag;

}
