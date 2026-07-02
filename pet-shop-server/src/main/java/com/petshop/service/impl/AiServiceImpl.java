package com.petshop.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petshop.entity.Category;
import com.petshop.entity.Product;
import com.petshop.entity.Store;
import com.petshop.mapper.CategoryMapper;
import com.petshop.service.AiService;
import com.petshop.service.ProductService;
import com.petshop.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI 智能客服服务实现（通义千问 + 数据库动态上下文注入）
 */
@Service
public class AiServiceImpl implements AiService {

    @Value("${ai.api-key}")
    private String apiKey;

    @Value("${ai.api-url}")
    private String apiUrl;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private StoreService storeService;

    private static final String BASE_SYSTEM_PROMPT =
            "你是 PetShop 宠物商店的智能客服助手。你会收到商店的实时数据，请优先基于这些真实数据回答用户问题。\n" +
            "回答规则：\n" +
            "1. 价格、库存、地址等信息必须与提供的数据一致，不要编造\n" +
            "2. 数据中没有的信息，可以说「这个问题建议您到店或联系店员咨询」\n" +
            "3. 语气亲切友好，像宠物店的店员一样，适当使用emoji\n" +
            "4. 用中文回答，简洁清晰";

    @Override
    public String chat(String userMessage, String sessionId) {
        try {
            String dbContext = buildDatabaseContext(userMessage);
            String systemPrompt = BASE_SYSTEM_PROMPT + "\n\n" + dbContext;

            JSONObject body = JSONUtil.createObj()
                    .set("model", "qwen-turbo")
                    .set("input", JSONUtil.createObj()
                            .set("messages", new Object[]{
                                    Map.of("role", "system", "content", systemPrompt),
                                    Map.of("role", "user", "content", userMessage)
                            }));

            String response = HttpRequest.post(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(body.toString())
                    .execute()
                    .body();

            JSONObject result = JSONUtil.parseObj(response);
            String reply = result.getByPath("output.text", String.class);
            return reply != null ? reply : "抱歉，我暂时无法回答这个问题，请稍后再试。";
        } catch (Exception e) {
            return "抱歉，AI服务暂时不可用，请稍后再试！";
        }
    }

    /**
     * 根据用户消息意图，查询数据库并构建上下文
     */
    private String buildDatabaseContext(String userMessage) {
        String msg = userMessage != null ? userMessage.toLowerCase() : "";
        StringBuilder ctx = new StringBuilder();

        // 1. 分类信息（始终提供，作为背景知识）
        List<Category> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort));
        if (!categories.isEmpty()) {
            ctx.append("## 商品分类\n");
            for (Category c : categories) {
                String typeTag = c.getType() == 1 ? "🐾宠物" : "🛍️周边";
                ctx.append(String.format("- %s（%s）\n", c.getName(), typeTag));
            }
            ctx.append("\n");
        }

        // 2. 意图识别 + 针对性查询
        boolean isPet = containsAny(msg, "宠物", "狗狗", "狗", "猫咪", "猫", "兔子", "仓鼠", "小宠",
                "品种", "年龄", "性别", "购买宠物", "领养", "带回家");
        boolean isSupplies = containsAny(msg, "狗粮", "猫粮", "玩具", "用品", "周边", "零食", "笼子",
                "窝", "粮", "罐头", "砂", "食盆", "水盆", "牵引", "项圈");
        boolean isPrice = containsAny(msg, "价格", "多少钱", "优惠", "打折", "便宜", "贵", "预算");
        boolean isStore = containsAny(msg, "店铺", "门店", "地址", "电话", "营业时间", "在哪", "怎么去",
                "地图", "位置", "附近", "联系方式");
        boolean isHot = containsAny(msg, "热门", "推荐", "热销", "卖得好", "受欢迎", "有什么", "有哪些",
                "买什么", "推荐一下");
        boolean isCare = containsAny(msg, "养护", "喂养", "护理", "怎么养", "吃什么", "注意事项", "生病",
                "疫苗", "驱虫", "绝育", "洗澡");

        // 宠物商品
        if (isPet || isHot) {
            ctx.append(buildProductSection("在售宠物", 1, 8));
        }

