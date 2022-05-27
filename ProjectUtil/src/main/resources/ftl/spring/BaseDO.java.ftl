package generate.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
<#if supperClassName?? && supperClassName != "">
import com.cmc.projectutil.model.entity.${supperClassName};
</#if>
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

<#if supperClassName?? && supperClassName != "">
@EqualsAndHashCode(callSuper = true)
</#if>
@TableName(value = "${tableName}")
@Data
@ApiModel(description = "${tableComment}")
public class ${tableNameCamelCaseUpperFirst}DO<#if supperClassName?? && supperClassName != ""><#if supperClassName == "BaseEntityFour"> extends BaseEntityFour<${tableNameCamelCaseUpperFirst}DO><#else> extends ${supperClassName}</#if></#if> {

<#list columnList as column>
    <#if column.columnComment?? && column.columnComment != "">
    @ApiModelProperty(value = "${column.columnComment}")
    </#if>
    private ${column.columnJavaType} ${column.columnNameCamelCase};

</#list>
}
