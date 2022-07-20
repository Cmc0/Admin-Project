package com.admin.im.model.document;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class ImElasticsearchBaseDocument {

    @ApiModelProperty(value = "Elasticsearch id")
    private String _id;

    @ApiModelProperty(value = "联系人 idSet")
    private Set<Long> cIdSet = new HashSet<>();

    @ApiModelProperty(value = "群组 idSet")
    private Set<Long> gIdSet = new HashSet<>();

    @ApiModelProperty(value = "会话 idSet")
    private Set<String> sIdSet = new HashSet<>();

}
