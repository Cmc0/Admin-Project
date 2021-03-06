package com.admin.bulletin.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.admin.bulletin.mapper.SysBulletinMapper;
import com.admin.bulletin.model.dto.SysBulletinInsertOrUpdateDTO;
import com.admin.bulletin.model.dto.SysBulletinPageDTO;
import com.admin.bulletin.model.dto.SysBulletinUserSelfPageDTO;
import com.admin.bulletin.model.entity.SysBulletinDO;
import com.admin.bulletin.model.entity.SysBulletinReadTimeRefUserDO;
import com.admin.bulletin.model.enums.SysBulletinStatusEnum;
import com.admin.bulletin.service.SysBulletinReadTimeRefUserService;
import com.admin.bulletin.service.SysBulletinService;
import com.admin.bulletin.task.BulletinPublishTask;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.model.constant.BaseRedisConstant;
import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullId;
import com.admin.common.model.entity.BaseEntity;
import com.admin.common.model.entity.BaseEntityThree;
import com.admin.common.model.entity.BaseEntityTwo;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.common.util.KafkaUtil;
import com.admin.common.util.MultiLockUtil;
import com.admin.common.util.MyEntityUtil;
import com.admin.common.util.UserUtil;
import com.admin.xxljob.dto.XxlJobInsertOrUpdateDTO;
import com.admin.xxljob.service.XxlJobService;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;

