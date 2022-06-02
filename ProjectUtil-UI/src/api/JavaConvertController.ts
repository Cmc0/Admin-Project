import $http from "@/util/HttpUtil";
import NotBlankStrDTO from "@/model/dto/NotBlankStrDTO";

// sql转java
export function sqlToJava(form: NotBlankStrDTO) {
    return $http.myPost<string, NotBlankStrDTO>('/myConvert/sqlToJava', form)
}

// java转ts
export function javaToTs(form: NotBlankStrDTO) {
    return $http.myPost<string, NotBlankStrDTO>('/myConvert/javaToTs', form)
}

// 给sql添加AS
export function sqlAddAs(form: NotBlankStrDTO) {
    return $http.myPost<string, NotBlankStrDTO>('/myConvert/sqlAddAs', form)
}

// 通过：表结构sql，生成后台代码
export function forSpringByTableSql(form: NotBlankStrDTO) {
    return $http.myPost<string, NotBlankStrDTO>('/myConvert/forSpringByTableSql', form)
}

// 通过：表结构sql，生成前端代码
export function forAntByTableSql(form: NotBlankStrDTO) {
    return $http.myPost<string, NotBlankStrDTO>('/myConvert/forAntByTableSql', form)
}
