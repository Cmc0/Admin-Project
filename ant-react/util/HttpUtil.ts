import {RequestData} from '@ant-design/pro-components'
import axios, {AxiosInstance, AxiosRequestConfig, AxiosResponse} from 'axios'
import {ToastError} from './ToastUtil'
import MyPageDTO from "@/model/dto/MyPageDTO";
import LocalStorageKey from "@/model/constant/LocalStorageKey";
import {logout} from "./UserUtil";

export const TIMEOUT_MSG = '请求超时，请重试'
export const BASE_ERROR_MSG = "请求错误："

let hiddenErrorMsgFlag = false

const config: AxiosRequestConfig = {
    baseURL: '/api',
    timeout: 30 * 60 * 1000, // 默认 30分钟
}

const $http = axios.create(config) as MyAxiosInstance

// 请求拦截器
$http.interceptors.request.use(
    (config) => {

        if (!config.url?.startsWith('http')) {
            // 不以 http开头的，才携带 jwt
            config.headers!['Authorization'] =
                localStorage.getItem(LocalStorageKey.JWT) || ''
            config.headers!['category'] = 1 // 类别：1 H5（网页） 2 APP（移动端） 3 PC（桌面程序） 4 微信小程序
        }

        if (config.headers?.hiddenErrorMsg) {
            hiddenErrorMsgFlag = true
        }

        return config
    },
    (err) => {
        ToastError(BASE_ERROR_MSG + err.message, 5)
        return Promise.reject(err)
    }
)

// 响应拦截器
$http.interceptors.response.use(
    (response: AxiosResponse<ApiResultVO>) => {

        const config = response.config

        if (config.url?.startsWith('http')) {
            return response // 如果是 http请求
        }
        if (config.responseType === 'blob') {
            return response // 如果请求的是文件
        }

        const hiddenErrorMsg = config.headers?.hiddenErrorMsg // 是否隐藏错误提示

        // 接口请求报错，是否隐藏错误信息：关闭
        if (hiddenErrorMsg) {
            hiddenErrorMsgFlag = false
        }

        const res = response.data
        if (res.code !== 200 || !res.success) {
            if (res.code === 100111) { // 这个代码需要跳转到：登录页面
                if (localStorage.getItem(LocalStorageKey.JWT)) {
                    logout()
                    ToastError(res.msg, 5)
                }
            } else {
                if (!hiddenErrorMsg) {
                    ToastError(res.msg, 5)
                }
            }
            return Promise.reject(res) // 这里会 触发 catch
        } else {
            return response
        }
    },
    (err) => {
        if (hiddenErrorMsgFlag) {
            return Promise.reject(err) // 这里会触发 catch
        }

        // 所有的请求错误，例如 500 404 错误，超出 2xx 范围的状态码都会触发该函数。
        let msg: string = err.message
        if (msg === 'Network Error') {
            msg = '连接异常，请重试'
        } else if (msg.includes('timeout')) {
            msg = TIMEOUT_MSG
        } else if (msg.includes('Request failed with status code')) {
            msg = '接口【' + msg.substring(msg.length - 3) + '】异常，请联系管理员'
        }

        ToastError(msg || (BASE_ERROR_MSG + err.message), 5)
        return Promise.reject(err) // 这里会触发 catch
    }
)

export interface ApiResultVO<T = string> {
    code: number
    success: boolean
    msg: string
    data: T
}

interface MyAxiosInstance extends AxiosInstance {
    myPost<T = string, D = any>(url: string, data?: D, config?: AxiosRequestConfig<D>): Promise<ApiResultVO<T>>

    myProPost<T = string, D = any>(url: string, data?: D, config?: AxiosRequestConfig<D>): Promise<T>

    myTreePost<T, D extends MyPageDTO = any>(url: string, data?: D, config?: AxiosRequestConfig<D>): Promise<T[]>

    myProTreePost<T, D extends MyPageDTO = any>(url: string, data?: D, config?: AxiosRequestConfig<D>): Promise<RequestData<T>>

    myPagePost<T, D extends MyPageDTO = any>(url: string, data?: D, config?: AxiosRequestConfig<D>): Promise<Page<T>>

    myProPagePost<T, D extends MyPageDTO = any>(url: string, data?: D, config?: AxiosRequestConfig<D>): Promise<RequestData<T>>
}

export interface Page<T> {
    total: number // 总数
    size: number // 每页显示条数，默认 10
    current: number // 当前页
    records: T[] // 查询数据列表
}


$http.myPost = <T = string, D = any>(url: string, data?: D, config?: AxiosRequestConfig<D>): Promise<ApiResultVO<T>> => {
    return new Promise((resolve, reject) => {
        return $http.post<ApiResultVO, AxiosResponse<ApiResultVO<T>>, D>(url, data, config).then(({data}) => {
            resolve(data)
        }).catch(err => {
            reject(err)
        })
    })
}

$http.myProPost = <T = string, D = any>(url: string, data?: D, config?: AxiosRequestConfig<D>): Promise<T> => {
    return new Promise((resolve, reject) => {
        return $http.post<ApiResultVO, AxiosResponse<ApiResultVO<T>>, D>(url, data, config).then(({data}) => {
            resolve(data.data)
        }).catch(err => {
            reject(err)
        })
    })
}

$http.myTreePost = <T, D extends MyPageDTO>(url: string, data?: D, config?: AxiosRequestConfig<D>): Promise<T[]> => {
    return new Promise((resolve, reject) => {
        handleData(data)
        return $http.post<ApiResultVO, AxiosResponse<ApiResultVO<T[]>>, D>(url, data, config).then(({data}) => {
            resolve(data.data)
        }).catch(err => {
            reject(err)
        })
    })
}

$http.myProTreePost = <T, D extends MyPageDTO>(url: string, data?: D, config?: AxiosRequestConfig<D>): Promise<RequestData<T>> => {
    return new Promise((resolve, reject) => {
        handleData(data)
        return $http.post<ApiResultVO, AxiosResponse<ApiResultVO<T[]>>, D>(url, data, config).then(({data}) => {
            resolve({
                success: true,
                data: data.data
            })
        }).catch(err => {
            reject(err)
        })
    })
}

$http.myPagePost = <T, D extends MyPageDTO>(url: string, data?: D, config?: AxiosRequestConfig<D>): Promise<Page<T>> => {
    return new Promise((resolve, reject) => {
        handleData(data)
        return $http.post<ApiResultVO, AxiosResponse<ApiResultVO<Page<T>>>, D>(url, data, config).then(({data}) => {
            resolve(data.data)
        }).catch(err => {
            reject(err)
        })
    })
}

$http.myProPagePost = <T, D extends MyPageDTO>(url: string, data?: D, config?: AxiosRequestConfig<D>): Promise<RequestData<T>> => {
    return new Promise((resolve, reject) => {
        return $http.myPagePost<T, D>(url, data, config).then((res) => {
            resolve({
                success: true,
                total: res.total,
                data: res.records
            })
        }).catch(err => {
            reject(err)
        })
    })
}

function handleData<D extends MyPageDTO>(data?: D) {
    if (data?.sort) {
        const name = Object.keys(data.sort)[0]
        data.order = {name, value: data.sort[name]}
        data.sort = undefined
    }
}

export default $http
