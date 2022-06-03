// 下划线转换驼峰
export function toHump(name: string) {
    if (name) {
        return name.replace(/\_(\w)/g, (all, letter) => {
            return letter.toUpperCase()
        })
    }
}

