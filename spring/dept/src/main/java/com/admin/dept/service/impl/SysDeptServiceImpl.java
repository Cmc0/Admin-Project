package com.admin.dept.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.admin.area.model.entity.SysAreaRefDeptDO;
import com.admin.area.service.SysAreaRefDeptService;
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
import com.admin.dept.mapper.SysDeptMapper;
import com.admin.dept.model.dto.SysDeptInsertOrUpdateDTO;
import com.admin.dept.model.dto.SysDeptPageDTO;
import com.admin.dept.model.entity.SysDeptDO;
import com.admin.dept.model.entity.SysDeptRefUserDO;
import com.admin.dept.model.vo.SysDeptInfoByIdVO;
import com.admin.dept.service.SysDeptRefUserService;
import com.admin.dept.service.SysDeptService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDeptDO> implements SysDeptService {

    @Resource
    SysAreaRefDeptService sysAreaRefDeptService;
    @Resource
    SysDeptRefUserService SYsDeptRefUserService;

    /**
     * 新增/修改
     */
    @Override
    @Transactional
    public String insertOrUpdate(SysDeptInsertOrUpdateDTO dto) {

        if (dto.getId() != null && dto.getId().equals(dto.getParentId())) {
            ApiResultVO.error(BaseBizCodeEnum.PARENT_ID_CANNOT_BE_EQUAL_TO_ID);
        }

        // 相同父节点下：部门名（不能重复）
        Long count = lambdaQuery().eq(SysDeptDO::getName, dto.getName())
            .eq(BaseEntityFour::getParentId, MyEntityUtil.getNotNullParentId(dto.getParentId()))
            .ne(dto.getId() != null, BaseEntityTwo::getId, dto.getId()).count();
        if (count != 0) {
            ApiResultVO.error("操作失败：相同父节点下，部门名不能重复");
        }

        SysDeptDO sysDeptDO = new SysDeptDO();
        sysDeptDO.setEnableFlag(dto.isEnableFlag());
        sysDeptDO.setName(dto.getName());
        sysDeptDO.setOrderNo(dto.getOrderNo());
        sysDeptDO.setParentId(MyEntityUtil.getNotNullParentId(dto.getParentId()));
        sysDeptDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));
        sysDeptDO.setId(dto.getId());

        if (dto.getId() != null) {
            // 先删除子表数据，再插入
            deleteByIdSetSub(Collections.singleton(dto.getId()));
        }

        saveOrUpdate(sysDeptDO);

        // 插入子表数据：区域，部门关联表
        if (CollUtil.isNotEmpty(dto.getAreaIdSet())) {
            List<SysAreaRefDeptDO> insertList = new ArrayList<>();
            for (Long item : dto.getAreaIdSet()) {
                SysAreaRefDeptDO sysAreaRefDeptDO = new SysAreaRefDeptDO();
                sysAreaRefDeptDO.setAreaId(item);
                sysAreaRefDeptDO.setDeptId(sysDeptDO.getId());
                insertList.add(sysAreaRefDeptDO);
            }
            sysAreaRefDeptService.saveBatch(insertList);
        }
        // 插入子表数据：部门，用户关联表
        if (CollUtil.isNotEmpty(dto.getUserIdSet())) {
            List<SysDeptRefUserDO> insertList = new ArrayList<>();
            for (Long item : dto.getUserIdSet()) {
                SysDeptRefUserDO sysDeptRefUserDO = new SysDeptRefUserDO();
                sysDeptRefUserDO.setUserId(item);
                sysDeptRefUserDO.setDeptId(sysDeptDO.getId());
                insertList.add(sysDeptRefUserDO);
            }
            SYsDeptRefUserService.saveBatch(insertList);
        }

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysDeptDO> myPage(SysDeptPageDTO dto) {

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getName()), SysDeptDO::getName, dto.getName())
            .like(StrUtil.isNotBlank(dto.getRemark()), BaseEntityThree::getRemark, dto.getRemark())
            .eq(dto.getEnableFlag() != null, BaseEntityThree::getEnableFlag, dto.getEnableFlag())
            .eq(BaseEntityThree::getDelFlag, false).orderByDesc(BaseEntityFour::getOrderNo).page(dto.getPage(true));
    }

    /**
     * 查询：树结构
     */
    @Override
    public List<SysDeptDO> tree(SysDeptPageDTO dto) {

        List<SysDeptDO> resultList = new ArrayList<>();

        // 根据条件进行筛选，得到符合条件的数据，然后再逆向生成整棵树，并返回这个树结构
        dto.setPageSize(-1); // 不分页
        List<SysDeptDO> sysDeptDOList = myPage(dto).getRecords();

        if (sysDeptDOList.size() == 0) {
            return resultList;
        }

        List<SysDeptDO> allList = list();

        if (allList.size() == 0) {
            return resultList;
        }

        return MyTreeUtil.getFullTreeByDeepNode(sysDeptDOList, allList);
    }

    /**
     * 批量删除
     */
    @Override
    @Transactional
    public String deleteByIdSet(NotEmptyIdSet notEmptyIdSet) {

        // 如果存在下级，则无法删除
        Long count = lambdaQuery().in(BaseEntityFour::getParentId, notEmptyIdSet.getIdSet()).count();
        if (count != 0) {
            ApiResultVO.error(BaseBizCodeEnum.PLEASE_DELETE_THE_CHILD_NODE_FIRST);
        }

        removeByIds(notEmptyIdSet.getIdSet());

        // 移除子表数据
        deleteByIdSetSub(notEmptyIdSet.getIdSet());

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 批量删除：移除子表数据
     */
    private void deleteByIdSetSub(Set<Long> idSet) {

        sysAreaRefDeptService.lambdaUpdate().in(SysAreaRefDeptDO::getDeptId, idSet).remove();

        SYsDeptRefUserService.lambdaUpdate().in(SysDeptRefUserDO::getDeptId, idSet).remove();

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysDeptDO infoById(NotNullId notNullId) {

        SysDeptInfoByIdVO sysDeptInfoByIdVO =
            BeanUtil.copyProperties(getById(notNullId.getId()), SysDeptInfoByIdVO.class);

        if (sysDeptInfoByIdVO == null) {
            return null;
        }

        // 获取：绑定的区域 idSet
        List<SysAreaRefDeptDO> sysAreaRefDeptDOList =
            sysAreaRefDeptService.lambdaQuery().eq(SysAreaRefDeptDO::getDeptId, notNullId.getId())
                .select(SysAreaRefDeptDO::getAreaId).list();
        Set<Long> areaIdSet =
            sysAreaRefDeptDOList.stream().map(SysAreaRefDeptDO::getAreaId).collect(Collectors.toSet());

        // 获取：绑定的用户 idSet
        List<SysDeptRefUserDO> sysDeptRefUserDOList =
            SYsDeptRefUserService.lambdaQuery().eq(SysDeptRefUserDO::getDeptId, notNullId.getId())
                .select(SysDeptRefUserDO::getUserId).list();
        Set<Long> userIdSet =
            sysDeptRefUserDOList.stream().map(SysDeptRefUserDO::getUserId).collect(Collectors.toSet());

        sysDeptInfoByIdVO.setAreaIdSet(areaIdSet);
        sysDeptInfoByIdVO.setUserIdSet(userIdSet);

        MyEntityUtil.handleParentId(sysDeptInfoByIdVO);

        return sysDeptInfoByIdVO;
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

        List<SysDeptDO> listByIds = listByIds(dto.getIdSet());

        for (SysDeptDO item : listByIds) {
            item.setOrderNo(item.getOrderNo() + dto.getNumber());
        }

        updateBatchById(listByIds);

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }
}




