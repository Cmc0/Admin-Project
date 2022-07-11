import MyOrderDTO from "@/model/dto/MyOrderDTO";
import MyPageDTO from "@/model/dto/MyPageDTO";
import NotNullId from "@/model/dto/NotNullId";
import {AxiosRequestConfig} from "axios";
import NotEmptyIdSet from "@/model/dto/NotEmptyIdSet";
import $http from "../../util/HttpUtil";

// 公告-管理 批量删除
export function sysBulletinDeleteByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysBulletin/deleteByIdSet', form, config)
}

export interface SysBulletinDO {
    content?: string // 公告内容（富文本）
    createId?: number // 创建人id
    createTime?: string // 创建时间
    delFlag?: boolean // 是否逻辑删除
    enableFlag?: boolean // 启用/禁用
    id?: number // 主键id
    publishTime?: string // 发布时间
    remark?: string // 描述/备注
    status?: number // 公告状态：1 草稿 2 公示
    title?: string // 标题
    type?: number // 公告类型（字典值）
    updateId?: number // 修改人id
    updateTime?: string // 修改时间
    version?: number // 乐观锁
    xxlJobId?: number // xxlJobId
}

// 公告-管理 通过主键id，查看详情
export function sysBulletinInfoById(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myProPost<SysBulletinDO>('/sysBulletin/infoById', form, config)
}

export interface SysBulletinInsertOrUpdateDTO {
    content?: string // 公告内容（富文本）
    id?: number // 主键id
    publishTime?: string // 发布时间
    remark?: string // 描述/备注
    title?: string // 标题
    type?: number // 公告类型（字典值）
}

// 公告-管理 新增/修改
export function sysBulletinInsertOrUpdate(form: SysBulletinInsertOrUpdateDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysBulletin/insertOrUpdate', form, config)
}

export interface SysBulletinPageDTO extends MyPageDTO {
    content?: string // 公告内容（富文本）
    createId?: number // 创建人id
    current?: number // 第几页
    order?: MyOrderDTO // 排序字段
    pageSize?: number // 每页显示条数
    ptBeginTime?: string // 发布时间范围查询：起始时间
    ptEndTime?: string // 发布时间范围查询：结束时间
    remark?: string // 描述/备注
    status?: number // 公告状态：1 草稿 2 公示
    title?: string // 标题
    type?: number // 公告类型（字典值）
    xxlJobId?: number // xxlJobId
}

// 公告-管理 分页排序查询
export function sysBulletinPage(form: SysBulletinPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysBulletinDO>('/sysBulletin/page', form, config)
}

// 公告-管理 发布 公告
export function sysBulletinPublish(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysBulletin/publish', form, config)
}

// 公告-管理 撤回 公告
export function sysBulletinRevoke(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysBulletin/revoke', form, config)
}

// 公告-管理 获取：当前用户可以查看的公告，总数
export function sysBulletinUserSelfCount(config?: AxiosRequestConfig) {
    return $http.myPost<number>('/sysBulletin/userSelfCount', undefined, config)
}

export interface SysBulletinUserSelfPageDTO extends MyPageDTO {
    content?: string // 公告内容（富文本）
    current?: number // 第几页
    order?: MyOrderDTO // 排序字段
    pageSize?: number // 每页显示条数
    ptBeginTime?: string // 发布时间范围查询：起始时间
    ptEndTime?: string // 发布时间范围查询：结束时间
    title?: string // 标题
    type?: number // 公告类型（字典值）
}

// 公告-管理 分页排序查询：当前用户可以查看的公告
export function sysBulletinUserSelfPage(form: SysBulletinUserSelfPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysBulletinDO>('/sysBulletin/userSelfPage', form, config)
}
