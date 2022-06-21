package com.admin.area.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.admin.area.mapper.SysAreaMapper;
import com.admin.area.model.dto.SysAreaInsertOrUpdateDTO;
import com.admin.area.model.dto.SysAreaPageDTO;
import com.admin.area.model.entity.SysAreaDO;
import com.admin.area.model.entity.SysAreaRefDeptDO;
import com.admin.area.model.vo.SysAreaInfoByIdVO;
import com.admin.area.service.SysAreaRefDeptService;
import com.admin.area.service.SysAreaService;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.model.dto.AddOrderNoDTO;
import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullId;
import com.admin.common.model.entity.BaseEntityFour;
import com.admin.common.model.entity.BaseEntityThree;
import com.admin.common.model.entity.BaseEntityTwo;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.common.util.MyEntityUtil;
import com.admin.common.util.MyTreeUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysAreaServiceImpl extends ServiceImpl<SysAreaMapper, SysAreaDO> implements SysAreaService {

    @Resource
    SysAreaRefDeptService areaRefDeptService;

    /**
     * 新增/修改 区域
     */
    @Override
    @Transactional
    public String insertOrUpdate(SysAreaInsertOrUpdateDTO dto) {

        if (dto.getId() != null && dto.getId().equals(dto.getParentId())) {
            ApiResultVO.error(BaseBizCodeEnum.PARENT_ID_CANNOT_BE_EQUAL_TO_ID);
        }

        // 相同父节点下：区域名（不能重复）
        Long count = lambdaQuery().eq(SysAreaDO::getName, dto.getName())
            .eq(BaseEntityFour::getParentId, MyEntityUtil.getNotNullParentId(dto.getParentId()))
            .ne(dto.getId() != null, BaseEntityTwo::getId, dto.getId()).count();
        if (count != 0) {
            ApiResultVO.error("操作失败：相同父节点下，区域名不能重复");
        }

        SysAreaDO sysAreaDO = new SysAreaDO();
        sysAreaDO.setName(dto.getName());
        sysAreaDO.setParentId(MyEntityUtil.getNotNullParentId(dto.getParentId()));
        sysAreaDO.setEnableFlag(dto.isEnableFlag());
        sysAreaDO.setId(dto.getId());
        sysAreaDO.setOrderNo(dto.getOrderNo());
        sysAreaDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));

        if (dto.getId() != null) {
            // 先删除子表数据
            areaRefDeptService.removeById(dto.getId());
        }

        saveOrUpdate(sysAreaDO);

        // 再插入子表数据
        if (CollUtil.isNotEmpty(dto.getDeptIdSet())) {
            List<SysAreaRefDeptDO> insertList = new ArrayList<>();
            for (Long item : dto.getDeptIdSet()) {
                SysAreaRefDeptDO sysAreaRefDeptDO = new SysAreaRefDeptDO();
                sysAreaRefDeptDO.setAreaId(sysAreaDO.getId());
                sysAreaRefDeptDO.setDeptId(item);
                insertList.add(sysAreaRefDeptDO);
            }
            areaRefDeptService.saveBatch(insertList);
        }

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 分页排序查询：区域
     */
    @Override
    public Page<SysAreaDO> myPage(SysAreaPageDTO dto) {

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getName()), SysAreaDO::getName, dto.getName())
            .like(StrUtil.isNotBlank(dto.getRemark()), BaseEntityThree::getRemark, dto.getRemark())
            .eq(dto.getId() != null, BaseEntityTwo::getId, dto.getId())
            .eq(dto.getEnableFlag() != null, BaseEntityThree::getEnableFlag, dto.getEnableFlag())
            .orderByDesc(BaseEntityFour::getOrderNo).page(dto.getPage());
    }

    /**
     * 查询区域（树结构）
     */
    @Override
    public List<SysAreaDO> tree(SysAreaPageDTO dto) {

        List<SysAreaDO> resultList = new ArrayList<>();

        // 根据条件进行筛选，得到符合条件的数据，然后再逆向生成整棵树，并返回这个树结构
        dto.setPageSize(-1); // 不分页
        List<SysAreaDO> dbList = myPage(dto).getRecords();

        if (dbList.size() == 0) {
            return resultList;
        }

        // 查询出所有的菜单
        List<SysAreaDO> allList = list();

        if (allList.size() == 0) {
            return resultList;
        }

        return MyTreeUtil.getFullTreeByDeepNode(dbList, allList);
    }

    /**
     * 删除区域
     */
    @Override
    @Transactional
    public String deleteByIdSet(NotEmptyIdSet notEmptyIdSet) {

        // 如果存在下级，则无法删除
        Long count = lambdaQuery().in(BaseEntityFour::getParentId, notEmptyIdSet.getIdSet()).count();
        if (count != 0) {
            ApiResultVO.error(BaseBizCodeEnum.PLEASE_DELETE_THE_CHILD_NODE_FIRST);
        }

        // 移除子表数据
        areaRefDeptService.lambdaUpdate().in(SysAreaRefDeptDO::getAreaId, notEmptyIdSet.getIdSet()).remove();

        removeByIds(notEmptyIdSet.getIdSet());

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysAreaInfoByIdVO infoById(NotNullId notNullId) {

        SysAreaDO sysAreaDO = getById(notNullId.getId());
        if (sysAreaDO == null) {
            return null;
        }

        // 封装绑定的部门 idSet
        List<SysAreaRefDeptDO> areaRefDeptDOList =
            areaRefDeptService.lambdaQuery().eq(SysAreaRefDeptDO::getAreaId, notNullId.getId())
                .select(SysAreaRefDeptDO::getDeptId).list();

        Set<Long> deptIdSet = areaRefDeptDOList.stream().map(SysAreaRefDeptDO::getDeptId).collect(Collectors.toSet());

        SysAreaInfoByIdVO sysAreaInfoByIdVO = BeanUtil.copyProperties(sysAreaDO, SysAreaInfoByIdVO.class);
        sysAreaInfoByIdVO.setDeptIdSet(deptIdSet);

        return sysAreaInfoByIdVO;
    }

    /**
     * 通过主键 idSet，加减排序号
     */
    @Override
    @Transactional
    public String addOrderNo(AddOrderNoDTO dto) {
        if (dto.getNumber() == 0) {
            return BaseBizCodeEnum.API_RESULT_OK.getMsg();
        }

        List<SysAreaDO> listByIds = listByIds(dto.getIdSet());

        for (SysAreaDO item : listByIds) {
            item.setOrderNo(item.getOrderNo() + dto.getNumber());
        }

        updateBatchById(listByIds);

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }
}




