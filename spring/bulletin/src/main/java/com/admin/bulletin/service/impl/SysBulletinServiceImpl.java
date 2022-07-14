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
import com.admin.common.model.constant.BaseConstant;
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
     * 新增/修改
     */
    @Override
    @Transactional
    public String insertOrUpdate(SysBulletinInsertOrUpdateDTO dto) {

        if (dto.getId() != null) {
            // 如果是修改，则加锁
            RLock lock =
                redissonClient.getLock(BaseConstant.PRE_REDISSON + BaseConstant.PRE_LOCK_BULLETIN_ID + dto.getId());
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
     * 执行：新增/修改 公告
     */
    private void doInsertOrUpdate(SysBulletinInsertOrUpdateDTO dto) {

        if (dto.getId() != null) {
            boolean exists = lambdaQuery().eq(BaseEntityTwo::getId, dto.getId())
                .eq(SysBulletinDO::getStatus, SysBulletinStatusEnum.DRAFT).exists();
            if (!exists) {
                ApiResultVO.error("操作失败：只能修改草稿状态的公告");
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

        saveOrUpdate(sysBulletinDO); // 操作数据库
    }

    /**
     * 发布 公告
     */
    @Override
    public String publish(NotNullId notNullId) {

        RLock lock =
            redissonClient.getLock(BaseConstant.PRE_REDISSON + BaseConstant.PRE_LOCK_BULLETIN_ID + notNullId.getId());
        lock.lock();

        try {

            SysBulletinDO sysBulletinDO = lambdaQuery().eq(BaseEntityTwo::getId, notNullId.getId())
                .select(BaseEntityTwo::getId, SysBulletinDO::getStatus, SysBulletinDO::getPublishTime,
                    SysBulletinDO::getTitle).one();

            if (SysBulletinStatusEnum.PUBLICITY.equals(sysBulletinDO.getStatus())) {
                ApiResultVO.error("操作失败：已经发布过了，请刷新重试");
            }

            int compare = DateUtil.compare(sysBulletinDO.getPublishTime(), new Date());
            if (compare < 0) {
                ApiResultVO.error("操作失败：发布时间晚于当前时间，请刷新重试");
            }

            sysBulletinDO.setStatus(SysBulletinStatusEnum.PUBLICITY);

            // 增加一个定时任务，用于：让用户收到 webSocket的提示
            XxlJobInsertOrUpdateDTO xxlJobInsertOrUpdateDTO = new XxlJobInsertOrUpdateDTO();
            xxlJobInsertOrUpdateDTO.setJobDesc("webSocket：有新的公告【" + sysBulletinDO.getTitle() + "】");
            xxlJobInsertOrUpdateDTO.setProSendTime(sysBulletinDO.getPublishTime());
            xxlJobInsertOrUpdateDTO.setExecutorHandler(BulletinPublishTask.BULLETIN_PUBLISH);
            Long xxlJobId = xxlJobService.insert(xxlJobInsertOrUpdateDTO);

            // 设置：定时任务 主键id
            sysBulletinDO.setXxlJobId(xxlJobId);

            updateById(sysBulletinDO); // 操作数据库

            return BaseBizCodeEnum.API_RESULT_OK.getMsg();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 撤回 公告
     */
    @Override
    public String revoke(NotNullId notNullId) {
        RLock lock =
            redissonClient.getLock(BaseConstant.PRE_REDISSON + BaseConstant.PRE_LOCK_BULLETIN_ID + notNullId.getId());
        lock.lock();
        try {

            SysBulletinDO sysBulletinDO = lambdaQuery().eq(BaseEntityTwo::getId, notNullId.getId())
                .select(BaseEntityTwo::getId, SysBulletinDO::getStatus, SysBulletinDO::getPublishTime,
                    SysBulletinDO::getXxlJobId).one();

            if (SysBulletinStatusEnum.DRAFT.equals(sysBulletinDO.getStatus())) {
                ApiResultVO.error("操作失败：已经撤回过了，请刷新重试");
            }

            // 判断发布时间是否晚于当前时间
            int compare = DateUtil.compare(sysBulletinDO.getPublishTime(), new Date());
            if (compare < 0) {
                ApiResultVO.error("操作失败：发布时间晚于当前时间，无法撤回");
            }

            sysBulletinDO.setStatus(SysBulletinStatusEnum.DRAFT);

            sysBulletinDO.setXxlJobId(MyEntityUtil.getNotNullLong(null)); // 设置 定时任务 主键id

            updateById(sysBulletinDO); // 操作数据库

            // 撤回时，删除创建的定时任务
            xxlJobService.deleteById(new NotNullId(sysBulletinDO.getXxlJobId()));

            return BaseBizCodeEnum.API_RESULT_OK.getMsg();

        } finally {
            lock.unlock();
        }
    }

    /**
     * 分页排序查询
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
     * 通过主键id，查看详情
     */
    @Override
    public SysBulletinDO infoById(NotNullId notNullId) {
        return getById(notNullId.getId());
    }

    /**
     * 批量删除
     */
    @Override
    public String deleteByIdSet(NotEmptyIdSet notEmptyIdSet) {

        // 设置连锁
        RLock multiLock =
            MultiLockUtil.getMultiLockForLong(BaseConstant.PRE_LOCK_BULLETIN_ID, notEmptyIdSet.getIdSet());
        multiLock.lock();
        try {

            Long count = lambdaQuery().in(BaseEntityTwo::getId, notEmptyIdSet.getIdSet())
                .eq(SysBulletinDO::getStatus, SysBulletinStatusEnum.DRAFT).count();
            if (count != notEmptyIdSet.getIdSet().size()) {
                ApiResultVO.error("操作失败：只能删除草稿状态的公告");
            }

            removeByIds(notEmptyIdSet.getIdSet());

            return BaseBizCodeEnum.API_RESULT_OK.getMsg();
        } finally {
            multiLock.unlock();
        }
    }

    /**
     * 分页排序查询：当前用户可以查看的公告
     */
    @Override
    public Page<SysBulletinDO> userSelfPage(SysBulletinUserSelfPageDTO dto) {

        Long currentUserId = UserUtil.getCurrentUserId();

        // 备注，dto.getPageSize()：0 点击关闭公告横幅 1 查询用户最新一条公告，用于横幅展示
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

                // 通知该用户，刷新 公告信息
                KafkaUtil.bulletinPublish(Collections.singleton(currentUserId));
            });
        }

        if (dto.getPageSize() == 0) {
            return dto.getPage(false); // 不去查询数据
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
     * 获取：当前用户可以查看的公告，总数
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
