package com.petshop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.petshop.entity.OrderItem;

import java.util.List;

public interface OrderItemService extends IService<OrderItem> {

    /** 根据订单ID查询订单项列表 */
    List<OrderItem> getByOrderId(Long orderId);
}
