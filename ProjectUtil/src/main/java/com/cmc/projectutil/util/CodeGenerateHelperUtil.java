package com.cmc.projectutil.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.cmc.projectutil.model.dto.CodeGenerateForSpringListDTO;
import com.cmc.projectutil.model.entity.BaseEntity;
import com.cmc.projectutil.model.entity.BaseEntityFour;
import com.cmc.projectutil.model.entity.BaseEntityThree;
import com.cmc.projectutil.model.entity.BaseEntityTwo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CodeGenerateHelperUtil {

    public static final String TINYINT_ONE = "tinyint(1)";

    public static final Map<String, String> COLUMN_TYPE_REF_JAVA_MAP = MapUtil.newHashMap();

    private static final Map<String, Set<String>> BASE_ENTITY_MAP = MapUtil.newHashMap();

    static {

        COLUMN_TYPE_REF_JAVA_MAP.put(TINYINT_ONE, "Boolean");
        COLUMN_TYPE_REF_JAVA_MAP.put("tinyint", "Byte");
        COLUMN_TYPE_REF_JAVA_MAP.put("int", "Integer");
        COLUMN_TYPE_REF_JAVA_MAP.put("bigint", "Long");
        COLUMN_TYPE_REF_JAVA_MAP.put("varchar", "String");
        COLUMN_TYPE_REF_JAVA_MAP.put("text", "String");
        COLUMN_TYPE_REF_JAVA_MAP.put("longtext", "String");
        COLUMN_TYPE_REF_JAVA_MAP.put("datetime", "Date");

        putBaseEntityMap(BaseEntity.class);
        putBaseEntityMap(BaseEntityTwo.class);
        putBaseEntityMap(BaseEntityThree.class);
        putBaseEntityMap(BaseEntityFour.class);

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
     * 匹配父类，如果没有匹配上，则返回 null
     */
    public static String getSupperClassName(List<CodeGenerateForSpringListDTO> item) {

        Set<String> columnNameCamelCaseSet =
            item.stream().map(CodeGenerateForSpringListDTO::getColumnNameCamelCase).collect(Collectors.toSet());

        return null;

    }

}