@Service
public class SysBulletinServiceImpl extends ServiceImpl<SysBulletinMapper, SysBulletinDO>
    implements SysBulletinService {

    @Resource
    RedissonClient redissonClient;
    @Resource
    XxlJobService xxlJobService;
    @Resource
    SysBulletinReadTimeRefUserService sysBulletinReadTimeRefUserService;

    /**
     * ??????/??????
     */
    @Override
    @Transactional
    public String insertOrUpdate(SysBulletinInsertOrUpdateDTO dto) {

        if (dto.getId() != null) {
            // ???????????????????????????
            RLock lock =
                redissonClient.getLock(BaseRedisConstant.PRE_REDISSON + BaseRedisConstant.PRE_LOCK_BULLETIN_ID + dto.getId());
            lock.lock();
            try {
                doInsertOrUpdate(dto);
            } finally {
                lock.unlock();
            }
        } else {
            doInsertOrUpdate(dto);
        }

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * ???????????????/?????? ??????
     */
    private void doInsertOrUpdate(SysBulletinInsertOrUpdateDTO dto) {

        if (dto.getId() != null) {
            boolean exists = lambdaQuery().eq(BaseEntityTwo::getId, dto.getId())
                .eq(SysBulletinDO::getStatus, SysBulletinStatusEnum.DRAFT).exists();
            if (!exists) {
                ApiResultVO.error("????????????????????????????????????????????????");
            }
        }

        SysBulletinDO sysBulletinDO = new SysBulletinDO();
        sysBulletinDO.setType(dto.getType());
        sysBulletinDO.setContent(dto.getContent());
        sysBulletinDO.setTitle(dto.getTitle());
        sysBulletinDO.setPublishTime(dto.getPublishTime());
        sysBulletinDO.setStatus(SysBulletinStatusEnum.DRAFT);
        sysBulletinDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));
        sysBulletinDO.setId(dto.getId());
        sysBulletinDO.setXxlJobId(MyEntityUtil.getNotNullLong(null));

        saveOrUpdate(sysBulletinDO); // ???????????????
    }

    /**
     * ?????? ??????
     */
    @Override
    public String publish(NotNullId notNullId) {

        RLock lock =
            redissonClient.getLock(BaseRedisConstant.PRE_REDISSON + BaseRedisConstant.PRE_LOCK_BULLETIN_ID + notNullId.getId());
        lock.lock();

        try {

            SysBulletinDO sysBulletinDO = lambdaQuery().eq(BaseEntityTwo::getId, notNullId.getId())
                .select(BaseEntityTwo::getId, SysBulletinDO::getStatus, SysBulletinDO::getPublishTime,
                    SysBulletinDO::getTitle).one();

            if (SysBulletinStatusEnum.PUBLICITY.equals(sysBulletinDO.getStatus())) {
                ApiResultVO.error("???????????????????????????????????????????????????");
            }

            int compare = DateUtil.compare(sysBulletinDO.getPublishTime(), new Date());
            if (compare < 0) {
                ApiResultVO.error("???????????????????????????????????????????????????????????????");
            }

            sysBulletinDO.setStatus(SysBulletinStatusEnum.PUBLICITY);

            // ??????????????????????????????????????????????????? webSocket?????????
            XxlJobInsertOrUpdateDTO xxlJobInsertOrUpdateDTO = new XxlJobInsertOrUpdateDTO();
            xxlJobInsertOrUpdateDTO.setJobDesc("webSocket?????????????????????" + sysBulletinDO.getTitle() + "???");
            xxlJobInsertOrUpdateDTO.setProSendTime(sysBulletinDO.getPublishTime());
            xxlJobInsertOrUpdateDTO.setExecutorHandler(BulletinPublishTask.BULLETIN_PUBLISH);
            Long xxlJobId = xxlJobService.insert(xxlJobInsertOrUpdateDTO);

            // ????????????????????? ??????id
            sysBulletinDO.setXxlJobId(xxlJobId);

            updateById(sysBulletinDO); // ???????????????

            return BaseBizCodeEnum.API_RESULT_OK.getMsg();
        } finally {
            lock.unlock();
        }
    }

    /**
     * ?????? ??????
     */
    @Override
    public String revoke(NotNullId notNullId) {
        RLock lock =
            redissonClient.getLock(BaseRedisConstant.PRE_REDISSON + BaseRedisConstant.PRE_LOCK_BULLETIN_ID + notNullId.getId());
        lock.lock();
        try {

            SysBulletinDO sysBulletinDO = lambdaQuery().eq(BaseEntityTwo::getId, notNullId.getId())
                .select(BaseEntityTwo::getId, SysBulletinDO::getStatus, SysBulletinDO::getPublishTime,
                    SysBulletinDO::getXxlJobId).one();

            if (SysBulletinStatusEnum.DRAFT.equals(sysBulletinDO.getStatus())) {
                ApiResultVO.error("???????????????????????????????????????????????????");
            }

            // ??????????????????????????????????????????
            int compare = DateUtil.compare(sysBulletinDO.getPublishTime(), new Date());
            if (compare < 0) {
                ApiResultVO.error("????????????????????????????????????????????????????????????");
            }

            sysBulletinDO.setStatus(SysBulletinStatusEnum.DRAFT);

            sysBulletinDO.setXxlJobId(MyEntityUtil.getNotNullLong(null)); // ?????? ???????????? ??????id

            updateById(sysBulletinDO); // ???????????????

            // ???????????????????????????????????????
            xxlJobService.deleteById(new NotNullId(sysBulletinDO.getXxlJobId()));

            return BaseBizCodeEnum.API_RESULT_OK.getMsg();

        } finally {
            lock.unlock();
        }
    }

    /**
     * ??????????????????
     */
    @Override
    public Page<SysBulletinDO> myPage(SysBulletinPageDTO dto) {

        return lambdaQuery().eq(dto.getStatus() != null, SysBulletinDO::getStatus, dto.getStatus())
            .eq(dto.getCreateId() != null, BaseEntity::getCreateId, dto.getCreateId())
            .like(StrUtil.isNotBlank(dto.getRemark()), BaseEntityThree::getRemark, dto.getRemark())
            .eq(dto.getXxlJobId() != null, SysBulletinDO::getXxlJobId, dto.getXxlJobId())
            .like(StrUtil.isNotBlank(dto.getContent()), SysBulletinDO::getContent, dto.getContent())
            .like(StrUtil.isNotBlank(dto.getTitle()), SysBulletinDO::getTitle, dto.getTitle())
            .eq(dto.getType() != null, SysBulletinDO::getType, dto.getType())
            .le(dto.getPtEndTime() != null, SysBulletinDO::getPublishTime, dto.getPtEndTime())
            .ge(dto.getPtBeginTime() != null, SysBulletinDO::getPublishTime, dto.getPtBeginTime())
            .eq(BaseEntityThree::getDelFlag, false).orderByDesc(dto.notHasOrder(), SysBulletinDO::getPublishTime)
            .page(dto.getPage(true));
    }

    /**
     * ????????????id???????????????
     */
    @Override
    public SysBulletinDO infoById(NotNullId notNullId) {
        return getById(notNullId.getId());
    }

    /**
     * ????????????
     */
    @Override
    public String deleteByIdSet(NotEmptyIdSet notEmptyIdSet) {

        // ????????????
        RLock multiLock =
            MultiLockUtil.getMultiLockForLong(BaseRedisConstant.PRE_LOCK_BULLETIN_ID, notEmptyIdSet.getIdSet());
        multiLock.lock();
        try {

            Long count = lambdaQuery().in(BaseEntityTwo::getId, notEmptyIdSet.getIdSet())
                .eq(SysBulletinDO::getStatus, SysBulletinStatusEnum.DRAFT).count();
            if (count != notEmptyIdSet.getIdSet().size()) {
                ApiResultVO.error("????????????????????????????????????????????????");
            }

            removeByIds(notEmptyIdSet.getIdSet());

            return BaseBizCodeEnum.API_RESULT_OK.getMsg();
        } finally {
            multiLock.unlock();
        }
    }

    /**
     * ??????????????????????????????????????????????????????
     */
    @Override
    public Page<SysBulletinDO> userSelfPage(SysBulletinUserSelfPageDTO dto) {

        Long currentUserId = UserUtil.getCurrentUserId();

        // ?????????dto.getPageSize()???0 ???????????????????????? 1 ???????????????????????????????????????????????????
        if (dto.getPageSize() != 1) {
            ThreadUtil.execute(() -> {
                SysBulletinReadTimeRefUserDO sysBulletinReadTimeRefUserDO = new SysBulletinReadTimeRefUserDO();
                sysBulletinReadTimeRefUserDO.setUserId(currentUserId);
                sysBulletinReadTimeRefUserDO.setBulletinReadTime(new Date());
                boolean exists = sysBulletinReadTimeRefUserService.lambdaQuery()
                    .eq(SysBulletinReadTimeRefUserDO::getUserId, currentUserId).exists();
                if (exists) {
                    sysBulletinReadTimeRefUserService.updateById(sysBulletinReadTimeRefUserDO);
                } else {
                    sysBulletinReadTimeRefUserService.save(sysBulletinReadTimeRefUserDO);
                }

                // ???????????????????????? ????????????
                KafkaUtil.newBulletin(Collections.singleton(currentUserId));
            });
        }

        if (dto.getPageSize() == 0) {
            return dto.getPage(false); // ??????????????????
        }

        return lambdaQuery().eq(BaseEntityThree::getDelFlag, false)
            .eq(SysBulletinDO::getStatus, SysBulletinStatusEnum.PUBLICITY).le(SysBulletinDO::getPublishTime, new Date())
            .like(StrUtil.isNotBlank(dto.getTitle()), SysBulletinDO::getTitle, dto.getTitle())
            .eq(dto.getType() != null, SysBulletinDO::getType, dto.getType())
            .like(StrUtil.isNotBlank(dto.getContent()), SysBulletinDO::getContent, dto.getContent())
            .le(dto.getPtEndTime() != null, SysBulletinDO::getPublishTime, dto.getPtEndTime())
            .ge(dto.getPtBeginTime() != null, SysBulletinDO::getPublishTime, dto.getPtBeginTime())
            .select(SysBulletinDO::getTitle, SysBulletinDO::getType, SysBulletinDO::getContent,
                SysBulletinDO::getPublishTime, BaseEntityTwo::getId, BaseEntity::getCreateId)
            .orderByDesc(dto.notHasOrder(), SysBulletinDO::getPublishTime).page(dto.getPage(true));
    }

    /**
     * ???????????????????????????????????????????????????
     */
    @Override
    public Long userSelfCount() {

        Long userIdSafe = UserUtil.getCurrentUserId();

        SysBulletinReadTimeRefUserDO sysBulletinReadTimeRefUserDO =
            sysBulletinReadTimeRefUserService.lambdaQuery().eq(SysBulletinReadTimeRefUserDO::getUserId, userIdSafe)
                .select(SysBulletinReadTimeRefUserDO::getBulletinReadTime).one();

        LambdaQueryChainWrapper<SysBulletinDO> lambdaQueryChainWrapper =
            lambdaQuery().eq(BaseEntityThree::getDelFlag, false)
                .eq(SysBulletinDO::getStatus, SysBulletinStatusEnum.PUBLICITY)
                .le(SysBulletinDO::getPublishTime, new Date());

        if (sysBulletinReadTimeRefUserDO != null) {
            lambdaQueryChainWrapper
                .ge(SysBulletinDO::getPublishTime, sysBulletinReadTimeRefUserDO.getBulletinReadTime());
        }

        return lambdaQueryChainWrapper.count();
    }
}
