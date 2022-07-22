package com.admin.im.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * {@link com.admin.im.model.document.ImElasticsearchFriendRequestDocument}
 */
@Data
public class ImFriendRequestDTO {

    @ApiModelProperty(value = "好友申请内容")
    private String content;

    @ApiModelProperty(value = "冗余字段")
    private Long toId;

}
