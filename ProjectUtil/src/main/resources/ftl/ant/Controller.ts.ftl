import $http from "@/util/HttpUtil"
import MyPageDTO from "@/model/dto/MyPageDTO"
import MyId from "@/model/dto/MyId";

export interface ${tableNameCamelCaseUpperFirst}InsertOrUpdateDTO extends MyId {
    <#list columnList as column>
        <#if column.columnName != "id">
    ${column.columnNameCamelCase}?: ${column.columnTsType} // ${column.columnComment!""}
        </#if>
    </#list>
}

// ${tableComment}：新增/修改
export function insertOrUpdate(form: ${tableNameCamelCaseUpperFirst}InsertOrUpdateDTO) {
    return $http.myPost<String, ${tableNameCamelCaseUpperFirst}InsertOrUpdateDTO>('/${tableNameCamelCase}/insertOrUpdate', form)
}

export interface ${tableNameCamelCaseUpperFirst}PageDTO extends MyPageDTO {
    <#list columnList as column>
        <#if column.columnName != "id">
            ${column.columnNameCamelCase}?: ${column.columnTsType} // ${column.columnComment!""}
        </#if>
    </#list>
}

export interface ${tableNameCamelCaseUpperFirst}PageVO {

}

// ${tableComment}：分页排序查询
export function page(form: ${tableNameCamelCaseUpperFirst}PageDTO) {
    return $http.myProPagePost<${tableNameCamelCaseUpperFirst}PageVO, ${tableNameCamelCaseUpperFirst}PageDTO>('/${tableNameCamelCase}/page', form)
}
