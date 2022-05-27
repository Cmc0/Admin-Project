package generate.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmc.projectutil.model.dto.AddOrderNoDTO;
import com.cmc.projectutil.model.dto.NotEmptyIdSet;
import com.cmc.projectutil.model.dto.NotNullId;
import generate.model.dto.${tableNameCamelCaseUpperFirst}InsertOrUpdateDTO;
import generate.model.dto.${tableNameCamelCaseUpperFirst}PageDTO;
import generate.model.entity.${tableNameCamelCaseUpperFirst}DO;
import generate.model.vo.${tableNameCamelCaseUpperFirst}InfoByIdVO;

import java.util.List;

public interface ${tableNameCamelCaseUpperFirst}Service extends IService<${tableNameCamelCaseUpperFirst}DO> {

    String insertOrUpdate(${tableNameCamelCaseUpperFirst}InsertOrUpdateDTO dto);

    Page<${tableNameCamelCaseUpperFirst}DO> myPage(${tableNameCamelCaseUpperFirst}PageDTO dto);
    <#if supperClassName?? && supperClassName == "BaseEntityFour">

    List<${tableNameCamelCaseUpperFirst}DO> tree(${tableNameCamelCaseUpperFirst}PageDTO dto);
    </#if>

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

    ${tableNameCamelCaseUpperFirst}InfoByIdVO infoById(NotNullId notNullId);

    <#if supperClassName?? && supperClassName == "BaseEntityFour">
    String addOrderNo(AddOrderNoDTO dto);

    </#if>
}
