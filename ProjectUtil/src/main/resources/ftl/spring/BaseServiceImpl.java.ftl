package generate.spring.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmc.projectutil.exception.BaseBizCodeEnum;
import com.cmc.projectutil.model.dto.AddOrderNoDTO;
import com.cmc.projectutil.model.dto.NotEmptyIdSet;
import com.cmc.projectutil.model.dto.NotNullId;
import com.cmc.projectutil.util.MyTreeUtil;
import generate.spring.model.dto.${tableNameCamelCaseUpperFirst}InsertOrUpdateDTO;
import generate.spring.model.dto.${tableNameCamelCaseUpperFirst}PageDTO;
import generate.spring.model.vo.${tableNameCamelCaseUpperFirst}InfoByIdVO;
import org.springframework.stereotype.Service;
import generate.spring.mapper.${tableNameCamelCaseUpperFirst}Mapper;
import generate.spring.model.entity.${tableNameCamelCaseUpperFirst}DO;
import generate.spring.service.${tableNameCamelCaseUpperFirst}Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ${tableNameCamelCaseUpperFirst}ServiceImpl extends ServiceImpl<${tableNameCamelCaseUpperFirst}Mapper, ${tableNameCamelCaseUpperFirst}DO> implements ${tableNameCamelCaseUpperFirst}Service {

    /**
     * 新增/修改
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public String insertOrUpdate(${tableNameCamelCaseUpperFirst}InsertOrUpdateDTO dto) {

        ${tableNameCamelCaseUpperFirst}DO ${tableNameCamelCase}DO = new ${tableNameCamelCaseUpperFirst}DO();
        ${tableNameCamelCase}DO.setId(dto.getId());

        saveOrUpdate(${tableNameCamelCase}DO);

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<${tableNameCamelCaseUpperFirst}DO> myPage(${tableNameCamelCaseUpperFirst}PageDTO dto) {
        return baseMapper.myPage(dto.getPage(), dto);
    }

    <#if supperClassName?? && supperClassName == "BaseEntityFour">
    /**
     * 查询：树结构
     */
    @Override
    public List<${tableNameCamelCaseUpperFirst}DO> tree(${tableNameCamelCaseUpperFirst}PageDTO dto) {

        // 根据条件进行筛选，得到符合条件的数据，然后再逆向生成整棵树，并返回这个树结构
        dto.setPageSize(-1); // 不分页
        List<${tableNameCamelCaseUpperFirst}DO> dbList = baseMapper.myPage(dto.getPage(), dto).getRecords();

        if (dbList.size() == 0) {
            return new ArrayList<>();
        }

        // 查询出所有的菜单
        List<${tableNameCamelCaseUpperFirst}DO> allList = list();

        if (allList.size() == 0) {
            return new ArrayList<>();
        }

        return MyTreeUtil.getFullTreeByDeepNode(dbList, allList);
    }

    </#if>
    /**
     * 批量删除
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public String deleteByIdSet(NotEmptyIdSet notEmptyIdSet) {

        removeByIds(notEmptyIdSet.getIdSet());

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public ${tableNameCamelCaseUpperFirst}InfoByIdVO infoById(NotNullId notNullId) {

        ${tableNameCamelCaseUpperFirst}DO ${tableNameCamelCase}DO = getById(notNullId.getId());
        if (${tableNameCamelCase}DO == null) {
            return null;
        }

        return BeanUtil.copyProperties(${tableNameCamelCase}DO, ${tableNameCamelCaseUpperFirst}InfoByIdVO.class);
    }

    <#if supperClassName?? && supperClassName == "BaseEntityFour">
    /**
     * 通过主键 idSet，加减排序号
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public String addOrderNo(AddOrderNoDTO dto) {

        if (dto.getNumber() == 0) {
            return BaseBizCodeEnum.API_RESULT_OK.getMsg();
        }

        List<${tableNameCamelCaseUpperFirst}DO> listByIds = listByIds(dto.getIdSet());

        for (${tableNameCamelCaseUpperFirst}DO item : listByIds) {
            item.setOrderNo(item.getOrderNo() + dto.getNumber());
        }

        updateBatchById(listByIds);

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    </#if>
}
