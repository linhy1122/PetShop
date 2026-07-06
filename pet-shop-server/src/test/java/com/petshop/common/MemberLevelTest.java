package com.petshop.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 会员等级与折扣 — JUnit 5 纯单元测试
 */
@DisplayName("MemberLevel 会员等级")
class MemberLevelTest {

    // ==================== getDiscountRate ====================
    @Nested
    @DisplayName("getDiscountRate 折扣率")
    class GetDiscountRate {

        @ParameterizedTest(name = "等级 {0} → 折扣率 {1}")
        @CsvSource({ "0, 1.00", "1, 0.95", "2, 0.90", "3, 0.85" })
        void validLevelReturnsCorrectRate(int level, String expected) {
            assertEquals(0, new BigDecimal(expected).compareTo(
                    MemberLevel.getDiscountRate(level)));
        }

        @ParameterizedTest(name = "无效等级 {0} → 默认 1.00")
        @ValueSource(ints = { -1, 4, 99, Integer.MAX_VALUE })
        void invalidLevelReturnsOne(int level) {
            assertEquals(BigDecimal.ONE, MemberLevel.getDiscountRate(level));
        }
    }

    // ==================== getMemberPrice ====================
    @Nested
    @DisplayName("getMemberPrice 会员价计算")
    class GetMemberPrice {

        @Test
        @DisplayName("普通会员原价")
        void normalPrice() {
            assertEquals(new BigDecimal("100.00"),
                    MemberLevel.getMemberPrice(new BigDecimal("100"), 0));
        }

        @Test
        @DisplayName("银卡 95 折")
        void silver95() {
            assertEquals(new BigDecimal("95.00"),
                    MemberLevel.getMemberPrice(new BigDecimal("100"), 1));
        }

        @Test
        @DisplayName("金卡 9 折")
        void gold90() {
            assertEquals(new BigDecimal("90.00"),
                    MemberLevel.getMemberPrice(new BigDecimal("100"), 2));
        }

        @Test
        @DisplayName("钻石 85 折")
        void diamond85() {
            assertEquals(new BigDecimal("85.00"),
                    MemberLevel.getMemberPrice(new BigDecimal("100"), 3));
        }

        @Test
        @DisplayName("HALF_UP 舍入：99.99 × 0.95 = 94.9905 → 94.99")
        void halfUpRounding() {
            assertEquals(new BigDecimal("94.99"),
                    MemberLevel.getMemberPrice(new BigDecimal("99.99"), 1));
        }

        @Test
        @DisplayName("零金额")
        void zeroAmount() {
            assertEquals(new BigDecimal("0.00"),
                    MemberLevel.getMemberPrice(BigDecimal.ZERO, 3));
        }
    }

    // ==================== calcLevel ====================
    @Nested
    @DisplayName("calcLevel 根据累计消费升级")
    class CalcLevel {

        @Test
        @DisplayName("0 元 → 普通用户")
        void zeroIsNormal() { assertEquals(0, MemberLevel.calcLevel(BigDecimal.ZERO)); }

        @Test
        @DisplayName("999.99 → 普通用户（未达银卡门槛）")
        void belowSilver() { assertEquals(0, MemberLevel.calcLevel(new BigDecimal("999.99"))); }

        @Test
        @DisplayName("1000 → 银卡（刚好达标）")
        void exactlySilver() { assertEquals(1, MemberLevel.calcLevel(new BigDecimal("1000.00"))); }

        @Test
        @DisplayName("4999.99 → 银卡（未达金卡）")
        void belowGold() { assertEquals(1, MemberLevel.calcLevel(new BigDecimal("4999.99"))); }

        @Test
        @DisplayName("5000 → 金卡（刚好达标）")
        void exactlyGold() { assertEquals(2, MemberLevel.calcLevel(new BigDecimal("5000.00"))); }

        @Test
        @DisplayName("19999.99 → 金卡（未达钻石）")
        void belowDiamond() { assertEquals(2, MemberLevel.calcLevel(new BigDecimal("19999.99"))); }

        @Test
        @DisplayName("20000 → 钻石（刚好达标）")
        void exactlyDiamond() { assertEquals(3, MemberLevel.calcLevel(new BigDecimal("20000.00"))); }

        @Test
        @DisplayName("远超钻石仍为钻石")
        void farAboveDiamond() { assertEquals(3, MemberLevel.calcLevel(new BigDecimal("999999"))); }
    }

    // ==================== getName / getDiscountDesc ====================
    @Nested
    @DisplayName("getName 和 getDiscountDesc")
    class NameAndDesc {

        @Test
        @DisplayName("各等级中文名")
        void names() {
            assertEquals("普通用户", MemberLevel.getName(0));
            assertEquals("银卡会员", MemberLevel.getName(1));
            assertEquals("金卡会员", MemberLevel.getName(2));
            assertEquals("钻石会员", MemberLevel.getName(3));
            assertEquals("普通用户", MemberLevel.getName(-1)); // 无效默认
        }

        @Test
        @DisplayName("各等级折扣描述")
        void descriptions() {
            assertEquals("无折扣", MemberLevel.getDiscountDesc(0));
            assertEquals("95折", MemberLevel.getDiscountDesc(1));
            assertEquals("9折", MemberLevel.getDiscountDesc(2));
            assertEquals("85折", MemberLevel.getDiscountDesc(3));
            assertEquals("无折扣", MemberLevel.getDiscountDesc(99)); // 无效默认
        }
    }
}
