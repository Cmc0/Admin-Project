package com.admin.start;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RuntimeUtil;
import lombok.SneakyThrows;

import java.io.File;

public class DoAdminPackage {

    /**
     * 打包：前端和后端
     */
    @SneakyThrows
    public static void main(String[] args) {

        String springPath = System.getProperty("user.dir"); // 例如：E:\Cmc0\Admin-Project\spring

        System.out.println("后端打包 ↓");

        String execForStr = RuntimeUtil.execForStr("cmd /c cd " + springPath + "&& mvn clean package");

        System.out.println(execForStr);

        System.out.println("后端打包 ↑");

        String jarPath = springPath + "/start/target/start-0.0.1-SNAPSHOT.jar";

        File jarFile = FileUtil.newFile(jarPath);

        //        Session session = JschUtil.getSession("139.196.121.3", 22, "root", "E:\\demo\\key1.pem", null);
        //
        //        Sftp sftp = JschUtil.createSftp(session);
        //
        //        sftp.close();

    }

}
