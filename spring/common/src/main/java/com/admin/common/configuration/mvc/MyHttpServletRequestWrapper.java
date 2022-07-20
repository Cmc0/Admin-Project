package com.admin.common.configuration.mvc;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import lombok.SneakyThrows;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MyHttpServletRequestWrapper extends HttpServletRequestWrapper {

    /**
     * 缓存下来的 requestBody
     */
    private byte[] body;

    @SneakyThrows
    public MyHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        try {
            body = IoUtil.readBytes(request.getInputStream());
        } catch (IORuntimeException ignored) {
        }
    }

    /**
     * 重新包装输入流
     */
    @Override
    public ServletInputStream getInputStream() {

        InputStream bodyStream = new ByteArrayInputStream(body);

        return new ServletInputStream() {

            @SneakyThrows
            @Override
            public int read() {
                return bodyStream.read();
            }

            /**
             * 下面的方法一般情况下不会被使用
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
