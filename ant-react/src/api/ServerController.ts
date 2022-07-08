import {AxiosRequestConfig} from "axios";
import $http from "../../util/HttpUtil";

export interface ServerWorkInfoVO {
    cpuUsed?: number // CPU使用率（0-100）
    diskTotal?: number // 磁盘总量（字节）
    diskUsable?: number // 磁盘可以使用总量（字节）
    diskUsed?: number // 磁盘已经使用总量（字节）
    jvmFreeMemory?: number // JVM中内存剩余大小（字节）
    jvmTotalMemory?: number // JVM中内存总大小（字节）
    jvmUsedMemory?: number // JVM中内存已经使用大小（字节）
    memoryAvailable?: number // 系统可用内存（字节）
    memoryTotal?: number // 系统总内存（字节）
    memoryUsed?: number // 系统已经使用内存（字节）
}

// 服务器-管理 服务器运行情况
export function serverWorkInfo(config?: AxiosRequestConfig) {
    return $http.myPost<ServerWorkInfoVO>('/server/workInfo', undefined, config)
}
