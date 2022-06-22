package com.admin.request.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SysRequestAllAvgVO {

    @ApiModelProperty(value = "请求的总数")
    private int count;

    @ApiModelProperty(value = "请求的平均耗时（毫秒）")
    private int avg;

}
