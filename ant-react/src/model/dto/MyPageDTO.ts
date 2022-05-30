import {SortOrder} from "antd/lib/table/interface";

export interface MyOrderDTO {
    name?: string // 排序的字段名
    value?: SortOrder // ascend（升序，默认） descend（降序）
}

export default interface MyPageDTO {
    current?: number // 第几页
    pageSize?: number // 每页显示条数
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
    order?: MyOrderDTO // 排序字段
}
