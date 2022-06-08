package com.cmc.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "user_security")
@Data
@ApiModel(description = "用户安全以及状态相关（user_id 子表）")
public class BaseUserSecurityDO extends BaseEntity {

    @TableId
    @ApiModelProperty(value = "用户主键id（外键）")
    private Long userId;

    @ApiModelProperty(value = "用户 jwt私钥后缀")
    private String jwtSecretSuf;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "正常/冻结")
    private Boolean enableFlag;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "是否注销")
    private Boolean delFlag;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "描述/备注")
    private String remark;

}
