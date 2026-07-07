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

    private static final String[] PET_KEYWORDS = {
            "宠物", "狗狗", "狗", "猫咪", "猫", "兔子", "仓鼠", "小宠",
            "品种", "年龄", "性别", "购买宠物", "领养", "带回家"
    };
    private static final String[] SUPPLIES_KEYWORDS = {
            "狗粮", "猫粮", "玩具", "用品", "周边", "零食", "笼子",
            "窝", "粮", "罐头", "砂", "食盆", "水盆", "牵引", "项圈"
    };
    private static final String[] PRICE_KEYWORDS = {
            "价格", "多少钱", "优惠", "打折", "便宜", "贵", "预算"
    };
    private static final String[] STORE_KEYWORDS = {
            "店铺", "门店", "地址", "电话", "营业时间", "在哪", "怎么去",
            "地图", "位置", "附近", "联系方式"
    };
    private static final String[] HOT_KEYWORDS = {
            "热门", "推荐", "热销", "卖得好", "受欢迎", "有什么", "有哪些",
            "买什么", "推荐一下"
    };
    private static final String[] CARE_KEYWORDS = {
            "养护", "喂养", "护理", "怎么养", "吃什么", "注意事项", "生病",
            "疫苗", "驱虫", "绝育", "洗澡"
    };
    private static final String CARE_HINT =
            "## 宠物养护提示\n用户询问养护问题，请基于你的专业知识回答，但建议用户到店做具体检查。\n\n";
    private static final String AI_UNAVAILABLE_MSG = "抱歉，AI服务暂时不可用，请稍后再试！";
    private static final String FALLBACK_REPLY = "抱歉，我暂时无法回答这个问题，请稍后再试。";

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
            return reply != null ? reply : FALLBACK_REPLY;
        } catch (Exception e) {
            return AI_UNAVAILABLE_MSG;
        }
    }

    /**
     * 根据用户消息意图，查询数据库并构建上下文
     */
    private String buildDatabaseContext(String userMessage) {
        String msg = userMessage != null ? userMessage.toLowerCase() : "";
        StringBuilder ctx = new StringBuilder();

        appendCategorySection(ctx);

        boolean isPet = containsAny(msg, PET_KEYWORDS);
        boolean isSupplies = containsAny(msg, SUPPLIES_KEYWORDS);
        boolean isPrice = containsAny(msg, PRICE_KEYWORDS);
        boolean isStore = containsAny(msg, STORE_KEYWORDS);
        boolean isHot = containsAny(msg, HOT_KEYWORDS);
        boolean isCare = containsAny(msg, CARE_KEYWORDS);

        if (isPet || isHot) {
            ctx.append(buildProductSection("在售宠物", 1, 8));
        }
        if (isSupplies || isHot) {
            ctx.append(buildProductSection("宠物周边 / 用品", 2, 8));
        }
        if (isPrice || isHot) {
            ctx.append(buildProductSection("全部在售商品", null, 20));
        }
        if (isStore || isHot) {
            appendStoreSection(ctx);
        }
        if (isCare) {
            ctx.append(CARE_HINT);
        }

        ensureFallbackIfEmpty(ctx);
        return ctx.toString();
    }

    /**
     * 添加分类信息到上下文
     */
    private void appendCategorySection(StringBuilder ctx) {
        List<Category> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort));
        if (categories.isEmpty()) {
            return;
        }
        ctx.append("## 商品分类\n");
        for (Category c : categories) {
            String typeTag = c.getType() == 1 ? "🐾宠物" : "🛍️周边";
            ctx.append(String.format("- %s（%s）\n", c.getName(), typeTag));
        }
        ctx.append("\n");
    }

    /**
     * 添加店铺信息到上下文（详细版，用于意图匹配时）
     */
    private void appendStoreSection(StringBuilder ctx) {
        List<Store> stores = storeService.list(
                new LambdaQueryWrapper<Store>().eq(Store::getStatus, 0));
        if (stores.isEmpty()) {
            return;
        }
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

    /**
     * 当没有匹配任何意图时，补充默认内容
     */
    private void ensureFallbackIfEmpty(StringBuilder ctx) {
        String content = ctx.toString();
        boolean hasProductSection = content.contains("## 在售宠物") || content.contains("## 宠物周边");
        if (ctx.length() == 0 || (content.trim().startsWith("## 商品分类") && !hasProductSection)) {
            ctx.append(buildProductSection("热门推荐", null, 10));
            appendStoreSectionCompact(ctx);
        }
    }

    /**
     * 添加店铺信息（精简版，用于默认兜底）
     */
    private void appendStoreSectionCompact(StringBuilder ctx) {
        List<Store> stores = storeService.list(
                new LambdaQueryWrapper<Store>().eq(Store::getStatus, 0));
        if (stores.isEmpty()) {
            return;
        }
        ctx.append("## 附近店铺\n");
        for (Store s : stores) {
            ctx.append(String.format("- %s | %s%s%s | ⭐%.1f\n",
                    s.getName(), nvl(s.getProvince()), nvl(s.getCity()),
                    nvl(s.getDistrict()), s.getRating() != null ? s.getRating() : 5.0));
        }
        ctx.append("\n");
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

        if (products.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("## ").append(title).append("（共 ").append(products.size()).append(" 件）\n");
        for (Product p : products) {
            sb.append(formatProductLine(p));
        }
        sb.append("\n");
        return sb.toString();
    }

    /**
     * 格式化单行商品信息
     */
    private String formatProductLine(Product p) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("- **%s**", p.getName()));
        if (p.getProductType() != null && p.getProductType() == 1) {
            sb.append(" 🐾宠物");
            if (p.getBreed() != null) {
                sb.append(" | 品种：" + p.getBreed());
            }
            if (p.getAge() != null) {
                sb.append(" | 年龄：" + p.getAge());
            }
            if (p.getGender() != null) {
                sb.append(" | 性别：" + formatGender(p.getGender()));
            }
        }
        sb.append(String.format(" | 💰¥%.2f", p.getPrice()));
        if (p.getStock() != null && p.getStock() > 0) {
            sb.append(String.format(" | 库存：%d", p.getStock()));
        }
        if (p.getSales() != null && p.getSales() > 0) {
            sb.append(String.format(" | 已售：%d", p.getSales()));
        }
        sb.append(String.format(" [查看详情](product:%d)", p.getId()));
        sb.append("\n");
        return sb.toString();
    }

    /**
     * 格式化性别字段
     */
    private static String formatGender(Integer gender) {
        if (gender == null) {
            return "未知";
        }
        if (gender == 1) {
            return "公";
        }
        if (gender == 2) {
            return "母";
        }
        return "未知";
    }

    private static boolean containsAny(String text, String... keywords) {
        for (String kw : keywords) {
            if (text.contains(kw)) {
                return true;
            }
        }
        return false;
    }

    private static String nvl(String s) {
        return s != null ? s : "";
    }

    @Override
    public void chatStream(String userMessage, String sessionId) {
        // TODO: 实现流式对话（SSE）—— 后续版本迭代实现
    }
}
