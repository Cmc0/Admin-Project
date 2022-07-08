package com.admin.system.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SystemAnalyzeActiveUserVO {

    @ApiModelProperty(value = "昨日活跃人数")
    private long yesterdayTotal;

    @ApiModelProperty(value = "每日活跃人数")
    private long dailyTotal;

    @ApiModelProperty(value = "本月活跃人数")
    private long currentMonthTotal;

    @ApiModelProperty(value = "上月活跃人数")
    private long lastMonthTotal;

}
