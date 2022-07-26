package com.admin.im.model.document;

import com.admin.im.model.enums.ImRequestResultEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ImFriendRequestDocument {

    @ApiModelProperty(value = "elasticsearch id，备注：fr_createId_toId")
    private String id;

    @ApiModelProperty(value = "申请内容")
    private String content;

    @ApiModelProperty(value = "好友申请目标对象 id")
    private Long toId;

    @ApiModelProperty(value = "好友申请创建人 id")
    private Long createId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "申请结果：1 未决定 2 已同意 3 已拒绝")
    private ImRequestResultEnum result;

    @ApiModelProperty(value = "申请结果处理时间")
    private Date resultTime;

}
