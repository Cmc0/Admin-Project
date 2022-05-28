import $http from "@/util/HttpUtil"
import MyPageDTO from "@/model/dto/MyPageDTO"

export interface ${tableNameCamelCaseUpperFirst}InsertOrUpdateDTO {
    <#list columnList as column>
    ${column.columnNameCamelCase}?: ${column.columnTsType} // ${column.columnComment!""}
    </#list>
}

// ${tableComment}：新增/修改
export function insertOrUpdate(form: ${tableNameCamelCaseUpperFirst}InsertOrUpdateDTO) {
    return $http.myPost<String, ${tableNameCamelCaseUpperFirst}InsertOrUpdateDTO>('/${tableNameCamelCase}/insertOrUpdate', form)
}

export interface ${tableNameCamelCaseUpperFirst}PageDTO extends MyPageDTO {

}

export interface ${tableNameCamelCaseUpperFirst}PageVO {

}

// ${tableComment}：分页排序查询
export function page(form: ${tableNameCamelCaseUpperFirst}PageDTO) {
    return $http.myProPagePost<${tableNameCamelCaseUpperFirst}PageVO, ${tableNameCamelCaseUpperFirst}PageDTO>('/${tableNameCamelCase}/page', form)
}
