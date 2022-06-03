export const SqlToJavaBeanSourceTemp =
    `DATE_FORMAT(#{item}, #{format}) AS date,
COUNT( 1 ) as count,
a.dept_name AS deptName,
a.category`

export default `const splitList = source.split(',');

let result = ''

splitList.forEach(item => {
    if (item.includes(" as ")) {
        appendToResult(item.split("as ")[1])
    } else if (item.includes(" AS ")) {
        appendToResult(item.split("AS ")[1])
    } else {
        appendToResult(StrUtil.toHump(item.split(".")[1]))
    }
})

function appendToResult(fieldName = '') {
    if (fieldName) {
        result = result + \`@ApiModelProperty(value = \\"xxx\\")\\n private String \${fieldName};\\n\\n\`
    }
}

setResult(result)
`
