import MyOrderDTO from "@/model/dto/MyOrderDTO";
import NotNullId from "@/model/dto/NotNullId";
import NotEmptyIdSet from "@/model/dto/NotEmptyIdSet";
import AddOrderNoDTO from "@/model/dto/AddOrderNoDTO";
import $http from "../../util/HttpUtil";

// 菜单-管理 通过主键 idSet，加减排序号
export function menuAddOrderNo(form: AddOrderNoDTO) {
    return $http.myPost<string>('/menu/addOrderNo', form)
}

// 菜单-管理 批量删除
export function menuDeleteByIdSet(form: NotEmptyIdSet) {
    return $http.myPost<string>('/menu/deleteByIdSet', form)
}

export interface MenuInfoByIdVO {
    authFlag?: boolean // 是否是权限菜单，权限菜单：不显示，只代表菜单权限
    auths?: string // 权限，多个可用逗号拼接，例如：menu:insertOrUpdate,menu:page,menu:deleteByIdSet,menu:infoById
    children?: BaseMenuDO // 子节点
    createId?: number // 创建人id
    createTime?: string // 创建时间
    delFlag?: boolean // 是否逻辑删除
    enableFlag?: boolean // 启用/禁用
    firstFlag?: boolean // 是否是起始页面，备注：只能存在一个 firstFlag === true 的菜单
    icon?: string // 图标
    id?: number // 主键id
    linkFlag?: boolean // 是否外链，即，打开页面会在一个新的窗口打开
    name?: string // 菜单名
    orderNo?: number // 排序号（值越大越前面，默认为 0）
    parentId?: number // 父节点id（顶级则为0）
    path?: string // 页面的 path，备注：path不能重复
    redirect?: string // 重定向：linkFlag === false 时使用，不必填
    remark?: string // 描述/备注
    roleIdSet?: number[] // 角色 idSet
    router?: string // 路由：linkFlag === false 时使用，不必填
    showFlag?: boolean // 是否显示在 左侧的菜单栏里面，如果为 false，也可以通过 $router.push()访问到
    updateId?: number // 修改人id
    updateTime?: string // 修改时间
    version?: number // 乐观锁
}

// 菜单-管理 通过主键id，查看详情
export function menuInfoById(form: NotNullId) {
    return $http.myPost<MenuInfoByIdVO>('/menu/infoById', form)
}

export interface MenuInsertOrUpdateDTO {
    authFlag?: boolean // 是否是权限菜单，权限菜单：不显示，只代表菜单权限
    auths?: string // 权限，多个可用逗号拼接，例如：menu:insertOrUpdate,menu:page,menu:deleteByIdSet,menu:infoById
    enableFlag?: boolean // 启用/禁用
    firstFlag?: boolean // 是否是起始页面，备注：只能存在一个 firstFlag === true 的菜单
    icon?: string // 图标
    id?: number // 主键id
    linkFlag?: boolean // 是否外链，即，打开页面会在一个新的窗口打开
    name?: string // 菜单名
    orderNo?: number // 排序号（值越大越前面，默认为 0）
    parentId?: number // 父节点id（顶级则为0）
    path?: string // 页面的 path，备注：path不能重复
    redirect?: string // 重定向：linkFlag === false 时使用，不必填
    remark?: string // 描述/备注
    roleIdSet?: number[] // 角色 idSet
    router?: string // 路由：linkFlag === false 时使用，不必填
    showFlag?: boolean // 是否显示在 左侧的菜单栏里面，如果为 false，也可以通过 $router.push()访问到
}

// 菜单-管理 新增/修改
export function menuInsertOrUpdate(form: MenuInsertOrUpdateDTO) {
    return $http.myPost<string>('/menu/insertOrUpdate', form)
}

export interface MenuPageDTO {
    authFlag?: boolean // 是否是权限菜单，权限菜单：不显示，只代表菜单权限
    auths?: string // 权限，多个可用逗号拼接，例如：menu:insertOrUpdate,menu:page,menu:deleteByIdSet,menu:infoById
    current?: number // 第几页
    enableFlag?: boolean // 启用/禁用
    firstFlag?: boolean // 是否是起始页面，备注：只能存在一个 firstFlag === true 的菜单
    id?: number // 主键id
    keyword?: string // 关键字
    linkFlag?: boolean // 是否外链，即，打开页面会在一个新的窗口打开
    name?: string // 菜单名
    order?: MyOrderDTO // 排序字段
    pageSize?: number // 每页显示条数
    parentId?: number // 父节点id（顶级则为0）
    path?: string // 页面的 path，备注：path不能重复
    redirect?: string // 重定向：linkFlag === false 时使用，不必填
    router?: string // 路由：linkFlag === false 时使用，不必填
    showFlag?: boolean // 是否显示在 左侧的菜单栏里面，如果为 false，也可以通过 $router.push()访问到
}

export interface Page«BaseMenuDO» {
    countId?: string // undefined
    current?: number // undefined
    maxLimit?: number // undefined
    optimizeCountSql?: boolean // undefined
    orders?: OrderItem // undefined
    pages?: number // undefined
    records?: BaseMenuDO // undefined
    searchCount?: boolean // undefined
    size?: number // undefined
    total?: number // undefined
}

// 菜单-管理 分页排序查询
export function menuPage(form: MenuPageDTO) {
    return $http.myPost<Page«BaseMenuDO»>('/menu/page', form)
}

export interface MenuPageDTO {
    authFlag?: boolean // 是否是权限菜单，权限菜单：不显示，只代表菜单权限
    auths?: string // 权限，多个可用逗号拼接，例如：menu:insertOrUpdate,menu:page,menu:deleteByIdSet,menu:infoById
    current?: number // 第几页
    enableFlag?: boolean // 启用/禁用
    firstFlag?: boolean // 是否是起始页面，备注：只能存在一个 firstFlag === true 的菜单
    id?: number // 主键id
    keyword?: string // 关键字
    linkFlag?: boolean // 是否外链，即，打开页面会在一个新的窗口打开
    name?: string // 菜单名
    order?: MyOrderDTO // 排序字段
    pageSize?: number // 每页显示条数
    parentId?: number // 父节点id（顶级则为0）
    path?: string // 页面的 path，备注：path不能重复
    redirect?: string // 重定向：linkFlag === false 时使用，不必填
    router?: string // 路由：linkFlag === false 时使用，不必填
    showFlag?: boolean // 是否显示在 左侧的菜单栏里面，如果为 false，也可以通过 $router.push()访问到
}

// 菜单-管理 查询：树结构
export function menuTree(form: MenuPageDTO) {
    return $http.myPost<List«BaseMenuDO»>('/menu/tree', form)
}

// 菜单-管理 获取当前用户绑定的菜单
export function menuUser() {
    return $http.myPost<List«BaseMenuDO»>('/menu/user')
}

