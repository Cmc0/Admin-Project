export default interface BaseEntity {
    createId: number // 创建人id
    createTime: Date // 创建时间
    updateId: number // 修改人id
    updateTime: Date // 修改时间
    version: number // 乐观锁
}
