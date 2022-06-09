package com.cmc.websocket.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cmc.common.model.entity.BaseEntityThree;
import com.cmc.common.model.enums.RequestCategoryEnum;
import com.cmc.websocket.model.enums.WebSocketTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_web_socket")
@Data
@ApiModel(description = "WebSocket 连接记录主表")
public class WebSocketDO extends BaseEntityThree {

    @ApiModelProperty(value = "用户id")
    private Long userId;

    /**
     * {@link com.cmc.common.util.IpUtil#getRegion()}
     */
    @ApiModelProperty(value = "IpUtil.getRegion() 获取到的 ip所处区域")
    private String region;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "浏览器和浏览器版本，用 / 分隔表示")
    private String browser;

    @ApiModelProperty(value = "操作系统")
    private String os;

    @ApiModelProperty(value = "是否是移动端网页，true：是 false 否")
    private Boolean mobileFlag;

    @ApiModelProperty(value = "在线状态")
    private WebSocketTypeEnum type;

    @ApiModelProperty(value = "本次 Websocket连接的服务器的 ip:port")
    private String server;

    @ApiModelProperty(value = "类别")
    private RequestCategoryEnum category;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "连接中/断开连接")
    private Boolean enableFlag;

    @ApiModelProperty(value = "jwtHash，用于匹配 redis中存储的 jwtHash")
    private String jwtHash;

}
