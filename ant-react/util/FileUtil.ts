// 下载文件：需要这样请求 $http({responseType: 'blob'})
// 使用：download(res.data, res.headers['content-disposition'])
import $http from "./HttpUtil";
import {SysFileDownloadDTO} from "@/api/SysFileController";

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

export const ImageFileTypeList = ['image/jpeg', 'image/png']

// 检查：图片文件的文件类型
export function CheckImageFileType(imageFileType: string) {
    return ImageFileTypeList.includes(imageFileType)
}

// 检查：图片文件的文件类型，2097152（字节）= 2MB
export function CheckImageFileSize(imageFileSize: number, maxSize: number = 2097152) {
    return imageFileSize <= maxSize
}

// 通过 url，获取文件的 url，前提是：url以 /public/ 开头
export function GetPublicDownFileUrl(url?: string) {
    if (!url) {
        return ''
    }
    return import.meta.env.VITE_API_BASE_URL + '/sysFile/publicDownload?url=' + url
}
