package com.admin.common.model.dto;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "分页参数，查询所有：pageSize = -1，默认：current = 1，pageSize = 10")
public class MyPageDTO {

    @ApiModelProperty(value = "第几页")
    private long current = 1;

    @ApiModelProperty(value = "每页显示条数")
    private long pageSize = 10;

    @ApiModelProperty(value = "排序字段")
    private MyOrderDTO order;

    /**
     * 分页属性拷贝
     */
    public <T> Page<T> getPage() {
        Page<T> page = new Page<>();

        page.setCurrent(getCurrent());
        page.setSize(getPageSize());

        if (getOrder() == null || StrUtil.isBlank(order.getName())) {
            return page;
        }

        // 添加 orderList里面的排序规则
        page.orders().add(orderToOrderItem(getOrder()));

        return page;
    }

    /**
     * 分页属性拷贝-增加：默认创建时间 倒序排序
     */
    public <T> Page<T> getCreateTimeDescDefaultOrderPage() {
        return getDefaultOrderPage("createTime", false);
    }

    /**
     * 分页属性拷贝-增加：默认排序
     */
    public <T> Page<T> getDefaultOrderPage(String column, boolean asc) {

        Page<T> page = getPage();

        if (CollUtil.isEmpty(page.orders())) {
            page.orders().add(new OrderItem(column, asc));
        }

        return page;
    }

    /**
     * 自定义的排序规则，转换为 mybatis plus 的排序规则
     */
    public static OrderItem orderToOrderItem(MyOrderDTO order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setColumn(order.getName());
        if (StrUtil.isNotBlank(order.getValue())) {
            orderItem.setAsc("ascend".equals(order.getValue()));
        }
        return orderItem;
    }

}
