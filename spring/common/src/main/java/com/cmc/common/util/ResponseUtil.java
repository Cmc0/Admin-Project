package com.cmc.common.util;

import com.cmc.common.exception.BaseBizCodeEnum;
import com.cmc.common.exception.BaseException;
import com.cmc.common.model.vo.ApiResultVO;
import lombok.SneakyThrows;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;

public class ResponseUtil {

    @SneakyThrows
    public static void out(HttpServletResponse response, BaseBizCodeEnum baseBizCodeEnum) {

        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();

        try {
            ApiResultVO.error(baseBizCodeEnum); // 这里肯定会抛出 BaseException异常
        } catch (BaseException e) {
            out.write(e.getMessage()); // 变成 json字符串，输出给前端
            out.flush();
            out.close();
        }
    }

    @SneakyThrows
    public static void out(HttpServletResponse response, String msg) {

        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();

        try {
            ApiResultVO.error(msg); // 这里肯定会抛出 BaseException异常
        } catch (BaseException e) {
            out.write(e.getMessage()); // 变成 json字符串，输出给前端
            out.flush();
            out.close();
        }
    }

    /**
     * 获取 excel下载的 OutputStream
     */
    @SneakyThrows
    public static OutputStream getExcelOutputStream(HttpServletResponse response, String fileName) {

        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response
            .setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");

        return response.getOutputStream();
    }

}
