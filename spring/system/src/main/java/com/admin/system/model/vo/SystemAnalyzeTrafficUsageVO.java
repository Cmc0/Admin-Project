package com.admin.system.model.vo;

import com.admin.common.model.enums.SysRequestCategoryEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SystemAnalyzeTrafficUsageVO {

    @ApiModelProperty(value = "请求类别")
    private SysRequestCategoryEnum category;

    @ApiModelProperty(value = "该类别请求总数")
    private long total;

}
