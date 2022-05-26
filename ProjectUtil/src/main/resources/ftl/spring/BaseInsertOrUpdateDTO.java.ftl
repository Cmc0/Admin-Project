package generate.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
* {@link generate.model.entity.${tableNameCamelCaseUpperFirst}DO}
*/
@Data
@ApiModel(description = "${tableComment}")
public class ${tableNameCamelCaseUpperFirst}InsertOrUpdateDTO {

<#list columnList as column>
    <#if column.columnComment?? && column.columnComment != "">
    @ApiModelProperty(value = "${column.columnComment}")
    </#if>
    private ${column.columnJavaType} ${column.columnNameCamelCase};

</#list>
}
