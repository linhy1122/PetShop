package com.petshop.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 会员等级与折扣配置
 */
public class MemberLevel {

    /** 普通用户 — 原价 */
    public static final int NORMAL = 0;
    /** 银卡会员 — 95折 */
    public static final int SILVER = 1;
    /** 金卡会员 — 9折 */
    public static final int GOLD = 2;
    /** 钻石会员 — 85折 */
    public static final int DIAMOND = 3;

    /** 累计消费金额升级阈值 */
    private static final BigDecimal SILVER_THRESHOLD = new BigDecimal("1000");
    private static final BigDecimal GOLD_THRESHOLD = new BigDecimal("5000");
    private static final BigDecimal DIAMOND_THRESHOLD = new BigDecimal("20000");

    /**
     * 获取折扣率（例如 0.95 = 95折）
     */
    public static BigDecimal getDiscountRate(int level) {
        switch (level) {
            case SILVER:  return new BigDecimal("0.95");
            case GOLD:    return new BigDecimal("0.90");
            case DIAMOND: return new BigDecimal("0.85");
            default:      return BigDecimal.ONE;
        }
    }

    /**
     * 计算会员价 = 原价 × 折扣率，保留两位小数
     */
    public static BigDecimal getMemberPrice(BigDecimal originalPrice, int level) {
        return originalPrice.multiply(getDiscountRate(level))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 根据累计消费金额计算应升级到的会员等级
     */
    public static int calcLevel(BigDecimal totalSpent) {
        if (totalSpent.compareTo(DIAMOND_THRESHOLD) >= 0) return DIAMOND;
        if (totalSpent.compareTo(GOLD_THRESHOLD) >= 0) return GOLD;
        if (totalSpent.compareTo(SILVER_THRESHOLD) >= 0) return SILVER;
        return NORMAL;
    }

    /** 等级名称 */
    public static String getName(int level) {
        switch (level) {
            case SILVER:  return "银卡会员";
            case GOLD:    return "金卡会员";
            case DIAMOND: return "钻石会员";
            default:      return "普通用户";
        }
    }

    /** 折扣描述 */
    public static String getDiscountDesc(int level) {
        switch (level) {
            case SILVER:  return "95折";
            case GOLD:    return "9折";
            case DIAMOND: return "85折";
            default:      return "无折扣";
        }
    }
}
