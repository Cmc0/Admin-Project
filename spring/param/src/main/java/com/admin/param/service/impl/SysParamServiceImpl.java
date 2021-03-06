package com.admin.param.service.impl;

import cn.hutool.core.util.StrUtil;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.mapper.SysParamMapper;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.constant.BaseRedisConstant;
import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullId;
import com.admin.common.model.entity.BaseEntity;
import com.admin.common.model.entity.BaseEntityThree;
import com.admin.common.model.entity.SysParamDO;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.common.util.MyEntityUtil;
import com.admin.common.util.SysParamUtil;
import com.admin.param.model.dto.SysParamInsertOrUpdateDTO;
import com.admin.param.model.dto.SysParamPageDTO;
import com.admin.param.service.SysParamService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class SysParamServiceImpl extends ServiceImpl<SysParamMapper, SysParamDO> implements SysParamService {

    @Resource
    RedissonClient redissonClient;

    /**
     * 新增/修改
     */
    @Override
    @Transactional
    public String insertOrUpdate(SysParamInsertOrUpdateDTO dto) {

        RLock lock = redissonClient.getLock(BaseRedisConstant.PRE_REDISSON + BaseRedisConstant.PRE_REDIS_PARAM_CACHE);
        lock.lock();

        try {
            SysParamDO sysParamDO = new SysParamDO();
            sysParamDO.setName(dto.getName());
            sysParamDO.setValue(dto.getValue());
            sysParamDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));
            sysParamDO.setId(dto.getId());

            saveOrUpdate(sysParamDO);

            SysParamUtil.updateRedisCache(false); // 更新 redis中【系统参数】的缓存

            return BaseBizCodeEnum.API_RESULT_OK.getMsg();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysParamDO> myPage(SysParamPageDTO dto) {

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getName()), SysParamDO::getName, dto.getName())
            .like(StrUtil.isNotBlank(dto.getRemark()), BaseEntityThree::getRemark, dto.getRemark())
            .eq(dto.getEnableFlag() != null, BaseEntityThree::getEnableFlag, dto.getEnableFlag())
            .eq(BaseEntityThree::getDelFlag, false).orderByDesc(BaseEntity::getUpdateTime).page(dto.getPage(true));

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysParamDO infoById(NotNullId notNullId) {
        return getById(notNullId.getId());
    }

    /**
     * 批量删除
     */
    @Override
    @Transactional
    public String deleteByIdSet(NotEmptyIdSet notEmptyIdSet) {

        if (notEmptyIdSet.getIdSet().contains(BaseConstant.RSA_PRIVATE_KEY_ID)) {
            ApiResultVO.error("操作失败：id【" + BaseConstant.RSA_PRIVATE_KEY_ID + "】不允许删除");
        }

        if (notEmptyIdSet.getIdSet().contains(BaseConstant.IP_REQUESTS_PER_SECOND_ID)) {
            ApiResultVO.error("操作失败：id【" + BaseConstant.IP_REQUESTS_PER_SECOND_ID + "】不允许删除");
        }

        RLock lock = redissonClient.getLock(BaseRedisConstant.PRE_REDISSON + BaseRedisConstant.PRE_REDIS_PARAM_CACHE);
        lock.lock();

        try {
            removeByIds(notEmptyIdSet.getIdSet());

            SysParamUtil.updateRedisCache(false); // 更新 redis中【系统参数】的缓存

            return BaseBizCodeEnum.API_RESULT_OK.getMsg();
        } finally {
            lock.unlock();
        }
    }
}




