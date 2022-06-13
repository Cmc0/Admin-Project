import {SortOrder} from "antd/lib/table/interface";

export interface MyOrderDTO {
    name?: string // 排序的字段名
    value?: SortOrder // ascend（升序，默认） descend（降序）
}
