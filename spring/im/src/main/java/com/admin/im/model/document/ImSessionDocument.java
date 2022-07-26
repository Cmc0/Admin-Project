package com.admin.im.model.document;

import com.admin.im.model.enums.ImMessageCreateTypeEnum;
import com.admin.im.model.enums.ImToTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ImSessionDocument {

    @ApiModelProperty(value = "elasticsearch id，备注：toType_createId_toId")
    private String id;

    @ApiModelProperty(value = "会话归属用户 id")
    private Long createId;

    @ApiModelProperty(value = "会话目标对象 id")
    private String toId;

    @ApiModelProperty(value = "目标对象类型：1 好友 2 群组")
    private ImToTypeEnum type;

    @ApiModelProperty(value = "未读消息的总数")
    private long unreadTotal;

    @ApiModelProperty(value = "最后一次聊天的内容")
    private String lastContent;

    @ApiModelProperty(value = "最后一次聊天内容的创建时间")
    private Date lastContentCreateTime;

    @ApiModelProperty(value = "消息创建来源：1 用户 2 通过验证 3 创建完成")
    private ImMessageCreateTypeEnum lastContentCreateType;

}
