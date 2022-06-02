export default class {
    // 下划线转换驼峰
    toHump(name: string) {
        if(name){
            return name.replace(/\_(\w)/g, (all, letter) => {
                return letter.toUpperCase()
            })
        }
    }
}

