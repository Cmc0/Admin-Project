export const SqlAddAsSourceTemp =
    `a.area_name,
a.dept_name AS deptName,
a.category,
type,
type_name AS typeName,
job_name`

export default `    const splitList = source.split(',');

    let result = ''

    splitList.forEach((item, index) => {

        item = item.replaceAll('\\n', '')

        if (item.includes(" as ") || item.includes(" AS ")) {
            result = result + item
        } else {
            let s = item;
            if (item.includes(".")) {
                s = item.split('.')[1]
            }

            const str = item + " AS " + StrUtil.toHump(s)
            result = result + str
        }

        if (index !== splitList.length - 1) {
            result = result + ',\\n'
        }
    })

    setResult(result)`
