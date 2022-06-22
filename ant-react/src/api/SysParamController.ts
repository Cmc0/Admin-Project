import MyOrderDTO from "@/model/dto/MyOrderDTO";
import MyPageDTO from "@/model/dto/MyPageDTO";
import NotNullId from "@/model/dto/NotNullId";
import {AxiosRequestConfig} from "axios";
import NotEmptyIdSet from "@/model/dto/NotEmptyIdSet";
import $http from "../../util/HttpUtil";

// 系统参数-管理 批量删除
export function sysParamDeleteByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysParam/deleteByIdSet', form)
}

export interface SysParamDO {
    createId?: number // 创建人id
    createTime?: string // 创建时间
    delFlag?: boolean // 是否逻辑删除
    enableFlag?: boolean // 启用/禁用
    id?: number // 主键id
    name?: string // 配置名，以 id为不变值进行使用，不要用此属性
    remark?: string // 描述/备注
    updateId?: number // 修改人id
    updateTime?: string // 修改时间
    value?: string // 值
    version?: number // 乐观锁
}

// 系统参数-管理 通过主键id，查看详情
export function sysParamInfoById(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myProPost<SysParamDO>('/sysParam/infoById', form)
}

export interface SysParamInsertOrUpdateDTO {
    enableFlag?: boolean // 启用/禁用
    id?: number // 主键id
    name?: string // 配置名，以 id为不变值进行使用，不要用此属性
    remark?: string // 描述/备注
    value?: string // 值
}

// 系统参数-管理 新增/修改
export function sysParamInsertOrUpdate(form: SysParamInsertOrUpdateDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysParam/insertOrUpdate', form)
}

export interface SysParamPageDTO extends MyPageDTO {
    current?: number // 第几页
    enableFlag?: boolean // 启用/禁用
    id?: number // 主键id
    name?: string // 配置名，以 id为不变值进行使用，不要用此属性
    order?: MyOrderDTO // 排序字段
    pageSize?: number // 每页显示条数
    remark?: string // 描述/备注
}

// 系统参数-管理 分页排序查询
export function sysParamPage(form: SysParamPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysParamDO>('/sysParam/page', form)
}
