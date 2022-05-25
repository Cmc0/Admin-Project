import {SortOrder} from "antd/lib/table/interface";

export default interface MyPageDTO {
    current?: number // 第几页
    pageSize?: number // 每页显示条数
    order?: Record<string, SortOrder> // 排序字段
}
