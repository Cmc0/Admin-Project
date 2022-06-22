import MyOrderDTO from "@/model/dto/MyOrderDTO";
import MyPageDTO from "@/model/dto/MyPageDTO";
import NotNullId from "@/model/dto/NotNullId";
import NotEmptyIdSet from "@/model/dto/NotEmptyIdSet";
import AddOrderNoDTO from "@/model/dto/AddOrderNoDTO";
import $http from "../../util/HttpUtil";

// 区域-管理 通过主键 idSet，加减排序号
export function sysAreaAddOrderNo(form: AddOrderNoDTO) {
    return $http.myPost<string>('/sysArea/addOrderNo', form)
}

// 区域-管理 批量删除
export function sysAreaDeleteByIdSet(form: NotEmptyIdSet) {
    return $http.myPost<string>('/sysArea/deleteByIdSet', form)
}

export interface SysAreaInfoByIdVO {
    children?: SysAreaDO // 子节点
    createId?: number // 创建人id
    createTime?: string // 创建时间
    delFlag?: boolean // 是否逻辑删除
    deptIdSet?: number[] // 部门 idSet
    enableFlag?: boolean // 启用/禁用
    id?: number // 主键id
    name?: string // 区域名称
    orderNo?: number // 排序号（值越大越前面，默认为 0）
    parentId?: number // 父节点id（顶级则为0）
    remark?: string // 描述/备注
    updateId?: number // 修改人id
    updateTime?: string // 修改时间
    version?: number // 乐观锁
}

// 区域-管理 通过主键id，查看详情
export function sysAreaInfoById(form: NotNullId) {
    return $http.myProPost<SysAreaInfoByIdVO>('/sysArea/infoById', form)
}

export interface SysAreaInsertOrUpdateDTO {
    deptIdSet?: number[] // 部门 idSet
    enableFlag?: boolean // 启用/禁用
    id?: number // 主键id
    name?: string // 区域名
    orderNo?: number // 排序号（值越大越前面，默认为 0）
    parentId?: number // 父节点id（顶级则为0）
    remark?: string // 描述/备注
}

// 区域-管理 新增/修改
export function sysAreaInsertOrUpdate(form: SysAreaInsertOrUpdateDTO) {
    return $http.myPost<string>('/sysArea/insertOrUpdate', form)
}

export interface SysAreaPageDTO extends MyPageDTO {
    current?: number // 第几页
    enableFlag?: boolean // 启用/禁用
    id?: number // 主键id
    name?: string // 区域名
    order?: MyOrderDTO // 排序字段
    pageSize?: number // 每页显示条数
    remark?: string // 描述/备注
}

export interface SysAreaDO {
    name?: string // 区域名称
}

// 区域-管理 分页排序查询
export function sysAreaPage(form: SysAreaPageDTO) {
    return $http.myProPagePost<SysAreaDO>('/sysArea/page', form)
}

// 区域-管理 查询：树结构
export function sysAreaTree(form: SysAreaPageDTO) {
    return $http.myProTreePost<SysAreaDO>('/sysArea/tree', form)
}
