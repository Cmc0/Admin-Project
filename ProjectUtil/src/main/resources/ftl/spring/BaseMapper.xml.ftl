<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="generate.spring.mapper.${tableNameCamelCaseUpperFirst}Mapper">

    <select id="myPage" resultType="generate.spring.model.<#if supperClassName?? && supperClassName == "BaseEntityFour">entity<#else>vo</#if>.${tableNameCamelCaseUpperFirst}<#if supperClassName?? && supperClassName == "BaseEntityFour">DO<#else>PageVO</#if>">
        SELECT
<#list columnList as column>
        a.${column.columnName} AS ${column.columnNameCamelCase}<#if column_index != (columnList?size-1)>,</#if>
</#list>
        FROM ${tableName} a WHERE a.del_flag = false
<#list columnList as column>
    <#if column.columnJavaType == "String">
        <if test="dto.${column.columnNameCamelCase} != null and dto.${column.columnNameCamelCase} != ''">
            AND a.${column.columnName} LIKE CONCAT('%', <#noparse>#{</#noparse>dto.${column.columnNameCamelCase}}, '%')
        </if>
    <#else>
        <if test="dto.${column.columnNameCamelCase} != null">
            AND a.${column.columnName} = <#noparse>#{</#noparse>dto.${column.columnNameCamelCase}}
        </if>
    </#if>
</#list>
    </select>

</mapper>
