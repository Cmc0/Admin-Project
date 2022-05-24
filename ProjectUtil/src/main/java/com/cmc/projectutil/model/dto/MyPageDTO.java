package com.cmc.projectutil.model.dto;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "分页参数，查询所有：pageSize = -1，默认：pageNum = 1，pageSize = 10")
public class MyPageDTO {

    @ApiModelProperty(value = "第几页")
    private Long pageNum;

    @ApiModelProperty(value = "每页显示条数")
    private Long pageSize;

    @ApiModelProperty(value = "排序 list")
    List<OrderItem> orderList;

    /**
     * 分页属性拷贝
     */
    public <T> Page<T> getPage() {
        Page<T> resPage = new Page<>();

        resPage.setCurrent(getPageNum());
        resPage.setSize(getPageSize());
        if (CollUtil.isEmpty(getOrderList())) {
            return resPage;
        }

        resPage.orders().addAll(getOrderList()); // 添加 orderList里面的排序规则
        return resPage;
    }

}
