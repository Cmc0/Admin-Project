let queue: IExecTest[] = []

interface IExecTest {
    name: string
    func: () => void
}

export function execTest(data: IExecTest) {

    const length = queue.push(data);

    setTimeout(() => {
        const execData = queue.shift();
        if (execData) {
            console.log(`【${execData.name}】开始 ↓`)
            execData.func() // 执行方法
            console.log(`【${execData.name}】完成 ↑`)
        }
    }, 1000 * length)

}
