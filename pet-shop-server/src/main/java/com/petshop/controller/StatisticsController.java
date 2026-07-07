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
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理端统计数据接口
 */
@RestController
@RequestMapping("/api/admin/statistics")
public class StatisticsController {

    private static final String COLUMN_TOTAL = "total";

    @Autowired
    private ProductService productService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

    /**
     * 解析 yearMonth 参数（格式：YYYY-MM），返回 {year, month}。
     * 若参数为空则返回当前年月。
     */
    private int[] parseYearMonth(String yearMonth) {
        if (yearMonth == null || yearMonth.isEmpty()) {
            YearMonth now = YearMonth.now();
            return new int[]{now.getYear(), now.getMonthValue()};
        }
        YearMonth ym = YearMonth.parse(yearMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
        return new int[]{ym.getYear(), ym.getMonthValue()};
    }

    /** 获取总览数据（四项总数 + 营收 + 订单状态分布） */
    @GetMapping("/overview")
    public Result<Map<String, Object>> overview(
            @RequestParam(required = false) String yearMonth) {
        int[] ym = parseYearMonth(yearMonth);
        int year = ym[0];
        int month = ym[1];

        Map<String, Object> data = new HashMap<>();
        data.put("products", productService.count());
        data.put("stores", storeService.count());
        data.put("orders", orderService.count());
        data.put("users", userService.count());

        // ========== 营收统计（按所选月份） ==========

        // 当月GMV：所选月份创建的订单 total_amount 总和（含退款，仅排除已取消 status = -1）
        QueryWrapper<Order> monthGmvQw = new QueryWrapper<>();
        monthGmvQw.select("IFNULL(SUM(total_amount), 0) AS total")
             .apply("YEAR(create_time) = {0} AND MONTH(create_time) = {1}", year, month)
             .ne("status", -1);
        BigDecimal monthGmv = (BigDecimal) orderService.listMaps(monthGmvQw).get(0).get(COLUMN_TOTAL);
        data.put("monthGmv", monthGmv);

        // 当月实收：所选月份已支付订单的 pay_amount 总和
        QueryWrapper<Order> monthRevQw = new QueryWrapper<>();
        monthRevQw.select("IFNULL(SUM(pay_amount), 0) AS total")
               .apply("YEAR(pay_time) = {0} AND MONTH(pay_time) = {1}", year, month)
               .in("status", 1, 2, 3, 4);
        BigDecimal monthRevenue = (BigDecimal) orderService.listMaps(monthRevQw).get(0).get(COLUMN_TOTAL);
        data.put("monthRevenue", monthRevenue);

        // 当月退款：所选月份退款成功的 refund_money 总和
        QueryWrapper<Order> refundQw = new QueryWrapper<>();
        refundQw.select("IFNULL(SUM(refund_money), 0) AS total")
                .apply("YEAR(refund_time) = {0} AND MONTH(refund_time) = {1}", year, month)
                .in("status", -3, -4);
        BigDecimal monthRefund = (BigDecimal) orderService.listMaps(refundQw).get(0).get(COLUMN_TOTAL);
        data.put("monthRefund", monthRefund);

        // 当月净收：实收 - 退款
        BigDecimal monthNetRevenue = monthRevenue.subtract(monthRefund);
        data.put("monthNetRevenue", monthNetRevenue);

        // ========== 订单状态分布（全量） ==========
        QueryWrapper<Order> statusQw = new QueryWrapper<>();
        statusQw.select("status", "COUNT(*) AS count")
                .groupBy("status")
                .orderByAsc("status");
        List<Map<String, Object>> statusDistribution = orderService.listMaps(statusQw);
        data.put("statusDistribution", statusDistribution);

        // 回传当前使用的年月
        data.put("yearMonth", String.format("%d-%02d", year, month));

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

    /** 获取指定月份每日营收趋势数据 */
    @GetMapping("/monthly-daily")
    public Result<Map<String, Object>> monthlyDaily(
            @RequestParam(required = false) String yearMonth) {
        int[] ym = parseYearMonth(yearMonth);
        int year = ym[0];
        int month = ym[1];

        YearMonth targetMonth = YearMonth.of(year, month);
        YearMonth currentMonth = YearMonth.now();

        // 如果是当前月份，截至今天；否则截至该月最后一天
        LocalDate lastDay;
        if (targetMonth.equals(currentMonth)) {
            lastDay = LocalDate.now();
        } else {
            lastDay = targetMonth.atEndOfMonth();
        }
        LocalDate firstDay = targetMonth.atDay(1);
        int daysInMonth = lastDay.getDayOfMonth();

        // 生成日期列表
        List<String> dates = new ArrayList<>();
        for (int i = 1; i <= daysInMonth; i++) {
            dates.add(targetMonth.atDay(i).toString());
        }

        Map<String, Object> data = new HashMap<>();
        data.put("dates", dates);
        data.put("yearMonth", String.format("%d-%02d", year, month));

        // 每日GMV：按 create_time，status != -1（含退款，仅排除已取消）
        List<BigDecimal> gmv = new ArrayList<>();
        for (int i = 1; i <= daysInMonth; i++) {
            LocalDate date = targetMonth.atDay(i);
            QueryWrapper<Order> qw = new QueryWrapper<>();
            qw.select("IFNULL(SUM(total_amount), 0) AS total")
              .apply("DATE(create_time) = {0}", date.toString())
              .ne("status", -1);
            BigDecimal val = (BigDecimal) orderService.listMaps(qw).get(0).get(COLUMN_TOTAL);
            gmv.add(val != null ? val : BigDecimal.ZERO);
        }
        data.put("gmv", gmv);

        // 每日实收：按 pay_time，status IN (1,2,3,4)
        List<BigDecimal> revenue = new ArrayList<>();
        for (int i = 1; i <= daysInMonth; i++) {
            LocalDate date = targetMonth.atDay(i);
            QueryWrapper<Order> qw = new QueryWrapper<>();
            qw.select("IFNULL(SUM(pay_amount), 0) AS total")
              .apply("DATE(pay_time) = {0}", date.toString())
              .in("status", 1, 2, 3, 4);
            BigDecimal val = (BigDecimal) orderService.listMaps(qw).get(0).get(COLUMN_TOTAL);
            revenue.add(val != null ? val : BigDecimal.ZERO);
        }
        data.put("revenue", revenue);

        // 每日退款：按 refund_time，status IN (-3,-4)
        List<BigDecimal> refund = new ArrayList<>();
        for (int i = 1; i <= daysInMonth; i++) {
            LocalDate date = targetMonth.atDay(i);
            QueryWrapper<Order> qw = new QueryWrapper<>();
            qw.select("IFNULL(SUM(refund_money), 0) AS total")
              .apply("DATE(refund_time) = {0}", date.toString())
              .in("status", -3, -4);
            BigDecimal val = (BigDecimal) orderService.listMaps(qw).get(0).get(COLUMN_TOTAL);
            refund.add(val != null ? val : BigDecimal.ZERO);
        }
        data.put("refund", refund);

        // 每日净收：实收 - 退款
        List<BigDecimal> netRevenue = new ArrayList<>();
        for (int i = 0; i < daysInMonth; i++) {
            netRevenue.add(revenue.get(i).subtract(refund.get(i)));
        }
        data.put("netRevenue", netRevenue);

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
