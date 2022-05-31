import {RequestData} from '@ant-design/pro-components'
import axios, {AxiosInstance, AxiosRequestConfig, AxiosResponse} from 'axios'
import {ToastError} from './ToastUtil'
import MyPageDTO from "@/model/dto/MyPageDTO";

export interface IResVO<T = string> {
    code: number
    success: boolean
    msg: string
    data: T
}

export const timeoutMsg = '请求超时，请重试 ٩(๑❛ᴗ❛๑)۶'
export const baseErrorMsg = "请求错误 (灬ꈍ ꈍ灬)："

const config: AxiosRequestConfig = {
    baseURL: import.meta.env.DEV ? '/api' : '',
    timeout: 15 * 60 * 1000, // 默认 15分钟
}

const $http = axios.create(config) as MyAxiosInstance

// 请求拦截器
$http.interceptors.request.use(
    (config) => {
        return config
    },
    (err) => {
        ToastError(baseErrorMsg + err.message, 5)
        return Promise.reject(err)
    }
)

// 响应拦截器
$http.interceptors.response.use(
    (response: AxiosResponse<IResVO>) => {
        const res = response.data
        if (res.code !== 200 || !res.success) {
            ToastError(res.msg, 5)
            // 这里会 触发 catch，意思是：只要 code 不等于 200 或者 res.success === false，都不会走 then，而去走 catch
            return Promise.reject(new Error(res.msg || 'Error'))
        } else {
            return response
        }
    },
    (err) => {
        // 所有的请求错误，例如 500 404 错误，只要不是 200，都会在这里
        let msg: string = err.message
        if (msg === 'Network Error') {
            msg = '连接异常，请重试 ٩(๑>◡<๑)۶'
        } else if (msg.includes('timeout')) {
            msg = timeoutMsg
        } else if (msg.includes('Request failed with status code')) {
            msg =
                '接口【' +
                msg.substring(msg.length - 3) +
                '】异常，请联系管理员 (๑´ㅂ`๑) '
        }

        ToastError(msg || (baseErrorMsg + err.message), 5)

        return Promise.reject(err) // 这里会触发 catch
    }
)

interface MyAxiosInstance extends AxiosInstance {
    myPost<T, D>(url: string, data?: D, config?: AxiosRequestConfig<D>): Promise<IResVO<T>>

    myTreePost<T, D>(url: string, data?: D, config?: AxiosRequestConfig<D>): Promise<T[]>

    myProTreePost<T, D>(url: string, data?: D, config?: AxiosRequestConfig<D>): Promise<RequestData<T>>

    myPagePost<T, D extends MyPageDTO>(url: string, data?: D, config?: AxiosRequestConfig<D>): Promise<IPageVO<T>>

    myProPagePost<T, D extends MyPageDTO>(url: string, data?: D, config?: AxiosRequestConfig<D>): Promise<RequestData<T>>
}

$http.myPost = <T, D>(url: string, data?: D, config?: AxiosRequestConfig<D>): Promise<IResVO<T>> => {
    return new Promise((resolve, reject) => {
        return $http.post<IResVO, AxiosResponse<IResVO<T>>, D>(url, data, config).then(({data}) => {
            resolve(data)
        }).catch(err => {
            reject(err)
        })
    })
}

export interface IPageVO<T> {
    total: number // 总数
    size: number // 每页显示条数，默认 10
    current: number // 当前页
    records: T[] // 查询数据列表
}

$http.myTreePost = <T, D extends MyPageDTO>(url: string, data?: D, config?: AxiosRequestConfig<D>): Promise<T[]> => {
    return new Promise((resolve, reject) => {
        return $http.post<IResVO, AxiosResponse<IResVO<T[]>>, D>(url, data, config).then(({data}) => {
            resolve(data.data)
        }).catch(err => {
            reject(err)
        })
    })
}

$http.myProTreePost = <T, D extends MyPageDTO>(url: string, data?: D, config?: AxiosRequestConfig<D>): Promise<RequestData<T>> => {
    return new Promise((resolve, reject) => {
        handleProData(data)
        return $http.post<IResVO, AxiosResponse<IResVO<T[]>>, D>(url, data, config).then(({data}) => {
            resolve({
                success: true,
                data: data.data
            })
        }).catch(err => {
            reject(err)
        })
    })
}

$http.myPagePost = <T, D extends MyPageDTO>(url: string, data?: D, config?: AxiosRequestConfig<D>): Promise<IPageVO<T>> => {
    return new Promise((resolve, reject) => {
        return $http.post<IResVO, AxiosResponse<IResVO<IPageVO<T>>>, D>(url, data, config).then(({data}) => {
            resolve(data.data)
        }).catch(err => {
            reject(err)
        })
    })
}

$http.myProPagePost = <T, D extends MyPageDTO>(url: string, data?: D, config?: AxiosRequestConfig<D>): Promise<RequestData<T>> => {
    return new Promise((resolve, reject) => {
        handleProData(data)
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

function handleProData<D extends MyPageDTO>(data?: D) {
    if (data?.sort) {
        const name = Object.keys(data.sort)[0]
        data.order = {name, value: data.sort[name]}
        data.sort = undefined
    }
}

export default $http
