package com.petshop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.petshop.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品评价
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_review")
public class Review extends BaseEntity {

    /** 用户ID */
    private Long userId;
    /** 订单ID */
    private Long orderId;
    /** 商品ID */
    private Long productId;
    /** 评分（1-5星） */
    private Integer rating;
    /** 评价内容 */
    private String content;
    /** 评价图片（JSON数组） */
    private String images;
    /** 商家回复 */
    private String reply;
}
