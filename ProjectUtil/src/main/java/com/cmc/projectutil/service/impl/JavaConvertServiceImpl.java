package com.cmc.projectutil.service.impl;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.cmc.projectutil.model.dto.NotBlankStrDTO;
import com.cmc.projectutil.model.enums.ColumnTypeRefEnum;
import com.cmc.projectutil.service.JavaConvertService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JavaConvertServiceImpl implements JavaConvertService {

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
                sqlToJavaAppendStrBuilder(strBuilder, subBefore);
                continue;
            }

            if (StrUtil.contains(item, "as ")) {
                String subBefore = StrUtil.subAfter(item, "as ", false);
                sqlToJavaAppendStrBuilder(strBuilder, subBefore);
                continue;
            }

            String subBefore = StrUtil.subAfter(item, ".", false);
            subBefore = StrUtil.toCamelCase(subBefore);
            sqlToJavaAppendStrBuilder(strBuilder, subBefore);

        }

        return strBuilder.toString();
    }

    private void sqlToJavaAppendStrBuilder(StrBuilder strBuilder, String str) {

        if (StrUtil.isBlank(str)) {
            return;
        }

        String format = StrUtil.format("@ApiModelProperty(value = \"xxx\")\n" + "private String {};\n\n", str);

        strBuilder.append(format);

    }

    /**
     * java转ts
     */
    @Override
    public String javaToTs(NotBlankStrDTO dto) {

        String value = dto.getValue();

        List<String> stringList = StrUtil.splitTrim(value, ";");

        StrBuilder strBuilder = StrBuilder.create();

        for (String item : stringList) {

            List<String> splitTrimList = StrUtil.splitTrim(item, "\n");

            if (splitTrimList.size() == 1) {
                javaToTsAppendStrBuilder(strBuilder, splitTrimList, null);
                continue;
            }

            String group1 = ReUtil.getGroup1("@ApiModelProperty\\(value = \"(.*?)\"\\)", item);

            javaToTsAppendStrBuilder(strBuilder, splitTrimList, group1);

        }

        return strBuilder.toString();
    }

    private void javaToTsAppendStrBuilder(StrBuilder strBuilder, List<String> splitTrimList,
        String apiModelPropertyValue) {

        String str = splitTrimList.get(splitTrimList.size() - 1); // private String tableName

        List<String> strList = StrUtil.splitTrim(str, " ");

        if (strList.size() != 3) {
            return;
        }

        if (apiModelPropertyValue == null) {
            apiModelPropertyValue = "";
        }

        ColumnTypeRefEnum columnTypeRefEnum = ColumnTypeRefEnum.getByJavaType(strList.get(1));

        String tsType;
        if (columnTypeRefEnum == null) {
            tsType = "string[]";
        } else {
            tsType = columnTypeRefEnum.getTsType();
        }

        String format = StrUtil.format("{}?: {} // {}\n", strList.get(2), tsType, apiModelPropertyValue);

        strBuilder.append(format);
    }

}
