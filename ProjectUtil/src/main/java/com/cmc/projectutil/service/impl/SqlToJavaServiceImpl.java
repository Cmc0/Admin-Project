package com.cmc.projectutil.service.impl;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import com.cmc.projectutil.model.dto.NotBlankStrDTO;
import com.cmc.projectutil.service.SqlToJavaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SqlToJavaServiceImpl implements SqlToJavaService {

    /**
     * sql转java
     */
    @Override
    public String sqlToJava(NotBlankStrDTO dto) {

        String value = dto.getValue();

        List<String> stringList = StrUtil.split(value, ",");

        StrBuilder strBuilder = StrBuilder.create();

        for (String item : stringList) {

            if (StrUtil.contains(item, "AS ")) {
                String subBefore = StrUtil.subAfter(item, "AS ", false);
                appendStrBuilder(strBuilder, subBefore);
                continue;
            }

            if (StrUtil.contains(item, "as ")) {
                String subBefore = StrUtil.subAfter(item, "as ", false);
                appendStrBuilder(strBuilder, subBefore);
                continue;
            }

            String subBefore = StrUtil.subAfter(item, ".", false);
            subBefore = StrUtil.toCamelCase(subBefore);
            appendStrBuilder(strBuilder, subBefore);

        }

        return strBuilder.toString();
    }

    private void appendStrBuilder(StrBuilder strBuilder, String str) {

        if (StrUtil.isBlank(str)) {
            return;
        }

        String format = StrUtil.format("@ApiModelProperty(value = \"xxx\")\n" + "private String {};\n\n", str);

        strBuilder.append(format);

    }

}
