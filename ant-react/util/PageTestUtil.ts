let queue: IExecTest[] = []

interface IExecTest {
    name: string
    func: () => void
}

export function execTest(data: IExecTest) {

    if (import.meta.env.PROD) {
        return
    }

    const length = queue.push(data);

    setTimeout(() => {
        const execData = queue.shift();
        if (execData) {
            console.log(`【${execData.name}】测试开始 ↓ ${new Date().toLocaleString()}`)
            execData.func() // 执行方法
            console.log(`【${execData.name}】测试完成 ↑ ${new Date().toLocaleString()}`)
        }
    }, 1000 * length)

}
