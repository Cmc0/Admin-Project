package com.admin.request.model.dto;

import com.admin.common.model.dto.MyPageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * {@link com.admin.request.model.entity.SysRequestDO}
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysRequestPageDTO extends MyPageDTO {

    @ApiModelProperty(value = "请求的uri")
    private String uri;

    @ApiModelProperty(value = "耗时开始（毫秒）")
    private Long beginTimeNumber;

    @ApiModelProperty(value = "耗时结束（毫秒）")
    private Long endTimeNumber;

    @ApiModelProperty(value = "接口名（备用）")
    private String name;

    @ApiModelProperty(value = "创建开始时间")
    private Date beginCreateTime;

    @ApiModelProperty(value = "创建结束时间")
    private Date endCreateTime;

    @ApiModelProperty(value = "创建人id")
    private Long createId;

    /**
     * {@link com.admin.common.model.enums.SysRequestCategoryEnum}
     */
    @ApiModelProperty(value = "类别：1 H5（网页端） 2 APP（移动端） 3 PC（桌面程序） 4 微信小程序")
    private Byte category;

    @ApiModelProperty(value = "IpUtil.getRegion() 获取到的 ip所处区域")
    private String region;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "请求是否成功")
    private Boolean successFlag;

}
