package com.admin.im.model.dto;

import com.admin.im.model.enums.ImToTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ImSendDTO {

    @NotBlank
    @ApiModelProperty(value = "发送的内容")
    private String content;

    @NotNull
    @ApiModelProperty(value = "即时通讯 发送对象类型，1 联系人 2 群组")
    private ImToTypeEnum toType;

    @Min(1)
    @NotNull
    @ApiModelProperty(value = "给谁发送，c/gId，联系人/群组 id")
    private Long toId;

}
