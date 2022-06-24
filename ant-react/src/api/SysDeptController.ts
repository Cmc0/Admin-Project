import MyOrderDTO from "@/model/dto/MyOrderDTO";
import MyPageDTO from "@/model/dto/MyPageDTO";
import NotNullId from "@/model/dto/NotNullId";
import NotEmptyIdSet from "@/model/dto/NotEmptyIdSet";
import {AxiosRequestConfig} from "axios";
import AddOrderNoDTO from "@/model/dto/AddOrderNoDTO";
import $http from "../../util/HttpUtil";

// 部门-管理 通过主键 idSet，加减排序号
export function sysDeptAddOrderNo(form: AddOrderNoDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysDept/addOrderNo', form, config)
}

// 部门-管理 批量删除
export function sysDeptDeleteByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysDept/deleteByIdSet', form, config)
}

export interface SysDeptDO {
    name?: string // 部门名
}

// 部门-管理 通过主键id，查看详情
export function sysDeptInfoById(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myProPost<SysDeptDO>('/sysDept/infoById', form, config)
}

export interface SysDeptInsertOrUpdateDTO {
    areaIdSet?: number[] // 区域 idSet
    enableFlag?: boolean // 启用/禁用
    id?: number // 主键id
    name?: string // 部门名
    orderNo?: number // 排序号（值越大越前面，默认为 0）
    parentId?: number // 父节点id（顶级则为0）
    remark?: string // 描述/备注
    userIdSet?: number[] // 用户 idSet
}

// 部门-管理 新增/修改
export function sysDeptInsertOrUpdate(form: SysDeptInsertOrUpdateDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysDept/insertOrUpdate', form, config)
}

export interface SysDeptPageDTO extends MyPageDTO {
    current?: number // 第几页
    enableFlag?: boolean // 启用/禁用
    id?: number // 主键id
    name?: string // 部门名
    order?: MyOrderDTO // 排序字段
    pageSize?: number // 每页显示条数
    remark?: string // 描述/备注
}

// 部门-管理 分页排序查询
export function sysDeptPage(form: SysDeptPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysDeptDO>('/sysDept/page', form, config)
}

// 部门-管理 查询：树结构
export function sysDeptTree(form: SysDeptPageDTO, config?: AxiosRequestConfig) {
    return $http.myProTreePost<SysDeptDO>('/sysDept/tree', form, config)
}
