package com.admin.system.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SystemAnalyzeActiveUserTrendVO {

    @ApiModelProperty(value = "年月，格式：2022年7月（字符串）")
    private String monthDataStr;

    @ApiModelProperty(value = "该月活跃人数")
    private long total;

}
