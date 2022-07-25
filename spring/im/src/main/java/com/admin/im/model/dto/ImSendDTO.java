package com.admin.im.model.dto;

import com.admin.im.model.enums.ImToTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * {@link com.admin.im.model.document.ImMessageDocument}
 */
@Data
public class ImSendDTO {

    @Size(max = 2000)
    @NotBlank
    @ApiModelProperty(value = "内容，最大长度 2000")
    private String content;

    @Size(max = 32)
    @NotBlank
    @ApiModelProperty(value = "目标对象 id")
    private String toId;

    @NotNull
    @ApiModelProperty(value = "目标对象类型：1 好友 2 群组")
    private ImToTypeEnum toType;

}
