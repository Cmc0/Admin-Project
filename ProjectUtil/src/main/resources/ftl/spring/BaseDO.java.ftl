package generate.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
<#if supperClassName??>
import com.cmc.projectutil.model.entity.${supperClassName};
</#if>
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

<#if supperClassName??>
@EqualsAndHashCode(callSuper = true)
</#if>
@TableName(value = "${tableName}")
@Data
@ApiModel(description = "${tableComment}")
public class ${tableNameCamelCaseUpperFirst}DO<#if supperClassName??><#if supperClassName == "BaseEntityFour"> extends BaseEntityFour<${tableNameCamelCaseUpperFirst}DO><#else> extends ${supperClassName}</#if></#if> {

<#if supperClassName??>
    <#list noSupperClassColumnList as column>
        <#if column.columnComment?? && column.columnComment != "">
    @ApiModelProperty(value = "${column.columnComment}")
        </#if>
    private ${column.columnJavaType} ${column.columnNameCamelCase};

    </#list>
<#else>
    <#list columnList as column>
        <#if column.columnComment?? && column.columnComment != "">
    @ApiModelProperty(value = "${column.columnComment}")
        </#if>
    private ${column.columnJavaType} ${column.columnNameCamelCase};

    </#list>
</#if>
}
