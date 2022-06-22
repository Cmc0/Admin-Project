package com.admin.dict.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.mapper.SysDictMapper;
import com.admin.common.model.dto.AddOrderNoDTO;
import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullId;
import com.admin.common.model.entity.BaseEntityThree;
import com.admin.common.model.entity.BaseEntityTwo;
import com.admin.common.model.entity.SysDictDO;
import com.admin.common.model.enums.SysDictTypeEnum;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.common.util.MyEntityUtil;
import com.admin.dict.exception.BizCodeEnum;
import com.admin.dict.model.dto.SysDictInsertOrUpdateDTO;
import com.admin.dict.model.dto.SysDictPageDTO;
import com.admin.dict.model.vo.SysDictTreeVO;
import com.admin.dict.service.SysDictService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDictDO> implements SysDictService {

    /**
     * 新增/修改
     */
    @Override
    @Transactional
    public String insertOrUpdate(SysDictInsertOrUpdateDTO dto) {

        SysDictTypeEnum sysDictTypeEnum = SysDictTypeEnum.getByCode(dto.getType());
        if (sysDictTypeEnum == null) {
            ApiResultVO.error("操作失败：type【" + dto.getType() + "】不合法");
        }

        if (sysDictTypeEnum.equals(SysDictTypeEnum.DICT)) {
            // 字典 key和 name不能重复
            Long count = lambdaQuery().eq(SysDictDO::getType, SysDictTypeEnum.DICT)
                .and(i -> i.eq(SysDictDO::getDictKey, dto.getDictKey()).or().eq(SysDictDO::getName, dto.getName()))
                .eq(BaseEntityThree::getEnableFlag, true).ne(dto.getId() != null, BaseEntityTwo::getId, dto.getId())
                .count();
            if (count != 0) {
                ApiResultVO.error(BizCodeEnum.SAME_KEY_OR_NAME_EXISTS);
            }
            dto.setValue((byte)-1); // 字典的value为 -1
        } else {
            if (dto.getValue() == null) {
                ApiResultVO.error(BizCodeEnum.VALUE_CANNOT_BE_EMPTY);
            }
            // 字典项 value和 name不能重复
            Long count =
                lambdaQuery().eq(SysDictDO::getType, SysDictTypeEnum.DICT_ITEM).eq(BaseEntityThree::getEnableFlag, true)
                    .eq(SysDictDO::getDictKey, dto.getDictKey())
                    .and(i -> i.eq(SysDictDO::getValue, dto.getValue()).or().eq(SysDictDO::getName, dto.getName()))
                    .ne(dto.getId() != null, BaseEntityTwo::getId, dto.getId()).count();
            if (count != 0) {
                ApiResultVO.error(BizCodeEnum.SAME_VALUE_OR_NAME_EXISTS);
            }
        }

        if (dto.getId() != null && sysDictTypeEnum.equals(SysDictTypeEnum.DICT)) {
            // 如果是修改，并且是字典，那么也需要修改 该字典的字典项的 dictKey
            SysDictDO sysDictDO =
                lambdaQuery().eq(BaseEntityTwo::getId, dto.getId()).select(SysDictDO::getDictKey).one();
            if (sysDictDO == null) {
                ApiResultVO.error("操作失败：字典不存在，请刷新重试");
            }
            if (!sysDictDO.getDictKey().equals(dto.getDictKey())) {
                lambdaUpdate().eq(SysDictDO::getDictKey, sysDictDO.getDictKey())
                    .eq(SysDictDO::getType, SysDictTypeEnum.DICT_ITEM).set(SysDictDO::getDictKey, dto.getDictKey())
                    .update();
            }
        }

        SysDictDO sysDictDO = new SysDictDO();
        sysDictDO.setDictKey(dto.getDictKey());
        sysDictDO.setName(dto.getName());
        sysDictDO.setType(sysDictTypeEnum);
        sysDictDO.setValue(dto.getValue());
        sysDictDO.setOrderNo(dto.getOrderNo());
        sysDictDO.setEnableFlag(dto.isEnableFlag());
        sysDictDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));
        sysDictDO.setId(dto.getId());
        saveOrUpdate(sysDictDO);

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysDictDO> myPage(SysDictPageDTO dto) {

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getName()), SysDictDO::getName, dto.getName())
            .like(StrUtil.isNotBlank(dto.getRemark()), BaseEntityThree::getRemark, dto.getRemark())
            .like(StrUtil.isNotBlank(dto.getDictKey()), SysDictDO::getDictKey, dto.getDictKey())
            .eq(dto.getId() != null, BaseEntityTwo::getId, dto.getId())
            .eq(dto.getType() != null, SysDictDO::getType, dto.getType())
            .eq(dto.getEnableFlag() != null, BaseEntityThree::getEnableFlag, dto.getEnableFlag())
            .orderByDesc(SysDictDO::getOrderNo).page(dto.getPage());
    }

    /**
     * 查询：树结构
     */
    @Override
    public List<SysDictTreeVO> tree(SysDictPageDTO dto) {

        dto.setPageSize(-1); // 不分页
        List<SysDictDO> records = myPage(dto).getRecords();

        List<SysDictTreeVO> resList = new ArrayList<>();

        if (records.size() == 0) {
            return resList;
        }

        // 过滤出为 字典项的数据，目的：查询其所属字典，封装成树结构
        List<SysDictDO> dictItemList =
            records.stream().filter(it -> SysDictTypeEnum.DICT_ITEM.equals(it.getType())).collect(Collectors.toList());

        if (dictItemList.size() == 0) {
            // 如果没有字典项类型数据，则直接返回
            for (SysDictDO item : records) {
                SysDictTreeVO treeVO = BeanUtil.copyProperties(item, SysDictTreeVO.class);
                resList.add(treeVO);
            }
            return resList;
        }

        // 查询出 字典项所属 字典的信息
        List<SysDictDO> allDictList =
            records.stream().filter(item -> SysDictTypeEnum.DICT.equals(item.getType())).collect(Collectors.toList());

        Set<Long> dictIdSet = allDictList.stream().map(BaseEntityTwo::getId).collect(Collectors.toSet());
        Set<String> dictKeySet = dictItemList.stream().map(SysDictDO::getDictKey).collect(Collectors.toSet());

        // 查询数据库
        List<SysDictDO> sysDictDOList = lambdaQuery().notIn(dictIdSet.size() != 0, BaseEntityTwo::getId, dictIdSet)
            .in(dictKeySet.size() != 0, SysDictDO::getDictKey, dictKeySet).eq(SysDictDO::getType, SysDictTypeEnum.DICT)
            .orderByDesc(SysDictDO::getOrderNo).list();

        // 拼接本次返回值所需的所有 字典
        allDictList.addAll(sysDictDOList);

        for (SysDictDO item : allDictList) {
            SysDictTreeVO treeVO = BeanUtil.copyProperties(item, SysDictTreeVO.class);
            resList.add(treeVO);
        }

        // 封装 children
        for (SysDictDO item : dictItemList) {
            SysDictTreeVO sysDictTreeVO = BeanUtil.copyProperties(item, SysDictTreeVO.class);
            for (SysDictTreeVO subItem : resList) {
                if (subItem.getDictKey().equals(item.getDictKey())) {
                    List<SysDictTreeVO> children = subItem.getChildren();
                    if (children == null) {
                        children = new ArrayList<>();
                        subItem.setChildren(children);
                    }
                    children.add(sysDictTreeVO);
                    break;
                }
            }
        }

        return resList;
    }

    /**
     * 批量删除
     */
    @Override
    @Transactional
    public String deleteByIdSet(NotEmptyIdSet notEmptyIdSet) {

        List<SysDictDO> sysDictDOList = lambdaQuery().in(BaseEntityTwo::getId, notEmptyIdSet.getIdSet())
            .eq(SysDictDO::getType, SysDictTypeEnum.DICT).select(SysDictDO::getDictKey).list();

        // 根据 idSet删除
        removeByIds(notEmptyIdSet.getIdSet());

        if (CollUtil.isEmpty(sysDictDOList)) {
            return BaseBizCodeEnum.API_RESULT_OK.getMsg();
        }

        // 如果删除是字典项的父级，则把其下的字典项也跟着删除了
        Set<String> dictKeySet = sysDictDOList.stream().map(SysDictDO::getDictKey).collect(Collectors.toSet());

        lambdaUpdate().in(SysDictDO::getDictKey, dictKeySet).remove();

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysDictDO infoById(NotNullId notNullId) {
        return getById(notNullId.getId());
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

        List<SysDictDO> listByIds = listByIds(dto.getIdSet());

        for (SysDictDO item : listByIds) {
            item.setOrderNo(item.getOrderNo() + dto.getNumber());
        }

        updateBatchById(listByIds);

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }
}




