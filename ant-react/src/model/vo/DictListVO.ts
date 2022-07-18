export type TDictListVO = string | number

export default interface DictListVO<T extends TDictListVO = string> {
    label: string // 显示用
    value: T // 传值用
}
