package com.admin.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.admin.common.configuration.BaseConfiguration;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.constant.BaseRedisConstant;
import com.admin.common.model.constant.BaseRegexConstant;
import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullId;
import com.admin.common.model.entity.BaseEntityTwo;
import com.admin.common.model.entity.SysRoleRefUserDO;
import com.admin.common.model.entity.SysUserDO;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.common.model.vo.DictLongListVO;
import com.admin.common.util.*;
import com.admin.dept.model.entity.SysDeptRefUserDO;
import com.admin.dept.service.SysDeptRefUserService;
import com.admin.file.model.dto.SysFileRemoveDTO;
import com.admin.file.model.entity.SysFileDO;
import com.admin.file.service.SysFileService;
import com.admin.job.model.entity.SysJobRefUserDO;
import com.admin.job.service.SysJobRefUserService;
import com.admin.role.service.SysRoleRefUserService;
import com.admin.user.exception.BizCodeEnum;
import com.admin.user.mapper.SysUserProMapper;
import com.admin.user.model.dto.SysUserDictListDTO;
import com.admin.user.model.dto.SysUserInsertOrUpdateDTO;
import com.admin.user.model.dto.SysUserPageDTO;
import com.admin.user.model.dto.SysUserUpdatePasswordDTO;
import com.admin.user.model.vo.SysUserInfoByIdVO;
import com.admin.user.model.vo.SysUserPageVO;
import com.admin.user.service.SysUserService;
import com.admin.websocket.service.SysWebSocketService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserProMapper, SysUserDO> implements SysUserService {

    @Resource
    RedissonClient redissonClient;
    @Resource
    SysRoleRefUserService sysRoleRefUserService;
    @Resource
    SysDeptRefUserService sysDeptRefUserService;
    @Resource
    SysJobRefUserService sysJobRefUserService;
    @Resource
    SysFileService sysFileService;
    @Resource
    SysWebSocketService sysWebSocketService;

    /**
     * ??????????????????
     */
    @Override
    public Page<SysUserPageVO> myPage(SysUserPageDTO dto) {

        Page<SysUserPageVO> page = baseMapper.myPage(dto.getCreateTimeDescDefaultOrderPage(), dto);

        Set<Long> userIdSet = new HashSet<>();

        for (SysUserPageVO item : page.getRecords()) {
            item.setEmail(DesensitizedUtil.email(item.getEmail())); // ??????
            userIdSet.add(item.getId());
        }

        if (userIdSet.size() != 0) {

            List<SysRoleRefUserDO> sysRoleRefUserDOList =
                sysRoleRefUserService.lambdaQuery().in(SysRoleRefUserDO::getUserId, userIdSet)
                    .select(SysRoleRefUserDO::getUserId, SysRoleRefUserDO::getRoleId).list();

            List<SysDeptRefUserDO> sysDeptRefUserDOList =
                sysDeptRefUserService.lambdaQuery().in(SysDeptRefUserDO::getUserId, userIdSet)
                    .select(SysDeptRefUserDO::getUserId, SysDeptRefUserDO::getDeptId).list();

            List<SysJobRefUserDO> sysJobRefUserDOList =
                sysJobRefUserService.lambdaQuery().in(SysJobRefUserDO::getUserId, userIdSet)
                    .select(SysJobRefUserDO::getUserId, SysJobRefUserDO::getJobId).list();

            Map<Long, Set<Long>> roleUserGroupMap = sysRoleRefUserDOList.stream().collect(Collectors
                .groupingBy(SysRoleRefUserDO::getUserId,
                    Collectors.mapping(SysRoleRefUserDO::getRoleId, Collectors.toSet())));

            Map<Long, Set<Long>> deptUserGroupMap = sysDeptRefUserDOList.stream().collect(Collectors
                .groupingBy(SysDeptRefUserDO::getUserId,
                    Collectors.mapping(SysDeptRefUserDO::getDeptId, Collectors.toSet())));

            Map<Long, Set<Long>> jobUserGroupMap = sysJobRefUserDOList.stream().collect(Collectors
                .groupingBy(SysJobRefUserDO::getUserId,
                    Collectors.mapping(SysJobRefUserDO::getJobId, Collectors.toSet())));

            page.getRecords().forEach(it -> {
                it.setRoleIdSet(roleUserGroupMap.get(it.getId()));
                it.setDeptIdSet(deptUserGroupMap.get(it.getId()));
                it.setJobIdSet(jobUserGroupMap.get(it.getId()));
            });
        }

        return page;
    }

    /**
     * ????????????
     */
    @Override
    public List<DictLongListVO> dictList(SysUserDictListDTO dto) {

        List<SysUserDO> sysUserDOList = lambdaQuery().select(SysUserDO::getNickname, BaseEntityTwo::getId).list();

        List<DictLongListVO> dictListVOList =
            sysUserDOList.stream().map(it -> new DictLongListVO(it.getNickname(), it.getId()))
                .collect(Collectors.toList());

        // ?????? admin??????
        if (dto.isAddAdminFlag()) {
            dictListVOList
                .add(new DictLongListVO(BaseConfiguration.adminProperties.getAdminNickname(), BaseConstant.ADMIN_ID));
        }

        return dictListVOList;
    }

    /**
     * ??????/??????
     */
    @Override
    @Transactional
    public String insertOrUpdate(SysUserInsertOrUpdateDTO dto) {

        boolean passwordFlag = StrUtil.isNotBlank(dto.getPassword()) && StrUtil.isNotBlank(dto.getOrigPassword());

        if (dto.getId() == null && passwordFlag) {
            // ?????????????????? ???
            String paramValue = SysParamUtil.getValueById(BaseConstant.RSA_PRIVATE_KEY_ID); // ??????????????? ??????
            dto.setOrigPassword(MyRsaUtil.rsaDecrypt(dto.getOrigPassword(), paramValue)); // ??????????????????
            dto.setPassword(MyRsaUtil.rsaDecrypt(dto.getPassword(), paramValue)); // ??????????????????
            // ?????????????????? ???

            if (!ReUtil.isMatch(BaseRegexConstant.PASSWORD_REGEXP, dto.getOrigPassword())) {
                ApiResultVO.error(BizCodeEnum.PASSWORD_RESTRICTIONS); // ???????????????????????????
            }
        }

        RLock lock1 =
            redissonClient.getLock(BaseRedisConstant.PRE_REDISSON + BaseRedisConstant.PRE_LOCK_EMAIL_CODE + dto.getEmail());
        RLock lock2 = redissonClient.getLock(BaseRedisConstant.PRE_REDISSON + BaseRedisConstant.PRE_REDIS_ROLE_REF_USER_CACHE);

        RLock multiLock = redissonClient.getMultiLock(lock1, lock2);
        multiLock.lock();

        try {

            // ????????????????????????
            boolean exist = lambdaQuery().eq(SysUserDO::getEmail, dto.getEmail()).eq(SysUserDO::getDelFlag, false)
                .ne(dto.getId() != null, BaseEntityTwo::getId, dto.getId()).exists();
            if (exist) {
                ApiResultVO.error(BizCodeEnum.EMAIL_HAS_BEEN_REGISTERED);
            }

            if (dto.getId() != null) {
                deleteByIdSetSub(Collections.singleton(dto.getId())); // ????????? ????????????
            }

            SysUserDO sysUserDO = new SysUserDO();
            sysUserDO.setId(dto.getId());
            sysUserDO.setNickname(MyEntityUtil.getNotNullStr(dto.getNickname(), UserUtil.getRandomNickname()));
            sysUserDO.setBio(MyEntityUtil.getNotNullStr(dto.getBio()));
            sysUserDO.setAvatarUrl(MyEntityUtil.getNotNullStr(dto.getAvatarUrl()));
            if (dto.getId() == null) { // ??????????????????
                sysUserDO.setUuid(IdUtil.simpleUUID());
                sysUserDO.setJwtSecretSuf(IdUtil.simpleUUID());
                if (passwordFlag) {
                    sysUserDO.setPassword(PasswordConvertUtil.convert(dto.getPassword(), true));
                } else {
                    sysUserDO.setPassword(""); // ??????????????????
                }
            }
            sysUserDO.setEmail(dto.getEmail());
            sysUserDO.setEnableFlag(dto.isEnableFlag());

            saveOrUpdate(sysUserDO);

            // ?????????????????????
            insertOrUpdateSub(sysUserDO.getId(), dto);

            UserUtil.updateRoleRefUserForRedis(false); // ?????????redis????????????

            if (dto.getId() == null) {
                MyJwtUtil.setUserIdJwtSecretSufForRedis(sysUserDO.getId(), sysUserDO.getJwtSecretSuf()); // ?????????redis????????????
            }

            return BaseBizCodeEnum.API_RESULT_OK.getMsg();
        } finally {
            multiLock.unlock();
        }
    }

    /**
     * ??????????????????
     */
    private void deleteByIdSetSub(Set<Long> idSet) {

        // ????????????????????????????????????
        sysRoleRefUserService.lambdaUpdate().in(SysRoleRefUserDO::getUserId, idSet).remove();

        // ????????????????????????????????????
        sysJobRefUserService.lambdaUpdate().in(SysJobRefUserDO::getUserId, idSet).remove();

        // ????????????????????????????????????
        sysDeptRefUserService.lambdaUpdate().in(SysDeptRefUserDO::getUserId, idSet).remove();

    }

    /**
     * ??????/??????????????????????????????
     */
    private void insertOrUpdateSub(Long userId, SysUserInsertOrUpdateDTO dto) {

        // ???????????????????????????????????????
        if (CollUtil.isNotEmpty(dto.getRoleIdSet())) {
            List<SysRoleRefUserDO> insertList = new ArrayList<>();
            for (Long item : dto.getRoleIdSet()) {
                SysRoleRefUserDO sysRoleRefUserDO = new SysRoleRefUserDO();
                sysRoleRefUserDO.setRoleId(item);
                sysRoleRefUserDO.setUserId(userId);
                insertList.add(sysRoleRefUserDO);
            }
            sysRoleRefUserService.saveBatch(insertList);
        }

        // ???????????????????????????????????????
        if (CollUtil.isNotEmpty(dto.getJobIdSet())) {
            List<SysJobRefUserDO> insertList = new ArrayList<>();
            for (Long item : dto.getJobIdSet()) {
                SysJobRefUserDO sysJobRefUserDO = new SysJobRefUserDO();
                sysJobRefUserDO.setJobId(item);
                sysJobRefUserDO.setUserId(userId);
                insertList.add(sysJobRefUserDO);
            }
            sysJobRefUserService.saveBatch(insertList);
        }

        // ???????????????????????????????????????
        if (CollUtil.isNotEmpty(dto.getDeptIdSet())) {
            List<SysDeptRefUserDO> insertList = new ArrayList<>();
            for (Long item : dto.getDeptIdSet()) {
                SysDeptRefUserDO sysDeptRefUserDO = new SysDeptRefUserDO();
                sysDeptRefUserDO.setDeptId(item);
                sysDeptRefUserDO.setUserId(userId);
                insertList.add(sysDeptRefUserDO);
            }
            sysDeptRefUserService.saveBatch(insertList);
        }

    }

    /**
     * ??????????????????
     */
    @Override
    @Transactional
    public String deleteByIdSet(NotEmptyIdSet notEmptyIdSet) {

        // ????????????
        List<SysFileDO> sysFileDOList =
            sysFileService.lambdaQuery().in(BaseEntityTwo::getCreateId, notEmptyIdSet.getIdSet())
                .select(SysFileDO::getUrl).list();
        if (sysFileDOList.size() != 0) {
            SysFileRemoveDTO sysFileRemoveDTO = new SysFileRemoveDTO();
            // ????????????????????????????????? set
            Set<String> urlSet = sysFileDOList.stream().map(SysFileDO::getUrl).collect(Collectors.toSet());
            sysFileRemoveDTO.setUrlSet(urlSet);
            sysFileService.remove(sysFileRemoveDTO, false);
        }

        sysWebSocketService.offlineByUserIdSet(notEmptyIdSet.getIdSet());

        Set<String> userIdStrSet = notEmptyIdSet.getIdSet().stream().map(Object::toString).collect(Collectors.toSet());

        RLock lock1 = redissonClient.getLock(BaseRedisConstant.PRE_REDISSON + BaseRedisConstant.PRE_REDIS_ROLE_REF_USER_CACHE);
        RLock multiLock =
            MultiLockUtil.getMultiLock(BaseRedisConstant.PRE_REDIS_USER_ID_JWT_SECRET_SUF_CACHE + ":", userIdStrSet, lock1);
        multiLock.lock();

        try {
            deleteByIdSetSub(notEmptyIdSet.getIdSet()); // ?????????????????????

            // ???????????????????????????
            lambdaUpdate().in(BaseEntityTwo::getId, notEmptyIdSet.getIdSet()).eq(SysUserDO::getDelFlag, false)
                .set(SysUserDO::getDelFlag, true).update();

            UserUtil.updateRoleRefUserForRedis(false); // ?????????redis????????????
            MyJwtUtil.deleteUserIdJwtSecretSufForRedis(userIdStrSet); // ?????????redis????????????

            // ????????? ??????????????????????????????????????????
            KafkaUtil.delAccount(notEmptyIdSet.getIdSet());

            return BaseBizCodeEnum.API_RESULT_OK.getMsg();
        } finally {
            multiLock.unlock();
        }
    }

    /**
     * ????????????id???????????????
     */
    @Override
    public SysUserInfoByIdVO infoById(NotNullId notNullId) {

        SysUserInfoByIdVO sysUserInfoByIdVO =
            BeanUtil.copyProperties(getById(notNullId.getId()), SysUserInfoByIdVO.class);

        if (sysUserInfoByIdVO == null) {
            return null;
        }

        // ?????????????????????????????? idSet
        List<SysRoleRefUserDO> refUserDOList =
            sysRoleRefUserService.lambdaQuery().eq(SysRoleRefUserDO::getUserId, notNullId.getId())
                .select(SysRoleRefUserDO::getRoleId).list();
        Set<Long> roleIdSet = refUserDOList.stream().map(SysRoleRefUserDO::getRoleId).collect(Collectors.toSet());

        // ?????????????????????????????? idSet
        List<SysJobRefUserDO> jobRefUserDOList =
            sysJobRefUserService.lambdaQuery().eq(SysJobRefUserDO::getUserId, notNullId.getId())
                .select(SysJobRefUserDO::getJobId).list();
        Set<Long> jobIdSet = jobRefUserDOList.stream().map(SysJobRefUserDO::getJobId).collect(Collectors.toSet());

        // ?????????????????????????????? idSet
        List<SysDeptRefUserDO> deptRefUserDOList =
            sysDeptRefUserService.lambdaQuery().eq(SysDeptRefUserDO::getUserId, notNullId.getId())
                .select(SysDeptRefUserDO::getDeptId).list();
        Set<Long> deptIdSet = deptRefUserDOList.stream().map(SysDeptRefUserDO::getDeptId).collect(Collectors.toSet());

        sysUserInfoByIdVO.setRoleIdSet(roleIdSet);
        sysUserInfoByIdVO.setJobIdSet(jobIdSet);
        sysUserInfoByIdVO.setDeptIdSet(deptIdSet);
        sysUserInfoByIdVO.setPassword(null); // ??????????????? null

        return sysUserInfoByIdVO;
    }

    /**
     * ???????????? jwt????????????
     */
    @Override
    @Transactional
    public String refreshJwtSecretSuf(NotEmptyIdSet notEmptyIdSet) {

        List<SysUserDO> updateList = new ArrayList<>();

        for (Long item : notEmptyIdSet.getIdSet()) {
            SysUserDO sysUserDO = new SysUserDO();
            sysUserDO.setId(item);
            sysUserDO.setJwtSecretSuf(IdUtil.simpleUUID());
            updateList.add(sysUserDO);
        }

        RLock multiLock = MultiLockUtil
            .getMultiLockForLong(BaseRedisConstant.PRE_REDIS_USER_ID_JWT_SECRET_SUF_CACHE + ":", notEmptyIdSet.getIdSet());
        multiLock.lock();

        try {
            updateBatchById(updateList);

            MyJwtUtil.updateUserIdJwtSecretSufForRedis(notEmptyIdSet.getIdSet()); // ?????????redis????????????

            return BaseBizCodeEnum.API_RESULT_OK.getMsg();
        } finally {
            multiLock.unlock();
        }
    }

    /**
     * ??????????????????
     */
    @Override
    @Transactional
    public String resetAvatar(NotEmptyIdSet notEmptyIdSet) {

        lambdaUpdate().in(BaseEntityTwo::getId, notEmptyIdSet.getIdSet()).set(SysUserDO::getAvatarUrl, "").update();

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * ??????????????????
     */
    @Override
    @Transactional
    public String updatePassword(SysUserUpdatePasswordDTO dto) {

        boolean passwordFlag = StrUtil.isNotBlank(dto.getNewPassword()) && StrUtil.isNotBlank(dto.getNewOrigPassword());

        if (passwordFlag) {
            // ?????????????????? ???
            String paramValue = SysParamUtil.getValueById(BaseConstant.RSA_PRIVATE_KEY_ID); // ??????????????? ??????
            dto.setNewOrigPassword(MyRsaUtil.rsaDecrypt(dto.getNewOrigPassword(), paramValue)); // ??????????????????
            dto.setNewPassword(MyRsaUtil.rsaDecrypt(dto.getNewPassword(), paramValue)); // ??????????????????
            // ?????????????????? ???

            if (!ReUtil.isMatch(BaseRegexConstant.PASSWORD_REGEXP, dto.getNewOrigPassword())) {
                ApiResultVO.error(BizCodeEnum.PASSWORD_RESTRICTIONS); // ???????????????????????????
            }

            lambdaUpdate().in(BaseEntityTwo::getId, dto.getIdSet())
                .set(SysUserDO::getPassword, PasswordConvertUtil.convert(dto.getNewPassword(), true)).update();
        } else {

            lambdaUpdate().in(BaseEntityTwo::getId, dto.getIdSet()).set(SysUserDO::getPassword, "").update();
        }

        refreshJwtSecretSuf(new NotEmptyIdSet(dto.getIdSet())); // ?????????jwt????????????

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

}
