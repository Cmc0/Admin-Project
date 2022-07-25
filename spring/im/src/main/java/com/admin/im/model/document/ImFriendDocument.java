package com.admin.im.model.document;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ImFriendDocument {

    @ApiModelProperty(value = "elasticsearch id，备注：uuid")
    private String id;

    @ApiModelProperty(value = "好友归属用户 id")
    private Long createId;

    @ApiModelProperty(value = "好友添加时间")
    private Date createTime;

    @ApiModelProperty(value = "好友用户主键 id")
    private Long uId;

    @ApiModelProperty(value = "备注名称")
    private String remark;

}
