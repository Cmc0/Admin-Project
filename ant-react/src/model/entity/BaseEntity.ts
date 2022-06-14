export default interface BaseEntity {
    createId: number // 创建人id
    createTime: string // 创建时间
    updateId: number // 修改人id
    updateTime: string // 修改时间
    version: number // 乐观锁
}
