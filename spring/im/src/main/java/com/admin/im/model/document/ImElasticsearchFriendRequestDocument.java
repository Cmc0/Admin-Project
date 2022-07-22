package com.admin.im.model.document;

import com.admin.im.model.enums.ImFriendRequestResultEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ImElasticsearchFriendRequestDocument {

    @ApiModelProperty(value = "创建用户 id")
    private Long createId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "好友申请内容")
    private String content;

    @ApiModelProperty(value = "冗余字段")
    private Long toId;

    @ApiModelProperty(value = "好友申请结果")
    private ImFriendRequestResultEnum result;

}
