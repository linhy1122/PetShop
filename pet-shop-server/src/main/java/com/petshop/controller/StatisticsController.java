package com.petshop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.petshop.common.Result;
import com.petshop.entity.Order;
import com.petshop.entity.Product;
import com.petshop.entity.Store;
import com.petshop.entity.User;
import com.petshop.service.OrderService;
import com.petshop.service.ProductService;
import com.petshop.service.StoreService;
import com.petshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理端统计数据接口
 */
@RestController
@RequestMapping("/api/admin/statistics")
public class StatisticsController {

    @Autowired
    private ProductService productService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

    /** 获取总览数据（四项总数 + 营收 + 订单状态分布） */
    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        Map<String, Object> data = new HashMap<>();
        data.put("products", productService.count());
        data.put("stores", storeService.count());
        data.put("orders", orderService.count());
        data.put("users", userService.count());

        // ========== 营收统计 ==========

        // 今日GMV：今日创建的订单 total_amount 总和（排除取消/退款状态）
        QueryWrapper<Order> gmvQw = new QueryWrapper<>();
        gmvQw.select("IFNULL(SUM(total_amount), 0) AS total")
             .apply("DATE(create_time) = CURDATE()")
             .notIn("status", -1, -2, -3, -4);
        BigDecimal todayGmv = (BigDecimal) orderService.listMaps(gmvQw).get(0).get("total");
        data.put("todayGmv", todayGmv);

        // 今日实收：今日支付的 pay_amount 总和
        QueryWrapper<Order> revQw = new QueryWrapper<>();
        revQw.select("IFNULL(SUM(pay_amount), 0) AS total")
             .apply("DATE(pay_time) = CURDATE()")
             .in("status", 1, 2, 3, 4);
        BigDecimal todayRevenue = (BigDecimal) orderService.listMaps(revQw).get(0).get("total");
        data.put("todayRevenue", todayRevenue);

        // 本月实收：本月已支付订单的 pay_amount 总和
        QueryWrapper<Order> monthQw = new QueryWrapper<>();
        monthQw.select("IFNULL(SUM(pay_amount), 0) AS total")
               .apply("YEAR(create_time) = YEAR(CURDATE()) AND MONTH(create_time) = MONTH(CURDATE())")
               .in("status", 1, 2, 3, 4);
        BigDecimal monthRevenue = (BigDecimal) orderService.listMaps(monthQw).get(0).get("total");
        data.put("monthRevenue", monthRevenue);

        // 本月退款：本月退款成功的 refund_money 总和
        QueryWrapper<Order> refundQw = new QueryWrapper<>();
        refundQw.select("IFNULL(SUM(refund_money), 0) AS total")
                .apply("YEAR(refund_time) = YEAR(CURDATE()) AND MONTH(refund_time) = MONTH(CURDATE())")
                .in("status", -3, -4);
        BigDecimal monthRefund = (BigDecimal) orderService.listMaps(refundQw).get(0).get("total");
        data.put("monthRefund", monthRefund);

        // ========== 订单状态分布 ==========
        QueryWrapper<Order> statusQw = new QueryWrapper<>();
        statusQw.select("status", "COUNT(*) AS count")
                .groupBy("status")
                .orderByAsc("status");
        List<Map<String, Object>> statusDistribution = orderService.listMaps(statusQw);
        data.put("statusDistribution", statusDistribution);

        return Result.ok(data);
    }

    /** 热卖 Top 10 */
    @GetMapping("/top-sales")
    public Result<List<Product>> topSales() {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1)
               .orderByDesc(Product::getSales)
               .last("LIMIT 10");
        return Result.ok(productService.list(wrapper));
    }

    /** 获取近7日趋势数据 */
    @GetMapping("/trend")
    public Result<Map<String, Object>> trend() {
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(6);

        List<String> dates = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            dates.add(today.minusDays(i).toString());
        }

        Map<String, Object> data = new HashMap<>();
        data.put("dates", dates);
        data.put("products", countProductsByDate(sevenDaysAgo));
        data.put("stores", countStoresByDate(sevenDaysAgo));
        data.put("orders", countOrdersByDate(sevenDaysAgo));
        data.put("users", countUsersByDate(sevenDaysAgo));
        return Result.ok(data);
    }

    /** 商品按日统计 */
    private List<Integer> countProductsByDate(LocalDate startDate) {
        List<Product> list = productService.list(
            new LambdaQueryWrapper<Product>()
                .ge(Product::getCreateTime, startDate.atStartOfDay())
        );
        return groupByCreateDate(list, startDate);
    }

    /** 店铺按日统计 */
    private List<Integer> countStoresByDate(LocalDate startDate) {
        List<Store> list = storeService.list(
            new LambdaQueryWrapper<Store>()
                .ge(Store::getCreateTime, startDate.atStartOfDay())
        );
        return groupByCreateDate(list, startDate);
    }

    /** 订单按日统计 */
    private List<Integer> countOrdersByDate(LocalDate startDate) {
        List<Order> list = orderService.list(
            new LambdaQueryWrapper<Order>()
                .ge(Order::getCreateTime, startDate.atStartOfDay())
        );
        return groupByCreateDate(list, startDate);
    }

    /** 用户按日统计 */
    private List<Integer> countUsersByDate(LocalDate startDate) {
        List<User> list = userService.list(
            new LambdaQueryWrapper<User>()
                .ge(User::getCreateTime, startDate.atStartOfDay())
        );
        return groupByCreateDate(list, startDate);
    }

    /** 将记录按创建日期分组，返回连续7天的每日数量 */
    private List<Integer> groupByCreateDate(List<? extends com.petshop.common.BaseEntity> records, LocalDate startDate) {
        Map<LocalDate, Long> countMap = records.stream()
            .filter(r -> r.getCreateTime() != null)
            .collect(Collectors.groupingBy(
                r -> r.getCreateTime().toLocalDate(),
                Collectors.counting()
            ));

        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            result.add(countMap.getOrDefault(date, 0L).intValue());
        }
        return result;
    }
}
