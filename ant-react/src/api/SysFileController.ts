import $http from "../../util/HttpUtil";

export interface SysFileDownloadDTO {
    url?: string // 文件路径（包含文件名），例如：/bucketName/userId/folderName/fileName.xxx
}

// 文件-管理 文件下载
export function fileDownload(form: SysFileDownloadDTO) {
    return $http.myPost('/file/download', form)
}

export interface SysFileRemoveDTO {
    urlSet?: string[] // 文件路径（包含文件名） set
}

// 文件-管理 文件删除
export function fileRemove(form: SysFileRemoveDTO) {
    return $http.myPost<string>('/file/remove', form)
}

// 文件-管理 文件上传
export function fileUpload() {
    return $http.myPost<string>('/file/upload')
}
