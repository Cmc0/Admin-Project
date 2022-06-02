package com.cmc.projectutil.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.cmc.projectutil.model.dto.CodeGenerateItemDTO;
import com.cmc.projectutil.model.entity.BaseEntity;
import com.cmc.projectutil.model.entity.BaseEntityFour;
import com.cmc.projectutil.model.entity.BaseEntityThree;
import com.cmc.projectutil.model.entity.BaseEntityTwo;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class CodeGenerateHelperUtil {

    public static final Map<String, Set<String>> BASE_ENTITY_MAP = new LinkedHashMap<>();

    static {
        putBaseEntityMap(BaseEntityFour.class);
        putBaseEntityMap(BaseEntityThree.class);
        putBaseEntityMap(BaseEntityTwo.class);
        putBaseEntityMap(BaseEntity.class);
    }

    private static void putBaseEntityMap(Class<?> beanClass) {

        Field[] fieldArr = ReflectUtil.getFields(beanClass);

        ArrayList<Field> fieldsList = CollUtil.newArrayList(fieldArr);

        Set<String> fieldNameSet = fieldsList.stream()
            // 过滤掉：不存在于数据库的字段
            .filter(it -> it.getAnnotation(TableField.class) == null || it.getAnnotation(TableField.class).exist())
            .map(Field::getName).collect(Collectors.toSet());

        BASE_ENTITY_MAP.put(beanClass.getSimpleName(), fieldNameSet);

    }

    /**
     * 匹配父类名，如果没有匹配上，则返回 null
     */
    public static String getSupperClassName(List<CodeGenerateItemDTO> columnList) {

        Set<String> columnNameCamelCaseSet =
            columnList.stream().map(CodeGenerateItemDTO::getColumnNameCamelCase).collect(Collectors.toSet());

        for (Map.Entry<String, Set<String>> item : BASE_ENTITY_MAP.entrySet()) {

            Set<String> supperFieldNameSet = item.getValue();

            if (columnNameCamelCaseSet.containsAll(supperFieldNameSet)) {
                return item.getKey();
            }

        }

        return null;

    }

    /**
     * 获取：没有父类字段 list
     */
    public static List<CodeGenerateItemDTO> getNoSupperClassColumnList(String supperClassName,
        List<CodeGenerateItemDTO> columnList) {

        if (supperClassName == null) {
            return new ArrayList<>(columnList);
        }

        Set<String> supperClassFieldNameSet = CodeGenerateHelperUtil.BASE_ENTITY_MAP.get(supperClassName);

        return columnList.stream().filter(it -> !supperClassFieldNameSet.contains(it.getColumnNameCamelCase()))
            .collect(Collectors.toList());
    }
}
