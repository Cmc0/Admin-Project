package com.admin.start;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ssh.JschUtil;
import cn.hutool.extra.ssh.Sftp;
import com.jcraft.jsch.Session;
import lombok.SneakyThrows;

import java.util.concurrent.CountDownLatch;

public class DoAdminPackage {

    private static final String VITE_REMOTE_PATH = "/mydata/nginx/html/h5";

    private static final String SPRING_REMOTE_EXEC_CMD = "docker restart spring-boot-cmc";

    /**
     * 打包：前端和后端
     */
    @SneakyThrows
    public static void main(String[] args) {

        Session session = JschUtil.getSession("139.196.121.3", 22, "root", "E:\\demo\\key1.pem", null);

        Sftp sftp = JschUtil.createSftp(session);

        String springPath = System.getProperty("user.dir"); // 例如：E:\Cmc0\Admin-Project\spring

        CountDownLatch countDownLatch = ThreadUtil.newCountDownLatch(2);

        ThreadUtil.execute(() -> doSpringPackage(springPath, countDownLatch, sftp, session));
        ThreadUtil.execute(() -> doVitePackage(springPath, countDownLatch, sftp));

        countDownLatch.await();

        sftp.close();
    }

    /**
     * 后端打包
     */
    private static void doSpringPackage(String springPath, CountDownLatch countDownLatch, Sftp sftp, Session session) {

        System.out.println("后端打包 ↓");

        long timeNumber = System.currentTimeMillis();

        RuntimeUtil.execForStr("cmd /c cd " + springPath + "&& mvn clean package");

        timeNumber = System.currentTimeMillis() - timeNumber;
        String timeStr = DateUtil.formatBetween(timeNumber);

        System.out.println("后端打包 ↑ 耗时：" + timeStr);

        System.out.println("后端打包上传 ↓");

        String jarPath = springPath + "/start/target/start-0.0.1-SNAPSHOT.jar";

        timeNumber = System.currentTimeMillis();

        sftp.put(jarPath, "/mydata/springboot");

        timeNumber = System.currentTimeMillis() - timeNumber;
        timeStr = DateUtil.formatBetween(timeNumber);

        System.out.println("后端打包上传 ↑ 耗时：" + timeStr);

        System.out.println("启动后端 ↓");

        timeNumber = System.currentTimeMillis();

        JschUtil.exec(session, SPRING_REMOTE_EXEC_CMD, CharsetUtil.CHARSET_UTF_8);

        timeNumber = System.currentTimeMillis() - timeNumber;
        timeStr = DateUtil.formatBetween(timeNumber);

        System.out.println("启动后端 ↑ 耗时：" + timeStr);

        countDownLatch.countDown();

        System.out.println("后端执行完毕！");
    }

    /**
     * 前端打包
     */
    public static void doVitePackage(String springPath, CountDownLatch countDownLatch, Sftp sftp) {

        System.out.println("前端打包 ↓");

        long timeNumber = System.currentTimeMillis();

        String vitePath = StrUtil.subBefore(springPath, "\\", true);

        vitePath = vitePath + "/ant-react";

        RuntimeUtil.execForStr("cmd /c cd " + vitePath + "&& npm run build");

        timeNumber = System.currentTimeMillis() - timeNumber;
        String timeStr = DateUtil.formatBetween(timeNumber);

        System.out.println("前端打包 ↑ 耗时：" + timeStr);

        System.out.println("前端打包上传 ↓");

        String viteBuildPath = vitePath + "/dist";

        timeNumber = System.currentTimeMillis();

        sftp.delDir(VITE_REMOTE_PATH);

        sftp.syncUpload(FileUtil.newFile(viteBuildPath), VITE_REMOTE_PATH);

        timeNumber = System.currentTimeMillis() - timeNumber;
        timeStr = DateUtil.formatBetween(timeNumber);

        System.out.println("前端打包上传 ↑ 耗时：" + timeStr);

        countDownLatch.countDown();

        System.out.println("前端执行完毕！");
    }

}
