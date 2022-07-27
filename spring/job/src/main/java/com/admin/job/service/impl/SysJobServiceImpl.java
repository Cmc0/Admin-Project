package com.admin.job.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
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
import com.admin.job.mapper.SysJobMapper;
import com.admin.job.model.dto.SysJobInsertOrUpdateDTO;
import com.admin.job.model.dto.SysJobPageDTO;
import com.admin.job.model.entity.SysJobDO;
import com.admin.job.model.entity.SysJobRefUserDO;
import com.admin.job.model.vo.SysJobInfoByIdVO;
import com.admin.job.service.SysJobRefUserService;
import com.admin.job.service.SysJobService;
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
public class SysJobServiceImpl extends ServiceImpl<SysJobMapper, SysJobDO> implements SysJobService {

    @Resource
    SysJobRefUserService sysJobRefUserService;

    /**
     * 新增/修改
     */
    @Override
    @Transactional
    public String insertOrUpdate(SysJobInsertOrUpdateDTO dto) {

        if (dto.getId() != null && dto.getId().equals(dto.getParentId())) {
            ApiResultVO.error(BaseBizCodeEnum.PARENT_ID_CANNOT_BE_EQUAL_TO_ID);
        }

        // 相同父节点下：岗位名（不能重复）
        Long count = lambdaQuery().eq(SysJobDO::getName, dto.getName())
            .eq(BaseEntityFour::getParentId, MyEntityUtil.getNotNullParentId(dto.getParentId()))
            .ne(dto.getId() != null, BaseEntityTwo::getId, dto.getId()).count();
        if (count != 0) {
            ApiResultVO.error("操作失败：相同父节点下，岗位名不能重复");
        }

        if (dto.getId() != null) { // 如果是修改
            deleteByIdSetSub(CollUtil.newHashSet(dto.getId())); // 先移除子表数据
        }

        SysJobDO sysJobDO = new SysJobDO();
        sysJobDO.setEnableFlag(dto.isEnableFlag());
        sysJobDO.setName(dto.getName());
        sysJobDO.setOrderNo(dto.getOrderNo());
        sysJobDO.setParentId(MyEntityUtil.getNotNullParentId(dto.getParentId()));
        sysJobDO.setId(dto.getId());
        sysJobDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));

        saveOrUpdate(sysJobDO);

        // 再新增子表数据
        if (CollUtil.isNotEmpty(dto.getUserIdSet())) {
            List<SysJobRefUserDO> insertList = new ArrayList<>();
            for (Long item : dto.getUserIdSet()) {
                SysJobRefUserDO sysJobRefUserDO = new SysJobRefUserDO();
                sysJobRefUserDO.setJobId(sysJobDO.getId());
                sysJobRefUserDO.setUserId(item);
                insertList.add(sysJobRefUserDO);
            }
            sysJobRefUserService.saveBatch(insertList);
        }

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 分页排序查询：岗位
     */
    @Override
    public Page<SysJobDO> myPage(SysJobPageDTO dto) {

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getName()), SysJobDO::getName, dto.getName())
            .like(StrUtil.isNotBlank(dto.getRemark()), BaseEntityThree::getRemark, dto.getRemark())
            .eq(dto.getEnableFlag() != null, BaseEntityThree::getEnableFlag, dto.getEnableFlag())
            .eq(BaseEntityThree::getDelFlag, false).orderByDesc(BaseEntityFour::getOrderNo).page(dto.getPage(true));
    }

    /**
     * 查询岗位（树结构）
     */
    @Override
    public List<SysJobDO> tree(SysJobPageDTO dto) {

        List<SysJobDO> resultList = new ArrayList<>();

        // 根据条件进行筛选，得到符合条件的数据，然后再逆向生成整棵树，并返回这个树结构
        dto.setPageSize(-1); // 不分页
        List<SysJobDO> sysJobDOList = myPage(dto).getRecords();

        if (sysJobDOList.size() == 0) {
            return resultList;
        }

        List<SysJobDO> allList = list();

        if (allList.size() == 0) {
            return resultList;
        }

        return MyTreeUtil.getFullTreeByDeepNode(sysJobDOList, allList);
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

        deleteByIdSetSub(notEmptyIdSet.getIdSet()); // 移除子表数据

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 批量删除：移除子表数据
     */
    private void deleteByIdSetSub(Set<Long> idSet) {

        sysJobRefUserService.removeByIds(idSet);

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysJobInfoByIdVO infoById(NotNullId notNullId) {

        SysJobInfoByIdVO sysJobInfoByIdVO = BeanUtil.copyProperties(getById(notNullId.getId()), SysJobInfoByIdVO.class);

        if (sysJobInfoByIdVO == null) {
            return null;
        }

        // 获取：绑定的用户 idSet
        List<SysJobRefUserDO> sysJobRefUserDOList =
            sysJobRefUserService.lambdaQuery().eq(SysJobRefUserDO::getJobId, notNullId.getId())
                .select(SysJobRefUserDO::getUserId).list();
        Set<Long> userIdSet = sysJobRefUserDOList.stream().map(SysJobRefUserDO::getUserId).collect(Collectors.toSet());

        sysJobInfoByIdVO.setUserIdSet(userIdSet);

        MyEntityUtil.handleParentId(sysJobInfoByIdVO);

        return sysJobInfoByIdVO;
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

        List<SysJobDO> listByIds = listByIds(dto.getIdSet());

        for (SysJobDO item : listByIds) {
            item.setOrderNo(item.getOrderNo() + dto.getNumber());
        }

        updateBatchById(listByIds);

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();

    }
}




