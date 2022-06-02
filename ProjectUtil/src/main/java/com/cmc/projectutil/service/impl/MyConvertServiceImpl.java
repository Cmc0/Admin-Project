package com.cmc.projectutil.service.impl;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.cmc.projectutil.exception.BaseBizCodeEnum;
import com.cmc.projectutil.model.dto.CodeGenerateItemDTO;
import com.cmc.projectutil.model.dto.NotBlankStrDTO;
import com.cmc.projectutil.model.enums.ColumnTypeRefEnum;
import com.cmc.projectutil.service.CodeGenerateService;
import com.cmc.projectutil.service.MyConvertService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MyConvertServiceImpl implements MyConvertService {

    @Resource
    CodeGenerateService codeGenerateService;

    /**
     * sql转java
     */
    @Override
    public String sqlToJava(NotBlankStrDTO dto) {

        String value = dto.getValue();

        List<String> stringList = StrUtil.split(value, ",");

        StrBuilder strBuilder = StrBuilder.create();

        for (String item : stringList) {

            if (StrUtil.contains(item, " AS ")) {
                String subBefore = StrUtil.subAfter(item, "AS ", false);
                sqlToJavaAppendStrBuilder(strBuilder, subBefore);
                continue;
            }

            if (StrUtil.contains(item, " as ")) {
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

    /**
     * 给sql添加AS
     */
    @Override
    public String sqlAddAs(NotBlankStrDTO dto) {

        String value = dto.getValue();

        List<String> stringList = StrUtil.splitTrim(value, ",");

        StrBuilder strBuilder = StrBuilder.create();

        for (String item : stringList) {

            if (item.contains(" AS ") || item.contains(" as ")) {
                strBuilder.append(item).append(",\n");
                continue;
            }

            String s = item;
            if (item.contains(".")) {
                s = StrUtil.splitTrim(item, ".").get(1);
            }

            String str = item + " AS " + StrUtil.toCamelCase(s) + ",\n";
            strBuilder.append(str);

        }

        return strBuilder.toString();
    }

    /**
     * 通过：表结构sql，生成后台代码
     */
    @Override
    public String forSpringByTableSql(NotBlankStrDTO dto) {

        codeGenerateService.forSpring(getCodeGenerateItemDTOListByTableSql(dto.getValue()));

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 通过：表结构sql，生成前端代码
     */
    @Override
    public String forAntByTableSql(NotBlankStrDTO dto) {

        codeGenerateService.forAnt(getCodeGenerateItemDTOListByTableSql(dto.getValue()));

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 通过：表结构sql，获取 List<CodeGenerateItemDTO>
     */
    private List<CodeGenerateItemDTO> getCodeGenerateItemDTOListByTableSql(String value) {

        List<CodeGenerateItemDTO> codeGenerateItemDTOList = new ArrayList<>();

        String tableName = ReUtil.getGroup1("CREATE TABLE `(.*?)`", value);
        String tableComment = ReUtil.getGroup1("COMMENT='(.*?)';", value);

        List<String> stringList = StrUtil.splitTrim(value, "\n");

        List<String> collectList = stringList.stream().filter(it -> it.contains(",")).collect(Collectors.toList());

        for (String item : collectList) {

            List<String> splitTrimList = StrUtil.splitTrim(item, " ");
            String columnName = ReUtil.getGroup1("`(.*?)`", splitTrimList.get(0));
            String columnType = splitTrimList.get(1);

            String columnComment = ReUtil.getGroup1("COMMENT '(.*?)'", item);

            CodeGenerateItemDTO codeGenerateItemDTO = new CodeGenerateItemDTO();
            codeGenerateItemDTO.setTableName(tableName);
            codeGenerateItemDTO.setTableComment(tableComment);
            codeGenerateItemDTO.setColumnName(columnName);
            codeGenerateItemDTO.setColumnType(columnType);
            codeGenerateItemDTO.setColumnComment(columnComment);

            codeGenerateItemDTOList.add(codeGenerateItemDTO);

        }

        return codeGenerateItemDTOList;
    }

}
