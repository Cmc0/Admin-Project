// 下划线转换驼峰
function toHump(name: string) {
    if (name) {
        return name.replace(/\_(\w)/g, (all, letter) => {
            return letter.toUpperCase()
        })
    }
}

export default {toHump}

