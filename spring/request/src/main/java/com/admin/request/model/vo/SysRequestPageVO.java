package com.admin.request.model.vo;

import com.admin.request.model.entity.SysRequestDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysRequestPageVO extends SysRequestDO {

    @ApiModelProperty(value = "用户昵称")
    private String userNickname;

}
