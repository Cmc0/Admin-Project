export const JavaBeanToTsInterfaceSourceTemp =
    `private String createTime;

@ApiModelProperty(value = "总数")
 private String count;

@ApiModelProperty(value = "部门名称")
 private String deptName;

@ApiModelProperty(value = "类别")
 private String category;`

export default `    let result = ''

    const stringList = source.split(';');
    
    stringList.forEach(item => {

        const splitList = item.split('\\n');
        
        const str = splitList[splitList.length - 1] //private String tableName

        if (splitList.length === 1) {
            appendToResult(str)
        } else {
            const matchList = item.match(/@ApiModelProperty\\(value = \\"(.*?)\\"\\)/);
            if (matchList && matchList.length !== 0) {
                appendToResult(str, matchList[1])
            } else {
                appendToResult(str)
            }
        }

    })

    function appendToResult(str = '', apiModelPropertyValue = '') {

        if (!str) {
            return
        }

        const strList = str.split(" ");

        if (strList.length !== 3) {
            return;
        }

        const columnTypeRefEnum = ColumnTypeRefEnum.getByJavaType(strList[1]);

        result = result + \`\${strList[2]}?: \${columnTypeRefEnum.tsType} // \${apiModelPropertyValue}\\n\`

    }

    setResult(result)`
