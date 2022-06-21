package com.admin.websocket.model.dto;

import com.admin.common.model.dto.MyPageDTO;
import com.admin.common.model.enums.SysRequestCategoryEnum;
import com.admin.websocket.model.entity.SysWebSocketDO;
import com.admin.websocket.model.enums.SysWebSocketTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;

/**
 * {@link SysWebSocketDO}
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysWebSocketPageDTO extends MyPageDTO {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "用户id")
    private Long userId;

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

    /**
     * {@link SysWebSocketTypeEnum}
     */
    @Min(1)
    @ApiModelProperty(value = "状态：1 在线 2 隐身")
    private Byte type;

    @ApiModelProperty(value = "本次 Websocket连接的服务器的 ip:port")
    private String server;

    @ApiModelProperty(value = "连接中/断开连接")
    private Boolean enableFlag;

    /**
     * {@link SysRequestCategoryEnum}
     */
    @Min(1)
    @ApiModelProperty(value = "类别：1 H5（网页端） 2 APP（移动端） 3 PC（桌面程序） 4 微信小程序")
    private Byte category;

}
