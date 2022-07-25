package com.admin.im.model.document;

import com.admin.im.model.enums.ImToTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ImSessionDocument {

    @ApiModelProperty(value = "elasticsearch id，备注：uuid")
    private String id;

    @ApiModelProperty(value = "会话归属用户 id")
    private Long createId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "会话目标对象 id")
    private Long toId;

    @ApiModelProperty(value = "目标对象类型：1 好友 2 群组")
    private ImToTypeEnum type;

    @ApiModelProperty(value = "未读消息的总数")
    private long unreadTotal;

    @ApiModelProperty(value = "方便检索一个会话下的所有消息，组成：好友 toType_(toId + createId) 群组 toType_toId")
    private String qId;

}
