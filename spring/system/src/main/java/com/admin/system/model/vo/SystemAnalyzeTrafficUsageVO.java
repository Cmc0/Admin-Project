package com.admin.system.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SystemAnalyzeTrafficUsageVO {

    /**
     * {@link com.admin.common.model.enums.SysRequestCategoryEnum}
     */
    @ApiModelProperty(value = "请求类别：1 H5（网页端） 2 APP（移动端） 3 PC（桌面程序） 4 微信小程序")
    private String categoryStr;

    @ApiModelProperty(value = "该类别请求总数")
    private long total;

}
