package com.admin.system.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SystemAnalyzeTrafficUsageVO {

    /**
     * {@link com.admin.common.model.enums.SysRequestCategoryEnum}
     */
    @ApiModelProperty(value = "请求类别")
    private String categoryStr;

    @ApiModelProperty(value = "该类别请求总数")
    private long total;

}
