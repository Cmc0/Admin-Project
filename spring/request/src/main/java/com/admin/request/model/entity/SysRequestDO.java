package com.admin.request.model.entity;

import com.admin.common.model.entity.BaseEntityThree;
import com.admin.common.model.enums.SysRequestCategoryEnum;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_request")
@Data
@ApiModel(description = "接口请求记录主表")
public class SysRequestDO extends BaseEntityThree {

    @ApiModelProperty(value = "请求的uri")
    private String uri;

    @ApiModelProperty(value = "耗时（字符串）")
    private String timeStr;

    @ApiModelProperty(value = "耗时（毫秒）")
    private Long timeNumber;

    @ApiModelProperty(value = "接口名（备用）")
    private String name;

    @ApiModelProperty(value = "类别：1 H5（网页端） 2 APP（移动端） 3 PC（桌面程序） 4 微信小程序")
    private SysRequestCategoryEnum category;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "IpUtil.getRegion() 获取到的 ip所处区域")
    private String region;

}
