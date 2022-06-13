import BaseEntityFour from "@/model/entity/BaseEntityFour";

export default interface BaseMenuDO extends BaseEntityFour<BaseMenuDO> {
    path?: string // 页面的 path，备注：path不能重复
    auths?: string // 权限，多个可用逗号拼接，例如：menu:insertOrUpdate,menu:page,menu:deleteByIdSet,menu:infoById
    name?: string // 菜单名
    icon?: string // 图标
    showFlag?: boolean // 是否显示在 左侧的菜单栏里面，如果为 false，也可以通过 $router.push()访问到
    linkFlag?: boolean // 是否外链，即，打开页面会在一个新的窗口打开
    router?: string // 路由：linkFlag === false 时使用，不必填
    redirect?: string // 重定向：linkFlag === false 时使用，不必填
    firstFlag?: boolean // 是否是起始页面，备注：只能存在一个 firstFlag === true 的菜单
    authFlag?: boolean // 是否是权限菜单，权限菜单：不显示，只代表菜单权限
}
