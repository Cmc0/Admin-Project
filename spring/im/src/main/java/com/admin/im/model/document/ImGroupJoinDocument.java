package com.admin.im.model.document;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ImGroupJoinDocument {

    @ApiModelProperty(value = "elasticsearch id，备注：uuid")
    private String id;

}
