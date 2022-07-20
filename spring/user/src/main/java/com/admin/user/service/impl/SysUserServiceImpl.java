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
     * 分页排序查询
     */
    @Override
    public Page<SysUserPageVO> myPage(SysUserPageDTO dto) {

        Page<SysUserPageVO> page = baseMapper.myPage(dto.getCreateTimeDescDefaultOrderPage(), dto);

        Set<Long> userIdSet = new HashSet<>();

        for (SysUserPageVO item : page.getRecords()) {
            item.setEmail(DesensitizedUtil.email(item.getEmail())); // 脱敏
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
     * 下拉列表
     */
    @Override
    public List<DictLongListVO> dictList(SysUserDictListDTO dto) {

        List<SysUserDO> sysUserDOList = lambdaQuery().select(SysUserDO::getNickname, BaseEntityTwo::getId).list();

        List<DictLongListVO> dictListVOList =
            sysUserDOList.stream().map(it -> new DictLongListVO(it.getNickname(), it.getId()))
                .collect(Collectors.toList());

        // 增加 admin账号
        if (dto.isAddAdminFlag()) {
            dictListVOList
                .add(new DictLongListVO(BaseConfiguration.adminProperties.getAdminNickname(), BaseConstant.ADMIN_ID));
        }

        return dictListVOList;
    }

    /**
     * 新增/修改
     */
    @Override
    @Transactional
    public String insertOrUpdate(SysUserInsertOrUpdateDTO dto) {

        boolean passwordFlag = StrUtil.isNotBlank(dto.getPassword()) && StrUtil.isNotBlank(dto.getOrigPassword());

        if (dto.getId() == null && passwordFlag) {
            // 非对称：解密 ↓
            String paramValue = SysParamUtil.getValueById(BaseConstant.RSA_PRIVATE_KEY_ID); // 获取非对称 私钥
            dto.setOrigPassword(MyRsaUtil.rsaDecrypt(dto.getOrigPassword(), paramValue)); // 非对称：解密
            dto.setPassword(MyRsaUtil.rsaDecrypt(dto.getPassword(), paramValue)); // 非对称：解密
            // 非对称：解密 ↑

            if (!ReUtil.isMatch(BaseRegexConstant.PASSWORD_REGEXP, dto.getOrigPassword())) {
                ApiResultVO.error(BizCodeEnum.PASSWORD_RESTRICTIONS); // 不合法直接抛出异常
            }
        }

        RLock lock1 =
            redissonClient.getLock(BaseRedisConstant.PRE_REDISSON + BaseRedisConstant.PRE_LOCK_EMAIL_CODE + dto.getEmail());
        RLock lock2 = redissonClient.getLock(BaseRedisConstant.PRE_REDISSON + BaseRedisConstant.PRE_REDIS_ROLE_REF_USER_CACHE);

        RLock multiLock = redissonClient.getMultiLock(lock1, lock2);
        multiLock.lock();

        try {

            // 判断邮箱是否存在
            boolean exist = lambdaQuery().eq(SysUserDO::getEmail, dto.getEmail()).eq(SysUserDO::getDelFlag, false)
                .ne(dto.getId() != null, BaseEntityTwo::getId, dto.getId()).exists();
            if (exist) {
                ApiResultVO.error(BizCodeEnum.EMAIL_HAS_BEEN_REGISTERED);
            }

            if (dto.getId() != null) {
                deleteByIdSetSub(Collections.singleton(dto.getId())); // 先删除 子表数据
            }

            SysUserDO sysUserDO = new SysUserDO();
            sysUserDO.setId(dto.getId());
            sysUserDO.setNickname(MyEntityUtil.getNotNullStr(dto.getNickname(), UserUtil.getDefaultNickname()));
            sysUserDO.setBio(MyEntityUtil.getNotNullStr(dto.getBio()));
            sysUserDO.setAvatarUrl(MyEntityUtil.getNotNullStr(dto.getAvatarUrl()));
            if (dto.getId() == null) { // 如果是：新增
                sysUserDO.setUuid(IdUtil.simpleUUID());
                sysUserDO.setJwtSecretSuf(IdUtil.simpleUUID());
                if (passwordFlag) {
                    sysUserDO.setPassword(PasswordConvertUtil.convert(dto.getPassword(), true));
                } else {
                    sysUserDO.setPassword(""); // 密码可以为空
                }
            }
            sysUserDO.setEmail(dto.getEmail());
            sysUserDO.setEnableFlag(dto.isEnableFlag());

            saveOrUpdate(sysUserDO);

            // 新增数据到子表
            insertOrUpdateSub(sysUserDO.getId(), dto);

            UserUtil.updateRoleRefUserForRedis(false); // 更新：redis中的缓存

            if (dto.getId() == null) {
                MyJwtUtil.setUserIdJwtSecretSufForRedis(sysUserDO.getId(), sysUserDO.getJwtSecretSuf()); // 设置：redis中的缓存
            }

            return BaseBizCodeEnum.API_RESULT_OK.getMsg();
        } finally {
            multiLock.unlock();
        }
    }

    /**
     * 删除子表数据
     */
    private void deleteByIdSetSub(Set<Long> idSet) {

        // 删除：角色用户关联表数据
        sysRoleRefUserService.lambdaUpdate().in(SysRoleRefUserDO::getUserId, idSet).remove();

        // 删除：岗位用户关联表数据
        sysJobRefUserService.lambdaUpdate().in(SysJobRefUserDO::getUserId, idSet).remove();

        // 删除：部门用户关联表数据
        sysDeptRefUserService.lambdaUpdate().in(SysDeptRefUserDO::getUserId, idSet).remove();

    }

    /**
     * 新增/修改：新增数据到子表
     */
    private void insertOrUpdateSub(Long userId, SysUserInsertOrUpdateDTO dto) {

        // 新增数据到：角色用户关联表
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

        // 新增数据到：岗位用户关联表
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

        // 新增数据到：部门用户关联表
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
     * 批量注销用户
     */
    @Override
    @Transactional
    public String deleteByIdSet(NotEmptyIdSet notEmptyIdSet) {

        // 删除文件
        List<SysFileDO> sysFileDOList =
            sysFileService.lambdaQuery().in(BaseEntityTwo::getCreateId, notEmptyIdSet.getIdSet())
                .select(SysFileDO::getUrl).list();
        if (sysFileDOList.size() != 0) {
            SysFileRemoveDTO sysFileRemoveDTO = new SysFileRemoveDTO();
            // 文件路径（包含文件名） set
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
            deleteByIdSetSub(notEmptyIdSet.getIdSet()); // 删除关联表数据

            // 注销用户：逻辑删除
            lambdaUpdate().in(BaseEntityTwo::getId, notEmptyIdSet.getIdSet()).eq(SysUserDO::getDelFlag, false)
                .set(SysUserDO::getDelFlag, true).update();

            UserUtil.updateRoleRefUserForRedis(false); // 更新：redis中的缓存
            MyJwtUtil.deleteUserIdJwtSecretSufForRedis(userIdStrSet); // 更新：redis中的缓存

            // 并且给 消息中间件推送，进行下线操作
            KafkaUtil.delAccount(notEmptyIdSet.getIdSet());

            return BaseBizCodeEnum.API_RESULT_OK.getMsg();
        } finally {
            multiLock.unlock();
        }
    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysUserInfoByIdVO infoById(NotNullId notNullId) {

        SysUserInfoByIdVO sysUserInfoByIdVO =
            BeanUtil.copyProperties(getById(notNullId.getId()), SysUserInfoByIdVO.class);

        if (sysUserInfoByIdVO == null) {
            return null;
        }

        // 获取：用户绑定的角色 idSet
        List<SysRoleRefUserDO> refUserDOList =
            sysRoleRefUserService.lambdaQuery().eq(SysRoleRefUserDO::getUserId, notNullId.getId())
                .select(SysRoleRefUserDO::getRoleId).list();
        Set<Long> roleIdSet = refUserDOList.stream().map(SysRoleRefUserDO::getRoleId).collect(Collectors.toSet());

        // 获取：用户绑定的岗位 idSet
        List<SysJobRefUserDO> jobRefUserDOList =
            sysJobRefUserService.lambdaQuery().eq(SysJobRefUserDO::getUserId, notNullId.getId())
                .select(SysJobRefUserDO::getJobId).list();
        Set<Long> jobIdSet = jobRefUserDOList.stream().map(SysJobRefUserDO::getJobId).collect(Collectors.toSet());

        // 获取：用户绑定的部门 idSet
        List<SysDeptRefUserDO> deptRefUserDOList =
            sysDeptRefUserService.lambdaQuery().eq(SysDeptRefUserDO::getUserId, notNullId.getId())
                .select(SysDeptRefUserDO::getDeptId).list();
        Set<Long> deptIdSet = deptRefUserDOList.stream().map(SysDeptRefUserDO::getDeptId).collect(Collectors.toSet());

        sysUserInfoByIdVO.setRoleIdSet(roleIdSet);
        sysUserInfoByIdVO.setJobIdSet(jobIdSet);
        sysUserInfoByIdVO.setDeptIdSet(deptIdSet);
        sysUserInfoByIdVO.setPassword(null); // 密码设置为 null

        return sysUserInfoByIdVO;
    }

    /**
     * 刷新用户 jwt私钥后缀
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

            MyJwtUtil.updateUserIdJwtSecretSufForRedis(notEmptyIdSet.getIdSet()); // 更新：redis中的缓存

            return BaseBizCodeEnum.API_RESULT_OK.getMsg();
        } finally {
            multiLock.unlock();
        }
    }

    /**
     * 批量重置头像
     */
    @Override
    @Transactional
    public String resetAvatar(NotEmptyIdSet notEmptyIdSet) {

        lambdaUpdate().in(BaseEntityTwo::getId, notEmptyIdSet.getIdSet()).set(SysUserDO::getAvatarUrl, "").update();

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 批量修改密码
     */
    @Override
    @Transactional
    public String updatePassword(SysUserUpdatePasswordDTO dto) {

        boolean passwordFlag = StrUtil.isNotBlank(dto.getNewPassword()) && StrUtil.isNotBlank(dto.getNewOrigPassword());

        if (passwordFlag) {
            // 非对称：解密 ↓
            String paramValue = SysParamUtil.getValueById(BaseConstant.RSA_PRIVATE_KEY_ID); // 获取非对称 私钥
            dto.setNewOrigPassword(MyRsaUtil.rsaDecrypt(dto.getNewOrigPassword(), paramValue)); // 非对称：解密
            dto.setNewPassword(MyRsaUtil.rsaDecrypt(dto.getNewPassword(), paramValue)); // 非对称：解密
            // 非对称：解密 ↑

            if (!ReUtil.isMatch(BaseRegexConstant.PASSWORD_REGEXP, dto.getNewOrigPassword())) {
                ApiResultVO.error(BizCodeEnum.PASSWORD_RESTRICTIONS); // 不合法直接抛出异常
            }

            lambdaUpdate().in(BaseEntityTwo::getId, dto.getIdSet())
                .set(SysUserDO::getPassword, PasswordConvertUtil.convert(dto.getNewPassword(), true)).update();
        } else {

            lambdaUpdate().in(BaseEntityTwo::getId, dto.getIdSet()).set(SysUserDO::getPassword, "").update();
        }

        refreshJwtSecretSuf(new NotEmptyIdSet(dto.getIdSet())); // 刷新：jwt私钥后缀

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

}
