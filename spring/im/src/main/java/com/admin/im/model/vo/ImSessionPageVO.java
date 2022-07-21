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

    @ApiModelProperty(value = "头像地址")
    private String toAvatarUrl;

    @ApiModelProperty(value = "昵称")
    private String toNickname;

}
