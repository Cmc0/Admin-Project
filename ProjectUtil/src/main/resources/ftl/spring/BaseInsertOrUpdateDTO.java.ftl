package generate.spring.model.dto;

import com.cmc.projectutil.model.dto.MyId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
* {@link generate.spring.model.entity.${tableNameCamelCaseUpperFirst}DO}
*/
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "${tableComment}")
public class ${tableNameCamelCaseUpperFirst}InsertOrUpdateDTO extends MyId {

<#list columnList as column>
    <#if column.columnName != "id">
    <#if column.columnComment?? && column.columnComment != "">
    @ApiModelProperty(value = "${column.columnComment}")
    </#if>
    private ${column.columnJavaType} ${column.columnNameCamelCase};

    </#if>
</#list>
}
