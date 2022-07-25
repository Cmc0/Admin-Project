package com.admin.im.model.vo;

import com.admin.im.model.document.ImSessionDocument;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ImSessionPageVO extends ImSessionDocument {

    @ApiModelProperty(value = "最后一次聊天的内容")
    private String lastContent;

    @ApiModelProperty(value = "最后一次聊天内容的创建时间")
    private String lastContentCreateTime;

    @ApiModelProperty(value = "会话对象的名称")
    private String targetName;

    @ApiModelProperty(value = "会话对象的头像 url")
    private String targetAvatarUrl;

    @ApiModelProperty(value = "未读消息的总数")
    private long unreadTotal;

}
