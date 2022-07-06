package com.admin.server.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ServerWorkInfoVO {

    @ApiModelProperty(value = "JVM中内存总大小")
    private Long jvmTotalMemory;

    @ApiModelProperty(value = "JVM中内存剩余大小")
    private Long jvmFreeMemory;

    @ApiModelProperty(value = "系统总内存")
    private Long memoryTotal;

    @ApiModelProperty(value = "系统可用内存")
    private Long memoryAvailable;

    @ApiModelProperty(value = "CPU总的使用率")
    private double cpuTotal;

    @ApiModelProperty(value = "磁盘总量")
    private Long diskTotal;

    @ApiModelProperty(value = "磁盘使用总量")
    private Long diskUsing;

}
