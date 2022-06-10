package com.admin.websocket.model.vo;

import com.admin.websocket.model.entity.WebSocketDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class WebSocketPageVO extends WebSocketDO {

    @ApiModelProperty(value = "用户名")
    private String userName;

}
