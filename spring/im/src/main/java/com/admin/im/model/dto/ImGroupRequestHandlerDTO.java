package com.admin.im.model.dto;

import com.admin.im.model.enums.ImRequestResultEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ImGroupRequestHandlerDTO {

    @NotBlank
    @ApiModelProperty(value = "群组申请 id，备注：elasticsearch id，备注：gr_createId_gid")
    private String id;

    @NotNull
    @ApiModelProperty(value = "申请结果：1 未决定 2 已同意 3 已拒绝")
    private ImRequestResultEnum result;

}
