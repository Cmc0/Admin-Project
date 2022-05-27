import $http from "@/util/HttpUtil"
import MyPageDTO from "@/model/dto/MyPageDTO"

export interface CodeGeneratePageDTO extends MyPageDTO {

}

export interface CodeGeneratePageVO {
    id: string // 表名:字段名
    tableName: string // 表名
    tableComment: string // 表描述
    columnName: string // 字段名
    columnType: string // 字段类型，如：tinyint(1) varchar(300)
    columnComment: string // 字段描述
}

// 代码生成控制器：分页排序查询
export function page(form: CodeGeneratePageDTO) {
    return $http.myProPagePost<CodeGeneratePageVO, CodeGeneratePageDTO>('/codeGenerate/page', form)
}

// 代码生成控制器：生成后台代码
export function forSpring(list: CodeGeneratePageVO[]) {
    return $http.myPost<String, CodeGeneratePageVO[]>('/codeGenerate/forSpring', list)
}

// 代码生成控制器：生成前端代码
export function forAnt(list: CodeGeneratePageVO[]) {
    return $http.myPost<String, CodeGeneratePageVO[]>('/codeGenerate/forAnt', list)
}
