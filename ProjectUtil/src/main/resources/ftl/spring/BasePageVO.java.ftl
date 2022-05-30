package generate.spring.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * {@link generate.spring.model.entity.${tableNameCamelCaseUpperFirst}DO}
 */
@Data
@ApiModel(description = "${tableComment}")
public class ${tableNameCamelCaseUpperFirst}PageVO {

<#list columnList as column>
    <#if column.columnComment?? && column.columnComment != "">
    @ApiModelProperty(value = "${column.columnComment}")
    </#if>
    private ${column.columnJavaType} ${column.columnNameCamelCase};

</#list>
}
