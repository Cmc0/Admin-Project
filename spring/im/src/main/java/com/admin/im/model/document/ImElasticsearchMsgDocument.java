package com.admin.im.model.document;

import com.admin.im.model.enums.ImContentTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
public class ImElasticsearchMsgDocument {

    @ApiModelProperty(value = "Elasticsearch id")
    private String _id;

    @ApiModelProperty(value = "创建时间")
    private Date createTime = new Date();

    @ApiModelProperty(value = "创建用户 id")
    private Long createId;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "内容类型")
    private ImContentTypeEnum contentType;

    @ApiModelProperty(value = "联系人 idSet")
    private Set<Long> cIdSet = new HashSet<>();

    @ApiModelProperty(value = "群组 idSet")
    private Set<Long> gIdSet = new HashSet<>();

}
