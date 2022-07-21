package com.admin.im.model.vo;

import com.admin.im.model.document.ImElasticsearchMsgDocument;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ImSessionPageVO extends ImElasticsearchMsgDocument {

    @ApiModelProperty(value = "elasticsearch id")
    private String id;

}
