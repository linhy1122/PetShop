package com.petshop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.petshop.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单
 * 订单状态：
 *   0-已下单（待支付）
 *   1-已支付（待发货）
 *   2-已发货（待收货）
 *   3-已收货（待评价）
 *   4-已完成（已评价）
 *  -1-已取消  -2-申请退单中  -3-退单审核通过  -4-管理员直接退单
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_order")
public class Order extends BaseEntity {

    /** 订单编号（业务编号，展示用） */
    private String orderNo;
    /** 用户ID */
    private Long userId;
    /** 收货地址ID */
    private Long addressId;
    /** 订单总金额 */
    private BigDecimal totalAmount;
    /** 实付金额 */
    private BigDecimal payAmount;
    /** 支付方式 */
    private String payMethod;
    /** 支付时间 */
    private LocalDateTime payTime;
    /** 订单状态 */
    private Integer status;
    /** 订单备注 */
    private String remark;
    /** 取消/退单原因 */
    private String cancelReason;
    /** 物流公司 */
    private String logisticsCompany;
    /** 物流单号 */
    private String logisticsNo;
    /** 发货时间 */
    private LocalDateTime deliveryTime;
    /** 收货时间 */
    private LocalDateTime receiveTime;
    /** 完成时间 */
    private LocalDateTime finishTime;
}
