package com.admin.system.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SystemAnalyzeUserVO {

    @ApiModelProperty(value = "总用户数")
    private long total;

    @ApiModelProperty(value = "昨日新增用户")
    private long yesterdayAddTotal;

    @ApiModelProperty(value = "每日新增用户")
    private double dailyAddTotal;

    @ApiModelProperty(value = "昨日注销用户")
    private long yesterdayDeleteTotal;

    @ApiModelProperty(value = "每日注销用户")
    private double dailyDeleteTotal;

}
