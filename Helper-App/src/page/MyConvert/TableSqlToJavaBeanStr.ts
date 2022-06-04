export const TableSqlToJavaBeanSourceTemp =
    `CREATE TABLE \`area\` (
  \`id\` bigint(20) NOT NULL AUTO_INCREMENT,
  \`create_id\` bigint(20) NOT NULL,
  \`create_time\` datetime NOT NULL,
  \`update_id\` bigint(20) NOT NULL,
  \`update_time\` datetime NOT NULL,
  \`enable_flag\` tinyint(1) NOT NULL COMMENT '启用/禁用',
  \`version\` int(11) NOT NULL COMMENT '乐观锁',
  \`del_flag\` tinyint(1) NOT NULL COMMENT '是否逻辑删除',
  \`remark\` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '描述/备注',
  \`parent_id\` bigint(20) NOT NULL COMMENT '父节点id（顶级则为0）',
  \`name\` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '区域名称',
  \`order_no\` int(11) NOT NULL COMMENT '排序号（值越大越前面，默认为 0）',
  PRIMARY KEY (\`id\`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COMMENT='区域主表';`

export default `    const tableNameMatchList = source.match(/CREATE TABLE \`(.*?)\`/);
    const tableCommentMatchList = source.match(/COMMENT='(.*?)';/);

    let tableName = 'tableName'
    let tableComment = 'tableComment'

    if (tableNameMatchList && tableNameMatchList.length) {
        tableName = tableNameMatchList[1]
    }
    if (tableCommentMatchList && tableCommentMatchList.length) {
        tableComment = tableCommentMatchList[1]
    }

    let result = \`import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "\${tableComment}")
public class \${tableName} {\\n
\`

    const stringList = source.split('\\n');

    const collectList = stringList.filter(item => item.includes(','));

    collectList.forEach(item => {

        const columnNameMatchList = item.match(/\`(.*?)\`/);
        let columnName = ''
        if (columnNameMatchList && columnNameMatchList.length) {
            const columnNameHump = StrUtil.toHump(columnNameMatchList[1])
            if (columnNameHump) {
                columnName = columnNameHump
            }
        }

        const splitList = item.split(' ').filter(item => item.length);

        const columnTypeRefEnum = ColumnTypeRefEnum.getByColumnType(splitList[1]);

        const columnCommentMatchList = item.match(/COMMENT '(.*?)'/);
        let columnComment = ''
        if (columnCommentMatchList && columnCommentMatchList.length) {
            columnComment = columnCommentMatchList[1]
        }

        if (columnComment) {
            result = result + \`    @ApiModelProperty(value = "\${columnComment}")\\n\`
        }

        result = result + \`    private \${columnTypeRefEnum.javaType} \${columnName};

\`

    })

    result = result + '}'

    setResult(result)`
