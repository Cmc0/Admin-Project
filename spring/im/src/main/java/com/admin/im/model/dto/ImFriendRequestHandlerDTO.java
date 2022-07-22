package com.admin.im.model.dto;

import com.admin.im.model.enums.ImFriendRequestResultEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ImFriendRequestHandlerDTO {

    @NotBlank
    @ApiModelProperty(value = "elasticsearch id")
    private String id;

    @NotNull
    @ApiModelProperty(value = "处理结果")
    private ImFriendRequestResultEnum result;

}
