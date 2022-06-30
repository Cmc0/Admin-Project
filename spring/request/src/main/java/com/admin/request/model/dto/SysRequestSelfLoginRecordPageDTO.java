package com.admin.request.model.dto;

import com.admin.common.model.dto.MyPageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * {@link com.admin.request.model.entity.SysRequestDO}
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysRequestSelfLoginRecordPageDTO extends MyPageDTO {

    /**
     * {@link com.admin.common.model.enums.SysRequestCategoryEnum}
     */
    @ApiModelProperty(value = "类别：1 H5（网页端） 2 APP（移动端） 3 PC（桌面程序） 4 微信小程序")
    private Byte category;

    @ApiModelProperty(value = "IpUtil.getRegion() 获取到的 ip所处区域")
    private String region;

    @ApiModelProperty(value = "ip")
    private String ip;

}
