package generate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import generate.model.entity.${tableNameCamelCaseUpperFirst}DO;

public interface ${tableNameCamelCaseUpperFirst}Mapper extends BaseMapper<${tableNameCamelCaseUpperFirst}DO> {

    // 分页排序查询
    Page<${tableNameCamelCaseUpperFirst}DO> myPage(Page<${tableNameCamelCaseUpperFirst}DO> page,
        @Param("dto") ${tableNameCamelCaseUpperFirst}PageDTO dto);
}
