import BaseEntityTwo from "@/model/entity/BaseEntityTwo";

export default interface BaseEntityThree extends BaseEntityTwo {
    enableFlag: boolean // 启用/禁用
    delFlag: boolean // 是否逻辑删除
    remark: string // 描述/备注
}
