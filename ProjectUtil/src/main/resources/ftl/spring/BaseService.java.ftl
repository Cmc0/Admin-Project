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

    List<${tableNameCamelCaseUpperFirst}DO> tree(${tableNameCamelCaseUpperFirst}PageDTO dto);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

    ${tableNameCamelCaseUpperFirst}InfoByIdVO infoById(NotNullId notNullId);

    String addOrderNo(AddOrderNoDTO dto);

}
