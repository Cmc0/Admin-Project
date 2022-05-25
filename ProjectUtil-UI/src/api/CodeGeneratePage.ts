import $http from "@/util/HttpUtil"
import MyPageDTO from "@/model/dto/MyPageDTO"

export interface CodeGeneratePageDTO extends MyPageDTO {

}

export interface CodeGeneratePageVO {
    tableName: string // 表名
    tableComment: string // 表描述
    columnName: string // 字段名
    dataType: string // 字段类型，如：tinyint varchar
    columnType: string // 字段类型，如：tinyint(1) varchar(300)
    columnComment: string // 字段描述
}

// 代码生成控制器：分页排序查询
export default function (form: CodeGeneratePageDTO) {
    return $http.myPagePost<CodeGeneratePageVO, CodeGeneratePageDTO>('/codeGenerate/page', form)
}
