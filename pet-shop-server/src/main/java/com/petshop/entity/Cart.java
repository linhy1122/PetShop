package com.petshop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.petshop.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 购物车
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_cart")
public class Cart extends BaseEntity {

    /** 用户ID */
    private Long userId;
    /** 商品ID */
    private Long productId;
    /** 数量 */
    private Integer quantity;
    /** 是否选中：0-否, 1-是 */
    private Integer checked;
}
