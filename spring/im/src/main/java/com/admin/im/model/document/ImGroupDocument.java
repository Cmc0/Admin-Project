package com.admin.im.model.document;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ImGroupDocument {

    @ApiModelProperty(value = "elasticsearch id，备注：uuid")
    private String id;

    @ApiModelProperty(value = "群组名称")
    private String name;

    @ApiModelProperty(value = "创建人 id")
    private Long createId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "头像url")
    private String avatarUrl;

}
