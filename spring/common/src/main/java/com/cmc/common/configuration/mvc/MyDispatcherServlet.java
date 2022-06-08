package com.cmc.common.configuration.mvc;

import lombok.SneakyThrows;
import org.springframework.web.servlet.DispatcherServlet;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义 DispatcherServlet 来分派 MyHttpServletRequestWrapper
 */
public class MyDispatcherServlet extends DispatcherServlet {

    /**
     * 包装成我们自定义的request
     */
    @SneakyThrows
    @Override
    protected void doDispatch(HttpServletRequest request, @Nonnull HttpServletResponse response) {
        super.doDispatch(new MyHttpServletRequestWrapper(request), response);
    }

}
