interface IExplainList {
    title: string,
    description: string
}

const ExplainList: IExplainList[] = [
    {title: 'source', description: '要转换的内容'},
    {title: 'setResult', description: '设置转换后的内容'},
    {title: 'StrUtil', description: '字符串工具类'},
]

export const ExplainTitList = ExplainList.map(item => item.title)

export default ExplainList
