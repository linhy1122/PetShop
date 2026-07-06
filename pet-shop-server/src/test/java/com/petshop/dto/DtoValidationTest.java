package com.petshop.dto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DTO Bean Validation — JUnit 5 纯单元测试
 */
@DisplayName("DTO 参数校验")
class DtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Nested
    @DisplayName("LoginDto")
    class LoginDtoTest {
        @Test void blankUsername() {
            LoginDto d = dto(); d.setUsername("");
            assertHasViolation(d, "用户名不能为空");
        }
        @Test void blankPassword() {
            LoginDto d = dto(); d.setPassword(null);
            assertHasViolation(d, "密码不能为空");
        }
        @Test void blankCaptchaKey() {
            LoginDto d = dto(); d.setCaptchaKey(" ");
            assertHasViolation(d, "验证码不能为空");
        }
        @Test void allValid() { assertNoViolation(dto()); }

        LoginDto dto() {
            LoginDto d = new LoginDto();
            d.setUsername("user"); d.setPassword("pass"); d.setCaptchaKey("key");
            return d;
        }
    }

    @Nested
    @DisplayName("RegisterDto")
    class RegisterDtoTest {
        @Test void blankUsername() {
            RegisterDto d = valid(); d.setUsername(null);
            assertHasViolation(d, "用户名");
        }
        @Test void blankPassword() {
            RegisterDto d = valid(); d.setPassword(null);
            assertHasViolation(d, "密码");
        }
        @Test void phoneOptional() { assertNoViolation(valid()); }

        RegisterDto valid() {
            RegisterDto d = new RegisterDto();
            d.setUsername("u"); d.setPassword("p"); d.setCaptchaKey("k");
            return d;
        }
    }

    @Nested
    @DisplayName("OrderCreateDto")
    class OrderCreateDtoTest {
        @Test void nullAddress() {
            OrderCreateDto d = new OrderCreateDto();
            assertHasViolation(d, "收货地址不能为空");
        }
        @Test void valid() {
            OrderCreateDto d = new OrderCreateDto(); d.setAddressId(1L);
            assertNoViolation(d);
        }
    }

    @Nested
    @DisplayName("ProductDto")
    class ProductDtoTest {
        @Test void blankName() {
            ProductDto d = valid(); d.setName("");
            assertHasViolation(d, "商品名称");
        }
        @Test void nullCategory() {
            ProductDto d = valid(); d.setCategoryId(null);
            assertHasViolation(d, "分类");
        }
        @Test void nullStore() {
            ProductDto d = valid(); d.setStoreId(null);
            assertHasViolation(d, "店铺");
        }
        @Test void nullPrice() {
            ProductDto d = valid(); d.setPrice(null);
            assertHasViolation(d, "价格");
        }
        @Test void allValid() { assertNoViolation(valid()); }

        ProductDto valid() {
            ProductDto d = new ProductDto();
            d.setName("金毛犬"); d.setCategoryId(1L); d.setStoreId(1L);
            d.setPrice(new BigDecimal("1500"));
            return d;
        }
    }

    @Nested
    @DisplayName("UserAdminDto")
    class UserAdminDtoTest {
        @Test void memberLevelOutOfRange() {
            UserAdminDto d = valid(); d.setMemberLevel(4);
            assertHasViolation(d, "会员等级");
        }
        @Test void nullMemberLevel() {
            UserAdminDto d = valid(); d.setMemberLevel(null);
            assertHasViolation(d, "会员等级不能为空");
        }
        @Test void statusOutOfRange() {
            UserAdminDto d = valid(); d.setStatus(2);
            assertHasViolation(d, "用户状态");
        }
        @Test void nullStatus() {
            UserAdminDto d = valid(); d.setStatus(null);
            assertHasViolation(d, "用户状态不能为空");
        }
        @Test void validChinesePhone() {
            UserAdminDto d = valid(); d.setPhone("13812345678");
            assertNoViolation(d);
        }
        @Test void emptyPhoneOk() {
            UserAdminDto d = valid(); d.setPhone("");
            assertNoViolation(d);
        }

        @ParameterizedTest
        @ValueSource(strings = { "12345678901", "abc", "138-1234-5678" })
        void invalidPhone(String phone) {
            UserAdminDto d = valid(); d.setPhone(phone);
            assertHasViolation(d, "手机号");
        }

        @Test void invalidEmail() {
            UserAdminDto d = valid(); d.setEmail("not-email");
            assertHasViolation(d, "邮箱");
        }
        @Test void allValid() { assertNoViolation(valid()); }

        UserAdminDto valid() {
            UserAdminDto d = new UserAdminDto();
            d.setMemberLevel(0); d.setStatus(0);
            return d;
        }
    }

    @Nested
    @DisplayName("StoreDto")
    class StoreDtoTest {
        @Test void blankName() {
            StoreDto d = valid(); d.setName("");
            assertHasViolation(d, "店铺名称");
        }
        @Test void blankPhone() {
            StoreDto d = valid(); d.setPhone(null);
            assertHasViolation(d, "联系电话");
        }
        @Test void blankCity() {
            StoreDto d = valid(); d.setCity("");
            assertHasViolation(d, "城市");
        }
        @Test void nullStatus() {
            StoreDto d = valid(); d.setStatus(null);
            assertHasViolation(d, "店铺状态");
        }
        @Test void ratingOutOfRange() {
            StoreDto d = valid(); d.setRating(new BigDecimal("5.1"));
            assertHasViolation(d, "评分");
        }
        @Test void allValid() { assertNoViolation(valid()); }

        StoreDto valid() {
            StoreDto d = new StoreDto();
            d.setName("店铺"); d.setPhone("138"); d.setCity("上海");
            d.setAddress("地址"); d.setStatus(1);
            return d;
        }
    }

    @Nested
    @DisplayName("VideoDto")
    class VideoDtoTest {
        @Test void blankTitle() {
            VideoDto d = valid(); d.setTitle(null);
            assertHasViolation(d, "标题");
        }
        @Test void blankVideoUrl() {
            VideoDto d = valid(); d.setVideoUrl("");
            assertHasViolation(d, "视频");
        }
        @Test void allValid() { assertNoViolation(valid()); }

        VideoDto valid() {
            VideoDto d = new VideoDto();
            d.setTitle("视频"); d.setVideoUrl("http://a.mp4");
            return d;
        }
    }

    // ========== helpers ==========
    private <T> void assertHasViolation(T dto, String keyword) {
        Set<ConstraintViolation<T>> v = validator.validate(dto);
        assertFalse(v.isEmpty(), "期望有校验错误，关键词: " + keyword);
        assertTrue(v.stream().anyMatch(e ->
                e.getMessage().contains(keyword)));
    }

    private <T> void assertNoViolation(T dto) {
        assertTrue(validator.validate(dto).isEmpty());
    }
}
