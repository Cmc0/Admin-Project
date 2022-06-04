// language=HTML
export const JavaBeanToTsInterfaceSourceTemp =
    `private Date createTime;

@ApiModelProperty(value = "总数")
 private Integer count;

@ApiModelProperty(value = "部门名称")
 private String deptName;

@ApiModelProperty(value = "类别")
 private Byte category;
 
@ApiModelProperty(value = "所属区域 set")
private Set<String> areaNameSet;
 
@ApiModelProperty(value = "是否逻辑删除")
private Boolean delFlag;`

export default `    let result = ''

    const stringList = source.split(';');

    stringList.forEach(item => {

        const splitList = item.split('\\n');

        const str = splitList[splitList.length - 1] //private String tableName

        if (splitList.length === 1) {
            appendToResult(str)
        } else {
            const matchList = item.match(/@ApiModelProperty\\(value = \\"(.*?)\\"\\)/);
            if (matchList && matchList.length) {
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

        if (strList.length < 3) {
            return;
        }

        const columnTypeRefEnum = ColumnTypeRefEnum.getByJavaType(strList[strList.length - 2]);

        result = result + \`\${strList[strList.length - 1]}?: \${columnTypeRefEnum.tsType} // \${apiModelPropertyValue}\\n\`

    }

    setResult(result)`
