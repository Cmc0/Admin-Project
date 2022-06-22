// 处理后台返回的 ip所处区域
export function handlerRegion(region: string) {
    if (!region) return region
    return region
        .split('|')
        .filter((item) => item !== '0')
        .join(' ')
}
