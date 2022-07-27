package com.admin.im.model.dto;

import com.admin.im.model.enums.ImToTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class MessageBatchDeleteDTO {

    @NotNull
    @ApiModelProperty(value = "目标对象 id")
    private String toId;

    @NotNull
    @ApiModelProperty(value = "目标对象类型：1 好友 2 群组")
    private ImToTypeEnum toType;

    @NotEmpty
    @ApiModelProperty(value = "消息 idSet，elasticsearch id，备注：uuid")
    private Set<String> messageIdSet;

}
