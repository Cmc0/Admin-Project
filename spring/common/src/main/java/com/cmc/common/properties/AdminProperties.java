package com.cmc.common.properties;

import com.cmc.common.model.constant.BaseConstant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = BaseConstant.ADMIN)
public class AdminProperties {

    @ApiModelProperty(value = "jwt 密钥前缀")
    private String jwtSecretPre =
        "203e5c4e94c50b8e96cc6c8c3471309c11a39e1996dd5abab180ba9a0ddcefe99133edeff516e1d2d264f8dde85eaf6acefea2268826fda31080d00f64b47ad0111";

    @ApiModelProperty(value = "是否允许 admin登录，为 false时 admin下的所有配置都不会生效")
    private boolean adminEnable;

    @ApiModelProperty(value = "admin的用户名")
    private String adminUsername = "admin";

    @ApiModelProperty(value = "admin的密码，默认为 suancai，下面是 suancai经过 sha256加密之后的字符串，加密次数和前端需进行统一，输入 suancai 即可登录")
    private String adminPassword = "0c57d37d81577a6a026979ca2e545ebb0aefee59a824fb28c24a14ec9f5a5a53";

    @ApiModelProperty(value = "返回socket 连接地址时，使用的 ip/域名")
    private String socketAddress = "127.0.0.1";

}
