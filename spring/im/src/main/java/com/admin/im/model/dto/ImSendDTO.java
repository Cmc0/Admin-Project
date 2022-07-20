package com.admin.im.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class ImSendDTO {

    @ApiModelProperty(value = "发送的内容")
    private String content;

    @Min(1)
    @NotNull
    @ApiModelProperty(value = "给谁发送，c/gId，联系人/群组 id")
    private Long toId;

}
