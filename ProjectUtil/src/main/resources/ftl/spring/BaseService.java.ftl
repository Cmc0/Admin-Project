package generate.spring.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmc.projectutil.model.dto.AddOrderNoDTO;
import com.cmc.projectutil.model.dto.NotEmptyIdSet;
import com.cmc.projectutil.model.dto.NotNullId;
import generate.spring.model.dto.${tableNameCamelCaseUpperFirst}InsertOrUpdateDTO;
import generate.spring.model.dto.${tableNameCamelCaseUpperFirst}PageDTO;
import generate.spring.model.entity.${tableNameCamelCaseUpperFirst}DO;
import generate.spring.model.vo.${tableNameCamelCaseUpperFirst}InfoByIdVO;

import java.util.List;

public interface ${tableNameCamelCaseUpperFirst}Service extends IService<${tableNameCamelCaseUpperFirst}DO> {

    String insertOrUpdate(${tableNameCamelCaseUpperFirst}InsertOrUpdateDTO dto);

    Page<${tableNameCamelCaseUpperFirst}<#if supperClassName?? && supperClassName == "BaseEntityFour">DO<#else>PageVO</#if>> myPage(${tableNameCamelCaseUpperFirst}PageDTO dto);
    <#if supperClassName?? && supperClassName == "BaseEntityFour">

    List<${tableNameCamelCaseUpperFirst}DO> tree(${tableNameCamelCaseUpperFirst}PageDTO dto);
    </#if>

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

    ${tableNameCamelCaseUpperFirst}InfoByIdVO infoById(NotNullId notNullId);

    <#if supperClassName?? && supperClassName == "BaseEntityFour">
    String addOrderNo(AddOrderNoDTO dto);

    </#if>
}
