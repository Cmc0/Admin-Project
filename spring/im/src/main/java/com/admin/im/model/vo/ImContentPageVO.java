package com.admin.im.model.vo;

import com.admin.im.model.document.ImElasticsearchMsgDocument;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ImContentPageVO extends ImElasticsearchMsgDocument {

    @ApiModelProperty(value = "elasticsearch id，备注：uuid")
    private String id;

}
