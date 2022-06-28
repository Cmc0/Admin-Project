import BaseEntityThree from "@/model/entity/BaseEntityThree";

export default interface BaseEntityFour<T> extends BaseEntityThree {
    orderNo?: number // 排序号（值越大越前面，默认为 0）
    parentId?: number // 父节点id（顶级则为0）
    children?: T[] // 子节点
}
