import {AxiosRequestConfig} from "axios";
import $http from "../../util/HttpUtil";

export interface SysFileDownloadDTO {
    url?: string // 文件路径（包含文件名），例如：/bucketName/userId/folderName/fileName.xxx
}

export interface SysFileRemoveDTO {
    urlSet?: string[] // 文件路径（包含文件名） set
}

// 文件-管理 文件批量删除
export function sysFileRemove(form: SysFileRemoveDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysFile/remove', form, config)
}

// 文件-管理 文件上传
export function sysFileUpload(config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysFile/upload', undefined, config)
}
