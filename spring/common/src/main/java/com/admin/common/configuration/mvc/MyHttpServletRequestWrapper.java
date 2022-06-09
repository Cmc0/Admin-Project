package com.admin.common.configuration.mvc;

import cn.hutool.core.io.IoUtil;
import lombok.SneakyThrows;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

/**
 * 自定义 HttpServletRequestWrapper 来包装输入流
 */
public class MyHttpServletRequestWrapper extends HttpServletRequestWrapper {

    /**
     * 缓存下来的HTTP body
     */
    private byte[] body;

    @SneakyThrows
    public MyHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        body = IoUtil.readBytes(request.getInputStream());
    }

    /**
     * 重新包装输入流
     */
    @Override
    public ServletInputStream getInputStream() {
        return new ServletInputStream() {

            @Override
            public int read() {
                return new ByteArrayInputStream(body).read();
            }

            /**
             * 下面的方法一般情况下不会被使用，如果你引入了一些需要使用ServletInputStream的外部组件，可以重点关注一下。
             */
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }
        };
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(body)));
    }
}
