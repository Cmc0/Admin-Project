package com.admin.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.admin.common.configuration.BaseConfiguration;
import com.admin.common.configuration.JsonRedisTemplate;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.constant.BaseRegexConstant;
import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullId;
import com.admin.common.model.entity.BaseEntityTwo;
import com.admin.common.model.entity.SysRoleRefUserDO;
import com.admin.common.model.entity.SysUserDO;
import com.admin.common.model.vo.ApiResultVO;
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
import com.admin.user.model.dto.SysUserInsertOrUpdateDTO;
import com.admin.user.model.dto.SysUserPageDTO;
import com.admin.user.model.dto.SysUserUpdatePasswordDTO;
import com.admin.user.model.vo.SysUserBaseInfoVO;
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
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserProMapper, SysUserDO> implements SysUserService {

    @Resource
    HttpServletRequest httpServletRequest;
    @Resource
    JsonRedisTemplate<String> jsonRedisTemplate;
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
     * 退出登录
     */
    @Override
    public String logout() {

        // 清除 redis中的 jwtHash
        String jwtHash = MyJwtUtil.generateRedisJwtHash(httpServletRequest.getHeader(BaseConstant.JWT_HEADER_KEY),
            UserUtil.getCurrentUserId(), RequestUtil.getSysRequestCategoryEnum(httpServletRequest));

        jsonRedisTemplate.delete(jwtHash);

        return "登出成功";
    }

    /**
     * 用户基本信息
     */
    @Override
    public SysUserBaseInfoVO baseInfo() {

        Long userId = UserUtil.getCurrentUserId();

        SysUserBaseInfoVO sysUserBaseInfoVO = new SysUserBaseInfoVO();

        if (BaseConstant.ADMIN_ID.equals(userId)) {
            sysUserBaseInfoVO.setAvatarUrl("");
            sysUserBaseInfoVO.setNickname(BaseConfiguration.adminProperties.getAdminNickname());
            sysUserBaseInfoVO.setBio("");
            sysUserBaseInfoVO.setEmail("");
            sysUserBaseInfoVO.setPasswordFlag(true);
            return sysUserBaseInfoVO;
        }

        SysUserDO sysUserDO = lambdaQuery().eq(BaseEntityTwo::getId, userId)
            .select(SysUserDO::getAvatarUrl, SysUserDO::getNickname, SysUserDO::getEmail, SysUserDO::getBio,
                SysUserDO::getEmail, SysUserDO::getPassword).one();

        if (sysUserDO != null) {
            sysUserBaseInfoVO.setAvatarUrl(sysUserDO.getAvatarUrl());
            sysUserBaseInfoVO.setNickname(sysUserDO.getNickname());
            sysUserBaseInfoVO.setBio(sysUserDO.getBio());
            sysUserBaseInfoVO.setEmail(DesensitizedUtil.email(sysUserDO.getEmail())); // 脱敏
            sysUserBaseInfoVO.setPasswordFlag(StrUtil.isNotBlank(sysUserDO.getPassword()));
        }

        return sysUserBaseInfoVO;
    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysUserPageVO> myPage(SysUserPageDTO dto) {

        Page<SysUserPageVO> page = baseMapper.myPage(dto.getCreateTimeDescDefaultOrderPage(), dto);

        for (SysUserPageVO item : page.getRecords()) {
            item.setEmail(DesensitizedUtil.email(item.getEmail())); // 脱敏
        }

        // 增加 admin账号
        if (dto.isAddAdminFlag() && dto.getPageSize() == -1) {
            SysUserPageVO sysUserPageVO = new SysUserPageVO();
            sysUserPageVO.setId(BaseConstant.ADMIN_ID);
            sysUserPageVO.setNickname(BaseConfiguration.adminProperties.getAdminNickname());
            page.getRecords().add(sysUserPageVO);
            page.setTotal(page.getTotal() + 1); // total + 1
        }

        return page;
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

        RLock lock =
            redissonClient.getLock(BaseConstant.PRE_REDISSON + BaseConstant.PRE_LOCK_EMAIL_CODE + dto.getEmail());
        lock.lock();

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
            sysUserDO.setUuid(IdUtil.simpleUUID());
            sysUserDO.setJwtSecretSuf(IdUtil.simpleUUID());
            sysUserDO.setNickname(MyEntityUtil.getNotNullStr(dto.getNickname(), UserUtil.getDefaultNickname()));
            sysUserDO.setBio(MyEntityUtil.getNotNullStr(dto.getBio()));
            sysUserDO.setAvatarUrl(MyEntityUtil.getNotNullStr(dto.getAvatarUrl()));
            if (dto.getId() == null) { // 新增时：才可以设置密码
                if (passwordFlag) {
                    sysUserDO.setPassword(PasswordConvertUtil.convert(dto.getPassword(), true));
                } else {
                    sysUserDO.setPassword(""); // 密码可以为空
                }
            }
            sysUserDO.setEmail(dto.getEmail());

            saveOrUpdate(sysUserDO);

            // 新增数据到子表
            insertOrUpdateSub(sysUserDO.getId(), dto);

            return BaseBizCodeEnum.API_RESULT_OK.getMsg();
        } finally {
            lock.unlock();
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

        deleteByIdSetSub(notEmptyIdSet.getIdSet()); // 删除关联表数据

        // 注销用户：逻辑删除
        lambdaUpdate().in(BaseEntityTwo::getId, notEmptyIdSet.getIdSet()).eq(SysUserDO::getDelFlag, false)
            .set(SysUserDO::getDelFlag, true).update();

        sysWebSocketService.offlineByUserIdSet(notEmptyIdSet.getIdSet());

        // 并且给 消息中间件推送，进行下线操作
        KafkaUtil.delAccount(notEmptyIdSet.getIdSet());

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
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
            sysUserDO.setJwtSecretSuf(IdUtil.fastUUID());
            updateList.add(sysUserDO);
        }

        updateBatchById(updateList);

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
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

        refreshJwtSecretSuf(new NotEmptyIdSet(dto.getIdSet())); // 刷新：jwt私钥后缀

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

}
