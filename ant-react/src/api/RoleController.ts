import MyOrderDTO from "@/model/dto/MyOrderDTO";
import MyPageDTO from "@/model/dto/MyPageDTO";
import NotNullId from "@/model/dto/NotNullId";
import NotEmptyIdSet from "@/model/dto/NotEmptyIdSet";
import $http from "../../util/HttpUtil";

// 角色-管理 批量删除
export function roleDeleteByIdSet(form: NotEmptyIdSet) {
    return $http.myPost<string>('/role/deleteByIdSet', form)
}

export interface RolePageVO {
    createId?: number // 创建人id
    createTime?: string // 创建时间
    defaultFlag?: boolean // 是否是默认角色，备注：只会有一个默认角色
    delFlag?: boolean // 是否逻辑删除
    enableFlag?: boolean // 启用/禁用
    id?: number // 主键id
    menuIdSet?: number[] // 菜单 idSet
    name?: string // 角色名（不能重复）
    remark?: string // 描述/备注
    updateId?: number // 修改人id
    updateTime?: string // 修改时间
    userIdSet?: number[] // 用户 idSet
    version?: number // 乐观锁
}

// 角色-管理 通过主键id，查看详情
export function roleInfoById(form: NotNullId) {
    return $http.myProPost<RolePageVO>('/role/infoById', form)
}

export interface RoleInsertOrUpdateDTO {
    defaultFlag?: boolean // 是否是默认角色，备注：只会有一个默认角色
    enableFlag?: boolean // 启用/禁用
    id?: number // 主键id
    menuIdSet?: number[] // 菜单 idSet
    name?: string // 角色名，不能重复
    remark?: string // 描述/备注
    userIdSet?: number[] // 用户 idSet
}

// 角色-管理 新增/修改
export function roleInsertOrUpdate(form: RoleInsertOrUpdateDTO) {
    return $http.myPost<string>('/role/insertOrUpdate', form)
}

export interface RolePageDTO extends MyPageDTO {
    current?: number // 第几页
    defaultFlag?: boolean // 是否是默认角色，备注：只会有一个默认角色
    enableFlag?: boolean // 启用/禁用
    id?: number // 主键id
    name?: string // 角色名（不能重复）
    order?: MyOrderDTO // 排序字段
    pageSize?: number // 每页显示条数
    remark?: string // 描述/备注
}

export interface BaseRoleDO {
    createId?: number // 创建人id
    createTime?: string // 创建时间
    defaultFlag?: boolean // 是否是默认角色，备注：只会有一个默认角色
    delFlag?: boolean // 是否逻辑删除
    enableFlag?: boolean // 启用/禁用
    id?: number // 主键id
    name?: string // 角色名（不能重复）
    remark?: string // 描述/备注
    updateId?: number // 修改人id
    updateTime?: string // 修改时间
    version?: number // 乐观锁
}

// 角色-管理 分页排序查询
export function rolePage(form: RolePageDTO) {
    return $http.myProPagePost<BaseRoleDO>('/role/page', form)
}
