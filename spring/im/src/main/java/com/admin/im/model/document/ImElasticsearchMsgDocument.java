package com.admin.im.model.document;

import com.admin.im.model.enums.ImContentTypeEnum;
import com.admin.im.model.enums.ImToTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
public class ImElasticsearchMsgDocument {

    @ApiModelProperty(value = "创建时间")
    private Date createTime = new Date();

    @ApiModelProperty(value = "创建用户 id")
    private Long createId;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "内容类型")
    private ImContentTypeEnum contentType;

    @ApiModelProperty(value = "已读这条消息的用户 idSet")
    private Set<Long> rIdSet = new HashSet<>();

    @ApiModelProperty(value = "冗余字段")
    private Long toId;

    @ApiModelProperty(value = "冗余字段")
    private ImToTypeEnum toType;

    @ApiModelProperty(value = "冗余字段")
    private String sId;

}
