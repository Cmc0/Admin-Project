package com.admin.im.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * {@link com.admin.im.model.document.ImFriendRequestDocument}
 */
@Data
public class ImFriendRequestDTO {

    @ApiModelProperty(value = "申请内容")
    private String content;

    @Min(1)
    @NotNull
    @ApiModelProperty(value = "好友申请目标对象 id")
    private Long toId;

}
