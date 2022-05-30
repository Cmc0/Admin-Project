package generate.spring.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import generate.spring.model.dto.${tableNameCamelCaseUpperFirst}PageDTO;
import generate.spring.model.entity.${tableNameCamelCaseUpperFirst}DO;
import generate.spring.model.vo.${tableNameCamelCaseUpperFirst}PageVO;
import org.apache.ibatis.annotations.Param;

public interface ${tableNameCamelCaseUpperFirst}Mapper extends BaseMapper<${tableNameCamelCaseUpperFirst}DO> {

    // 分页排序查询
    Page<${tableNameCamelCaseUpperFirst}<#if supperClassName?? && supperClassName == "BaseEntityFour">DO<#else>PageVO</#if>> myPage(Page<${tableNameCamelCaseUpperFirst}<#if supperClassName?? && supperClassName == "BaseEntityFour">DO<#else>PageVO</#if>> page, @Param("dto") ${tableNameCamelCaseUpperFirst}PageDTO dto);

}
