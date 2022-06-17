package com.admin.start;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@SpringBootTest
class StartApplicationTests {

    /**
     * 同步：数据库字段和 entity实体类字段一致
     */
    @Test
    void syncEntityFromDb() {

    }

    public static void main(String[] args) {

        String userDir = System.getProperty("user.dir");

        String slash = "/";

        String java = ".java";

        String basePath = "/src/main/java/";

        File userDirFile = FileUtil.file(userDir);

        File[] fileArr = userDirFile.listFiles(File::isDirectory);

        List<File> fileList = CollUtil.newArrayList(fileArr);

        Map<String, String> map = fileList.stream().collect(Collectors
            .toMap(it -> slash + StrUtil.toCamelCase(it.getName(), '-').toLowerCase() + slash, File::getName));

        Set<Class<?>> classSet = ClassUtil.scanPackageByAnnotation("com.admin", TableName.class);

        for (Class<?> item : classSet) {

            String replace = basePath + StrUtil.replace(item.getName(), ".", slash);

            AtomicReference<String> name = new AtomicReference<>();
            map.entrySet().stream().filter(it -> replace.contains(it.getKey())).findFirst().ifPresent(it -> {
                name.set(it.getValue());
            });

            String fullPath = userDir + slash + name.get() + replace + java;

            System.out.println(fullPath);

            File file = FileUtil.file(fullPath);

            System.out.println(FileUtil.exist(file));

            System.out.println("================================");

        }

    }

}
