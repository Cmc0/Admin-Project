import MyOrderDTO from "@/model/dto/MyOrderDTO";
import MyPageDTO from "@/model/dto/MyPageDTO";
import NotNullId from "@/model/dto/NotNullId";
import NotEmptyIdSet from "@/model/dto/NotEmptyIdSet";
import {AxiosRequestConfig} from "axios";
import AddOrderNoDTO from "@/model/dto/AddOrderNoDTO";
import $http from "../../util/HttpUtil";

// 菜单-管理 通过主键 idSet，加减排序号
export function sysMenuAddOrderNo(form: AddOrderNoDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysMenu/addOrderNo', form, config)
}

// 菜单-管理 批量删除
export function sysMenuDeleteByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysMenu/deleteByIdSet', form, config)
}

export interface SysMenuInfoByIdVO {
    authFlag?: boolean // 是否是权限菜单，权限菜单：不显示，只代表菜单权限
    auths?: string // 权限，多个可用逗号拼接，例如：menu:insertOrUpdate,menu:page,menu:deleteByIdSet,menu:infoById
    children?: SysMenuDO[] // 子节点
    createId?: number // 创建人id
    createTime?: string // 创建时间
    delFlag?: boolean // 是否逻辑删除
    enableFlag?: boolean // 启用/禁用
    firstFlag?: boolean // 是否是起始页面，备注：只能存在一个 firstFlag === true 的菜单
    icon?: string // 图标
    id?: number // 主键id
    linkFlag?: boolean // 是否外链，即，打开页面会在一个新的窗口打开，可以配合 router
    name?: string // 菜单名
    orderNo?: number // 排序号（值越大越前面，默认为 0）
    parentId?: number // 父节点id（顶级则为0）
    path?: string // 页面的 path，备注：相同父菜单下，子菜单 path不能重复
    redirect?: string // 重定向，暂时未使用
    remark?: string // 描述/备注
    roleIdSet?: number[] // 角色 idSet
    router?: string // 路由
    showFlag?: boolean // 是否显示在 左侧的菜单栏里面，如果为 false，也可以通过 $router.push()访问到
    updateId?: number // 修改人id
    updateTime?: string // 修改时间
    version?: number // 乐观锁
}

// 菜单-管理 通过主键id，查看详情
export function sysMenuInfoById(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myProPost<SysMenuInfoByIdVO>('/sysMenu/infoById', form, config)
}

export interface SysMenuInsertOrUpdateDTO {
    authFlag?: boolean // 是否是权限菜单，权限菜单：不显示，只代表菜单权限
    auths?: string // 权限，多个可用逗号拼接，例如：menu:insertOrUpdate,menu:page,menu:deleteByIdSet,menu:infoById
    enableFlag?: boolean // 启用/禁用
    firstFlag?: boolean // 是否是起始页面，备注：只能存在一个 firstFlag === true 的菜单
    icon?: string // 图标
    id?: number // 主键id
    linkFlag?: boolean // 是否外链，即，打开页面会在一个新的窗口打开，可以配合 router
    name?: string // 菜单名
    orderNo?: number // 排序号（值越大越前面，默认为 0）
    parentId?: number // 父节点id（顶级则为0）
    path?: string // 页面的 path，备注：相同父菜单下，子菜单 path不能重复
    redirect?: string // 重定向，暂时未使用
    remark?: string // 描述/备注
    roleIdSet?: number[] // 角色 idSet
    router?: string // 路由
    showFlag?: boolean // 是否显示在 左侧的菜单栏里面，如果为 false，也可以通过 $router.push()访问到
}

// 菜单-管理 新增/修改
export function sysMenuInsertOrUpdate(form: SysMenuInsertOrUpdateDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysMenu/insertOrUpdate', form, config)
}

export interface SysMenuPageDTO extends MyPageDTO {
    authFlag?: boolean // 是否是权限菜单，权限菜单：不显示，只代表菜单权限
    auths?: string // 权限，多个可用逗号拼接，例如：menu:insertOrUpdate,menu:page,menu:deleteByIdSet,menu:infoById
    current?: number // 第几页
    enableFlag?: boolean // 启用/禁用
    firstFlag?: boolean // 是否是起始页面，备注：只能存在一个 firstFlag === true 的菜单
    linkFlag?: boolean // 是否外链，即，打开页面会在一个新的窗口打开，可以配合 router
    name?: string // 菜单名
    order?: MyOrderDTO // 排序字段
    pageSize?: number // 每页显示条数
    parentId?: number // 父节点id（顶级则为0）
    path?: string // 页面的 path，备注：相同父菜单下，子菜单 path不能重复
    redirect?: string // 重定向，暂时未使用
    router?: string // 路由
    showFlag?: boolean // 是否显示在 左侧的菜单栏里面，如果为 false，也可以通过 $router.push()访问到
}

export interface SysMenuDO {
    authFlag?: boolean // 是否是权限菜单，权限菜单：不显示，只代表菜单权限
    auths?: string // 权限，多个可用逗号拼接，例如：menu:insertOrUpdate,menu:page,menu:deleteByIdSet,menu:infoById
    children?: SysMenuDO[] // 子节点
    createId?: number // 创建人id
    createTime?: string // 创建时间
    delFlag?: boolean // 是否逻辑删除
    enableFlag?: boolean // 启用/禁用
    firstFlag?: boolean // 是否是起始页面，备注：只能存在一个 firstFlag === true 的菜单
    icon?: string // 图标
    id?: number // 主键id
    linkFlag?: boolean // 是否外链，即，打开页面会在一个新的窗口打开，可以配合 router
    name?: string // 菜单名
    orderNo?: number // 排序号（值越大越前面，默认为 0）
    parentId?: number // 父节点id（顶级则为0）
    path?: string // 页面的 path，备注：相同父菜单下，子菜单 path不能重复
    redirect?: string // 重定向，暂时未使用
    remark?: string // 描述/备注
    router?: string // 路由
    showFlag?: boolean // 是否显示在 左侧的菜单栏里面，如果为 false，也可以通过 $router.push()访问到
    updateId?: number // 修改人id
    updateTime?: string // 修改时间
    version?: number // 乐观锁
}

// 菜单-管理 分页排序查询
export function sysMenuPage(form: SysMenuPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysMenuDO>('/sysMenu/page', form, config)
}

// 菜单-管理 查询：树结构
export function sysMenuTree(form: SysMenuPageDTO, config?: AxiosRequestConfig) {
    return $http.myProTreePost<SysMenuDO>('/sysMenu/tree', form, config)
}

// 菜单-管理 获取：当前用户绑定的菜单
export function sysMenuUserSelfMenuList(config?: AxiosRequestConfig) {
    return $http.myProTreePost<SysMenuDO>('/sysMenu/userSelfMenuList', undefined, config)
}
