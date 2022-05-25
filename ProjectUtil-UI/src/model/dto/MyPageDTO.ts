interface IOrderItem {
    column?: string // 需要进行排序的字段
    asc?: boolean // 是否正序排列，默认 true
}

export default interface MyPageDTO {
    pageNum?: number // 第几页
    pageSize?: number// 每页显示条数
    orderList?: IOrderItem[] // 排序 list
}
