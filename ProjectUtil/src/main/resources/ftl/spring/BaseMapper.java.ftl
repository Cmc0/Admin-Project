package generate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import generate.model.dto.${tableNameCamelCaseUpperFirst}PageDTO;
import generate.model.entity.${tableNameCamelCaseUpperFirst}DO;
import org.apache.ibatis.annotations.Param;

public interface ${tableNameCamelCaseUpperFirst}Mapper extends BaseMapper<${tableNameCamelCaseUpperFirst}DO> {

    // 分页排序查询
    Page<${tableNameCamelCaseUpperFirst}DO> myPage(Page<${tableNameCamelCaseUpperFirst}DO> page,
        @Param("dto") ${tableNameCamelCaseUpperFirst}PageDTO dto);
}