        // 周边商品
        if (isSupplies || isHot) {
            ctx.append(buildProductSection("宠物周边 / 用品", 2, 8));
        }

        // 价格查询 → 返回所有在售商品 top 20
        if (isPrice || isHot) {
            ctx.append(buildProductSection("全部在售商品", null, 20));
        }

        // 店铺信息
        if (isStore || isHot) {
            List<Store> stores = storeService.list(
                    new LambdaQueryWrapper<Store>().eq(Store::getStatus, 0));
            if (!stores.isEmpty()) {
                ctx.append("## 附近店铺\n");
                for (Store s : stores) {
                    ctx.append(String.format("- **%s** ⭐%.1f\n", s.getName(),
                            s.getRating() != null ? s.getRating() : 5.0));
                    ctx.append(String.format("  地址：%s%s%s %s\n",
                            nvl(s.getProvince()), nvl(s.getCity()),
                            nvl(s.getDistrict()), nvl(s.getAddress())));
                    if (s.getPhone() != null) {
                        ctx.append(String.format("  电话：%s\n", s.getPhone()));
                    }
                    ctx.append(String.format("  营业时间：%s\n",
                            s.getBusinessHours() != null ? s.getBusinessHours() : "09:00-21:00"));
                }
                ctx.append("\n");
            }
        }

        // 宠物养护 → 不需要数据库查询，LLM 自带知识
        if (isCare) {
            ctx.append("## 宠物养护提示\n用户询问养护问题，请基于你的专业知识回答，但建议用户到店做具体检查。\n\n");
        }

        // 如果没有匹配到任何意图，默认返回全量概览
        if (ctx.length() == 0 || (ctx.toString().trim().startsWith("## 商品分类") &&
                !ctx.toString().contains("## 在售宠物") &&
                !ctx.toString().contains("## 宠物周边"))) {
            ctx.append(buildProductSection("热门推荐", null, 10));
            List<Store> stores = storeService.list(
                    new LambdaQueryWrapper<Store>().eq(Store::getStatus, 0));
            if (!stores.isEmpty()) {
                ctx.append("## 附近店铺\n");
                for (Store s : stores) {
                    ctx.append(String.format("- %s | %s%s%s | ⭐%.1f\n",
                            s.getName(), nvl(s.getProvince()), nvl(s.getCity()),
                            nvl(s.getDistrict()), s.getRating() != null ? s.getRating() : 5.0));
                }
                ctx.append("\n");
            }
        }

        return ctx.toString();
    }

    /**
     * 构建商品列表上下文
     */
    private String buildProductSection(String title, Integer productType, int limit) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);
        if (productType != null) {
            wrapper.eq(Product::getProductType, productType);
        }
        wrapper.orderByDesc(Product::getSales).last("LIMIT " + limit);
        List<Product> products = productService.list(wrapper);

        if (products.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        sb.append("## ").append(title).append("（共 ").append(products.size()).append(" 件）\n");
        for (Product p : products) {
            sb.append(String.format("- **%s**", p.getName()));
            if (p.getProductType() == 1) {
                sb.append(" 🐾宠物");
                if (p.getBreed() != null) sb.append(" | 品种：" + p.getBreed());
                if (p.getAge() != null) sb.append(" | 年龄：" + p.getAge());
                if (p.getGender() != null) {
                    String gender = p.getGender() == 1 ? "公" : p.getGender() == 2 ? "母" : "未知";
                    sb.append(" | 性别：" + gender);
                }
            }
            sb.append(String.format(" | 💰¥%.2f", p.getPrice()));
            if (p.getStock() != null && p.getStock() > 0) {
                sb.append(String.format(" | 库存：%d", p.getStock()));
            }
            if (p.getSales() != null && p.getSales() > 0) {
                sb.append(String.format(" | 已售：%d", p.getSales()));
            }
            sb.append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }

    private static boolean containsAny(String text, String... keywords) {
        for (String kw : keywords) {
            if (text.contains(kw)) return true;
        }
        return false;
    }

    private static String nvl(String s) {
        return s != null ? s : "";
    }

    @Override
    public void chatStream(String userMessage, String sessionId) {
        // TODO: 实现流式对话（SSE）
    }
}
