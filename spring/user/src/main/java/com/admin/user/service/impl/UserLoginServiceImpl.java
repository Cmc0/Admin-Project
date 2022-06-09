package com.admin.user.service.impl;

import cn.hutool.core.util.ReUtil;
import com.admin.common.configuration.BaseConfiguration;
import com.admin.common.model.constant.BaseRegexConstant;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.common.util.MyRsaDecryptUtil;
import com.admin.user.exception.BizCodeEnum;
import com.admin.user.model.dto.UserLoginPasswordDTO;
import com.admin.user.service.UserLoginService;
import org.springframework.stereotype.Service;

@Service
public class UserLoginServiceImpl implements UserLoginService {

    /**
     * 账号密码登录
     */
    @Override
    public String password(UserLoginPasswordDTO dto) {

        // 密码，非对称，解密
        dto.setPassword(MyRsaDecryptUtil.rsaDecrypt(dto.getPassword()));

        if (BaseConfiguration.adminProperties.getAdminAccount().equals(dto.getAccount())
            && BaseConfiguration.adminProperties.isAdminEnable()) {
            return passwordForAdmin(dto); // 如果是 admin账户，并且配置文件中允许 admin登录
        }

        boolean isEmail = ReUtil.isMatch(BaseRegexConstant.EMAIL, dto.getAccount());
        if (isEmail) {
            return loginByAccount(dto);
        }

        ApiResultVO.error(BizCodeEnum.ACCOUNT_NUMBER_AND_PASSWORD_NOT_VALID);
        return null; // 这里不会执行，只是为了语法检测
    }

    /**
     * admin 账号密码登录
     */
    private String passwordForAdmin(UserLoginPasswordDTO dto) {

        return "jwt";
    }

    /**
     * 账号密码登录
     */
    private String loginByAccount(UserLoginPasswordDTO dto) {

        return "jwt";
    }

}
