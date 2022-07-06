package com.admin.server.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ServerWorkInfoVO {

    @ApiModelProperty(value = "JVM中内存总大小（字节）")
    private long jvmTotalMemory;

    @ApiModelProperty(value = "JVM中内存剩余大小（字节）")
    private long jvmFreeMemory;

    @ApiModelProperty(value = "系统总内存（字节）")
    private long memoryTotal;

    @ApiModelProperty(value = "系统可用内存（字节）")
    private long memoryAvailable;

    @ApiModelProperty(value = "CPU使用率（0-100）")
    private double cpuUsed;

    @ApiModelProperty(value = "磁盘总量（字节）")
    private long diskTotal;

    @ApiModelProperty(value = "磁盘可以使用总量（字节）")
    private long diskUsable;

}
