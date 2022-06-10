package com.admin.websocket.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WebSocketRegVO {

    @ApiModelProperty(value = "WebSocket 连接地址，ip:port")
    private String socketUrl;

    @ApiModelProperty(value = "WebSocket 连接码，备注：只能使用一次")
    private String code;

}
