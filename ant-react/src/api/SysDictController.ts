import MyOrderDTO from "@/model/dto/MyOrderDTO";
import MyPageDTO from "@/model/dto/MyPageDTO";
import NotNullId from "@/model/dto/NotNullId";
import NotEmptyIdSet from "@/model/dto/NotEmptyIdSet";
import {AxiosRequestConfig} from "axios";
import AddOrderNoDTO from "@/model/dto/AddOrderNoDTO";
import $http from "../../util/HttpUtil";

// 字典-管理 通过主键 idSet，加减排序号
export function sysDictAddOrderNo(form: AddOrderNoDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysDict/addOrderNo', form)
}

// 字典-管理 删除字典
export function sysDictDeleteByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysDict/deleteByIdSet', form)
}

export interface SysDictDO {
    createId?: number // 创建人id
    createTime?: string // 创建时间
    delFlag?: boolean // 是否逻辑删除
    dictKey?: string // 字典 key（不能重复），字典项要冗余这个 key，目的：方便操作
    enableFlag?: boolean // 启用/禁用
    id?: number // 主键id
    name?: string // 字典/字典项 名
    orderNo?: number // 排序号（值越大越前面，默认为 0）
    remark?: string // 描述/备注
    type?: string // 类型：1 字典 2 字典项
    updateId?: number // 修改人id
    updateTime?: string // 修改时间
    value?: boolean // 字典项 value（数字 123...）备注：字典为 -1
    version?: number // 乐观锁
}

// 字典-管理 通过主键id，查看详情
export function sysDictInfoById(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myProPost<SysDictDO>('/sysDict/infoById', form)
}

export interface SysDictInsertOrUpdateDTO {
    dictKey?: string // 字典 key（不能重复），字典项要冗余这个 key，目的：方便操作
    enableFlag?: boolean // 启用/禁用
    id?: number // 主键id
    name?: string // 字典/字典项 名
    orderNo?: number // 排序号（值越大越前面，默认为 0）
    remark?: string // 描述/备注
    type?: boolean // 类型：1 字典 2 字典项
    value?: boolean // 字典项 value（数字 123...）备注：字典为 -1
}

// 字典-管理 新增/修改
export function sysDictInsertOrUpdate(form: SysDictInsertOrUpdateDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysDict/insertOrUpdate', form)
}

export interface SysDictPageDTO extends MyPageDTO {
    current?: number // 第几页
    dictKey?: string // 字典 key（不能重复），字典项要冗余这个 key，目的：方便操作
    enableFlag?: boolean // 启用/禁用
    id?: number // 主键id
    name?: string // 字典/字典项 名
    order?: MyOrderDTO // 排序字段
    pageSize?: number // 每页显示条数
    remark?: string // 描述/备注
    type?: boolean // 类型：1 字典 2 字典项
}

// 字典-管理 分页排序查询
export function sysDictPage(form: SysDictPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysDictDO>('/sysDict/page', form)
}

export interface SysDictTreeVO {
    children?: SysDictTreeVO // 字典子节点
    createId?: number // 创建人id
    createTime?: string // 创建时间
    delFlag?: boolean // 是否逻辑删除
    dictKey?: string // 字典 key（不能重复），字典项要冗余这个 key，目的：方便操作
    enableFlag?: boolean // 启用/禁用
    id?: number // 主键id
    name?: string // 字典/字典项 名
    orderNo?: number // 排序号（值越大越前面，默认为 0）
    remark?: string // 描述/备注
    type?: string // 类型：1 字典 2 字典项
    updateId?: number // 修改人id
    updateTime?: string // 修改时间
    value?: boolean // 字典项 value（数字 123...）备注：字典为 -1
    version?: number // 乐观锁
}

// 字典-管理 查询：树结构
export function sysDictTree(form: SysDictPageDTO, config?: AxiosRequestConfig) {
    return $http.myProTreePost<SysDictTreeVO>('/sysDict/tree', form)
}
