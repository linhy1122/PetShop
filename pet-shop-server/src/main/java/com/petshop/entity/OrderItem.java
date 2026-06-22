package com.petshop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.petshop.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 订单项
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_order_item")
public class OrderItem extends BaseEntity {

    /** 订单ID */
    private Long orderId;
    /** 商品ID */
    private Long productId;
    /** 商品名称（快照） */
    private String productName;
    /** 商品图片（快照） */
    private String productImage;
    /** 购买数量 */
    private Integer quantity;
    /** 商品单价（快照） */
    private BigDecimal price;
    /** 小计 */
    private BigDecimal subtotal;
}
