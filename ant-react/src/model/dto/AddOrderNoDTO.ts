export default interface AddOrderNoDTO {
    idSet: (string | number)[] // 主键 idSet
    number: number // 统一加减的数值
}

export const AddOrderNo = "累加排序号"
