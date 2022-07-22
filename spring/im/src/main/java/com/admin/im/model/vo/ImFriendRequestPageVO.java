package com.admin.im.model.vo;

import com.admin.im.model.document.ImElasticsearchFriendRequestDocument;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ImFriendRequestPageVO extends ImElasticsearchFriendRequestDocument {

    @ApiModelProperty(value = "elasticsearch id")
    private String id;

}
