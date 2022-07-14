package com.admin.start;

import cn.hutool.core.thread.ThreadUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

@SpringBootTest
class StartApplicationTests {

    @Test
    void contextLoads() {
    }

    @SneakyThrows
    public static void main(String[] args) {

        //        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        //
        //        System.out.println("请输入：1 全部打包 2 后端打包 3 前端打包");
        //
        //        ThreadUtil.execute(() -> {
        //            ThreadUtil.sleep(2000);
        //            System.out.println("close");
        //            try {
        //                bufferedReader.close();
        //            } catch (IOException e) {
        //                e.printStackTrace();
        //            }
        //        });
        //
        //        String readLine = bufferedReader.readLine();
        //
        //        int number = Convert.toInt(readLine, 1);
        //
        //        System.out.println(number);

        Thread thread = new Thread(() -> {
            ConsoleInputReadTask consoleInputReadTask = new ConsoleInputReadTask();
            consoleInputReadTask.call();
        });

        thread.start();

        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = (t, e) -> {
            System.out.println("UncaughtExceptionHandler");
            thread.interrupt();
        };

        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);

        ThreadUtil.sleep(5000);

        System.out.println("close");

    }

}

class ConsoleInputReadTask implements Callable {

    @SneakyThrows
    public String call() {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("ConsoleInputReadTask run() called.");

        String input;

        new Thread(() -> {
            ThreadUtil.sleep(2000);
            throw new RuntimeException("throw new RuntimeException");
        }).start();

        do {

            System.out.println("Please type something: ");

            try {

                // wait until we have data to complete a readLine()

                while (!br.ready() /* ADD SHUTDOWN CHECK HERE */) {

                    Thread.sleep(200);

                }

                input = br.readLine();

            } catch (InterruptedException e) {

                System.out.println("ConsoleInputReadTask() cancelled");

                return null;

            }

        } while ("".equals(input));

        System.out.println("Thank You for providing input!");

        return input;
    }

}
