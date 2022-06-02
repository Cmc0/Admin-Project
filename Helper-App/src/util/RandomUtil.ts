// 获取随机字符串
export function randomString(length: number = 6) {
    const BASE_CHAR_NUMBER = 'abcdefghijklmnopqrstuvwxyz0123456789'
    let resStr = ''
    for (let index = 0; index < length; index++) {
        resStr += BASE_CHAR_NUMBER.charAt(Math.floor(Math.random() * BASE_CHAR_NUMBER.length))
    }
    return resStr
}
