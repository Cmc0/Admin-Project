import MyOrderDTO from "@/model/dto/MyOrderDTO";
import MyPageDTO from "@/model/dto/MyPageDTO";
import NotNullId from "@/model/dto/NotNullId";
import NotEmptyIdSet from "@/model/dto/NotEmptyIdSet";
import {AxiosRequestConfig} from "axios";
import AddOrderNoDTO from "@/model/dto/AddOrderNoDTO";
import $http from "../../util/HttpUtil";

// 岗位-管理 通过主键 idSet，加减排序号
export function sysJobAddOrderNo(form: AddOrderNoDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysJob/addOrderNo', form)
}

// 岗位-管理 批量删除
export function sysJobDeleteByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysJob/deleteByIdSet', form)
}

export interface SysJobInfoByIdVO {
    children?: SysJobDO // 子节点
    createId?: number // 创建人id
    createTime?: string // 创建时间
    delFlag?: boolean // 是否逻辑删除
    enableFlag?: boolean // 启用/禁用
    id?: number // 主键id
    name?: string // 岗位名称
    orderNo?: number // 排序号（值越大越前面，默认为 0）
    parentId?: number // 父节点id（顶级则为0）
    remark?: string // 描述/备注
    updateId?: number // 修改人id
    updateTime?: string // 修改时间
    userIdSet?: number[] // 用户 idSet
    version?: number // 乐观锁
}

// 岗位-管理 通过主键id，查看详情
export function sysJobInfoById(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myProPost<SysJobInfoByIdVO>('/sysJob/infoById', form)
}

export interface SysJobInsertOrUpdateDTO {
    enableFlag?: boolean // 启用/禁用
    id?: number // 主键id
    name?: string // 岗位名
    orderNo?: number // 排序号（值越大越前面，默认为 0）
    parentId?: number // 父节点id（顶级则为0）
    remark?: string // 描述/备注
    userIdSet?: number[] // 用户 idSet
}

// 岗位-管理 新增/修改
export function sysJobInsertOrUpdate(form: SysJobInsertOrUpdateDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysJob/insertOrUpdate', form)
}

export interface SysJobPageDTO extends MyPageDTO {
    current?: number // 第几页
    enableFlag?: boolean // 启用/禁用
    id?: number // 主键id
    name?: string // 岗位名
    order?: MyOrderDTO // 排序字段
    pageSize?: number // 每页显示条数
    remark?: string // 描述/备注
}

export interface SysJobDO {
    name?: string // 岗位名称
}

// 岗位-管理 分页排序查询
export function sysJobPage(form: SysJobPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysJobDO>('/sysJob/page', form)
}

// 岗位-管理 查询：树结构
export function sysJobTree(form: SysJobPageDTO, config?: AxiosRequestConfig) {
    return $http.myProTreePost<SysJobDO>('/sysJob/tree', form)
}
