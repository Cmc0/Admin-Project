package com.admin.im.model.vo;

import com.admin.im.model.document.ImFriendDocument;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ImFriendPageVO extends ImFriendDocument {

    @ApiModelProperty(value = "好友的名称")
    private String targetName;

    @ApiModelProperty(value = "好友头像的 url")
    private String targetAvatarUrl;

}
