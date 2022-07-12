package com.admin.start;

import cn.hutool.core.io.FileUtil;
import lombok.SneakyThrows;

import java.io.File;

public class DoAdminPackage {

    /**
     * 打包：前端和后端
     */
    @SneakyThrows
    public static void main(String[] args) {

        String springPath = System.getProperty("user.dir"); // 例如：E:\Cmc0\Admin-Project\spring

        //        RuntimeUtil.execForStr("cmd /c cd " + springPath, "cmd /c mvn clean");

        File springFile = FileUtil.newFile(springPath);

        //        RuntimeUtil.exec(null, springFile, "cmd mvn clean");

        String jarPath = springPath + "/start/target/start-0.0.1-SNAPSHOT.jar";

        File jarFile = FileUtil.newFile(jarPath);

        //        Session session = JschUtil.getSession("139.196.121.3", 22, "root", "E:\\demo\\key1.pem", null);
        //
        //        Sftp sftp = JschUtil.createSftp(session);
        //
        //        sftp.close();

    }

}
