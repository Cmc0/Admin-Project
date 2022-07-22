// 下载文件：需要这样请求 $http({responseType: 'blob'})
// 使用：download(res.data, res.headers['content-disposition'])
import $http from "./HttpUtil";
import {SysFileDownloadDTO, sysFileUpload} from "@/api/SysFileController";
import {RcFile} from "antd/es/upload";

export function download(
    res: any,
    fileName: string = new Date().getTime() + '.xlsx'
) {
    if (!res) {
        throw new Error('download 方法的res参数不能为空')
    }
    const blob = new Blob([res])

    fileName = fileName.includes('filename=')
        ? decodeURIComponent(fileName.split('filename=')[1])
        : fileName

    const link = document.createElement('a')
    link.download = fileName
    link.style.display = 'none'
    link.href = URL.createObjectURL(blob)
    document.body.appendChild(link)
    link.click()
    URL.revokeObjectURL(link.href) // 释放URL 对象
    document.body.removeChild(link)
}

// 文件-管理 文件下载
export function sysFileDownload(form: SysFileDownloadDTO) {
    $http.request({
        url: '/sysFile/download',
        responseType: 'blob',
        method: 'post',
        data: form
    }).then(res => {
        download(res.data, res.headers['content-disposition'])
    })
}

// AVATAR 头像
type TSysFileUploadProType = 'AVATAR'

// 文件-管理 文件上传，二次封装
export function SysFileUploadPro(file: string | RcFile | Blob, type: TSysFileUploadProType) {
    const formData = new FormData()
    formData.append('file', file)
    // formData.append('uploadType', type)
    return new Promise<string>((resolve, reject) => {
        sysFileUpload(formData, {headers: {'Content-Type': 'multipart/form-data'}}).then(res => {
            resolve(res.data)
        }).catch(() => {
            reject()
        })
    })
}

export const AvatarFileTypeList = ["image/jpeg", "image/png", "image/jpg"]

// 检查：头像的文件类型
export function CheckAvatarFileType(avatarFileType: string) {
    return AvatarFileTypeList.includes(avatarFileType)
}

// 检查：文件的文件类型，2097152（字节）= 2MB
export function CheckFileSize(fileSize: number, maxSize: number = 2097152) {
    return fileSize <= maxSize
}

// 通过 url，获取文件的 url，前提是：url以 /public/ 开头
export function GetPublicDownFileUrl(url?: string) {
    if (!url) {
        return ''
    }
    return import.meta.env.VITE_API_BASE_URL + '/sysFile/publicDownload?url=' + url
}
