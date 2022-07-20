package com.admin.common.configuration.mvc;

import lombok.SneakyThrows;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义 DispatcherServlet，目的：可以多次获取 requestBody
 */
public class MyDispatcherServlet extends DispatcherServlet {

    @SneakyThrows
    @Override
    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) {
        super.doDispatch(new MyHttpServletRequestWrapper(request), response);
    }

}
