package com.petshop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单操作日志
 */
@Data
@TableName("t_order_log")
public class OrderLog implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    /** 订单ID */
    private Long orderId;
    /** 操作人 */
    private String operator;
    /** 原状态 */
    private Integer fromStatus;
    /** 新状态 */
    private Integer toStatus;
    /** 备注 */
    private String remark;
    /** 创建时间 */
    private LocalDateTime createTime;
}
