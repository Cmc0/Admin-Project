import MyOrderDTO from "@/model/dto/MyOrderDTO";
import MyPageDTO from "@/model/dto/MyPageDTO";
import NotNullId from "@/model/dto/NotNullId";
import NotEmptyIdSet from "@/model/dto/NotEmptyIdSet";
import AddOrderNoDTO from "@/model/dto/AddOrderNoDTO";
import $http from "../../util/HttpUtil";

// 部门-管理 通过主键 idSet，加减排序号
export function sysDeptAddOrderNo(form: AddOrderNoDTO) {
    return $http.myPost<string>('/sysDept/addOrderNo', form)
}

// 部门-管理 批量删除
export function sysDeptDeleteByIdSet(form: NotEmptyIdSet) {
    return $http.myPost<string>('/sysDept/deleteByIdSet', form)
}

export interface SysDeptDO {
    children?: SysDeptDO // 子节点
    createId?: number // 创建人id
    createTime?: string // 创建时间
    delFlag?: boolean // 是否逻辑删除
    enableFlag?: boolean // 启用/禁用
    id?: number // 主键id
    name?: string // 部门名称
    orderNo?: number // 排序号（值越大越前面，默认为 0）
    parentId?: number // 父节点id（顶级则为0）
    remark?: string // 描述/备注
    updateId?: number // 修改人id
    updateTime?: string // 修改时间
    version?: number // 乐观锁
}

// 部门-管理 通过主键id，查看详情
export function sysDeptInfoById(form: NotNullId) {
    return $http.myProPost<SysDeptDO>('/sysDept/infoById', form)
}

export interface SysDeptInsertOrUpdateDTO {
    areaIdSet?: number[] // 区域 idSet
    enableFlag?: boolean // 启用/禁用
    id?: number // 主键id
    name?: string // 部门名称
    orderNo?: number // 排序号（值越大越前面，默认为 0）
    parentId?: number // 父节点id（顶级则为0）
    remark?: string // 描述/备注
    userIdSet?: number[] // 用户 idSet
}

// 部门-管理 新增/修改
export function sysDeptInsertOrUpdate(form: SysDeptInsertOrUpdateDTO) {
    return $http.myPost<string>('/sysDept/insertOrUpdate', form)
}

export interface SysDeptPageDTO extends MyPageDTO {
    current?: number // 第几页
    enableFlag?: boolean // 启用/禁用
    id?: number // 主键id
    name?: string // 部门名称
    order?: MyOrderDTO // 排序字段
    pageSize?: number // 每页显示条数
    remark?: string // 描述/备注
}

// 部门-管理 分页排序查询
export function sysDeptPage(form: SysDeptPageDTO) {
    return $http.myProPagePost<SysDeptDO>('/sysDept/page', form)
}

// 部门-管理 查询：树结构
export function sysDeptTree(form: SysDeptPageDTO) {
    return $http.myProTreePost<SysDeptDO>('/sysDept/tree', form)
}
