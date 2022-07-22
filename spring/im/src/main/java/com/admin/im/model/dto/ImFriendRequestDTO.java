package com.admin.im.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * {@link com.admin.im.model.document.ImElasticsearchFriendRequestDocument}
 */
@Data
public class ImFriendRequestDTO {

    @ApiModelProperty(value = "好友申请内容")
    private String content;

    @Min(1)
    @NotNull
    @ApiModelProperty(value = "发送好友申请 用户主键 id")
    private Long toId;

}
