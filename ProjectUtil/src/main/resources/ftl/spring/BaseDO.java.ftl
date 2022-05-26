package generate.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName(value = "${tableName}")
@Data
@ApiModel(description = "${tableComment}")
public class ${tableNameCamelCaseUpperFirst}DO {

<#list columnList as column>
    @ApiModelProperty(value = "${column.columnComment}")
    private ${column.columnType} ${column.columnNameCamelCase};

</#list>

}
