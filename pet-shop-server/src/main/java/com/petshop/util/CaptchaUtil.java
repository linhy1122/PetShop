package com.petshop.util;

import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;

/**
 * 滑块验证码工具类
 */
public class CaptchaUtil {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String SECRET_KEY = "PetShopCaptcha@2024";
    private static final HMac HMAC = new HMac(HmacAlgorithm.HmacSHA256, SECRET_KEY.getBytes());

    private static final int BG_WIDTH = 300;
    private static final int BG_HEIGHT = 160;
    private static final int PIECE_WIDTH = 50;
    private static final int PIECE_HEIGHT = 50;
    private static final int PIECE_Y = 62;
    private static final int X_MIN = 30;
    private static final int X_MAX = BG_WIDTH - PIECE_WIDTH - 30;

    public static final int TOLERANCE = 5;

    /**
     * 生成滑块验证码（滑块为背景图的真实抠图）
     */
    public static Map<String, Object> generate() {
        int pieceX = X_MIN + RANDOM.nextInt(X_MAX - X_MIN);

        // 1. 生成背景图案
        BufferedImage bg = new BufferedImage(BG_WIDTH, BG_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(220 + RANDOM.nextInt(35), 220 + RANDOM.nextInt(35), 220 + RANDOM.nextInt(35)));
        g.fillRect(0, 0, BG_WIDTH, BG_HEIGHT);

        // 随机方块
        for (int i = 0; i < 12; i++) {
            g.setColor(randomPastel());
            g.fillRoundRect(RANDOM.nextInt(BG_WIDTH - 30), RANDOM.nextInt(BG_HEIGHT - 30),
                    20 + RANDOM.nextInt(60), 20 + RANDOM.nextInt(40), 6, 6);
        }
        // 随机圆形
        for (int i = 0; i < 8; i++) {
            g.setColor(randomPastel());
            int cr = 8 + RANDOM.nextInt(20);
            int cx = RANDOM.nextInt(BG_WIDTH);
            int cy = RANDOM.nextInt(BG_HEIGHT);
            g.fillOval(cx - cr, cy - cr, cr * 2, cr * 2);
        }
        g.dispose();

        // 2. 从背景抠出滑块（真实图案）
        BufferedImage slider = new BufferedImage(PIECE_WIDTH, PIECE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D sg = slider.createGraphics();
        sg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        sg.drawImage(bg.getSubimage(pieceX, PIECE_Y, PIECE_WIDTH, PIECE_HEIGHT), 0, 0, null);
        // 给滑块加边框使其可见
        sg.setColor(new Color(255, 255, 255, 150));
        sg.setStroke(new BasicStroke(2));
        sg.drawRoundRect(1, 1, PIECE_WIDTH - 2, PIECE_HEIGHT - 2, 4, 4);
        sg.setColor(new Color(70, 130, 180));
        sg.setStroke(new BasicStroke(1.5f));
        sg.drawRoundRect(1, 1, PIECE_WIDTH - 2, PIECE_HEIGHT - 2, 4, 4);
        sg.dispose();

        // 3. 在背景上标记抠图位置
        Graphics2D g2 = bg.createGraphics();
        g2.setColor(new Color(0, 0, 0, 50));
        g2.fillRect(pieceX, PIECE_Y, PIECE_WIDTH, PIECE_HEIGHT);
        g2.setColor(new Color(100, 100, 100));
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10f, new float[]{6f, 3f}, 0f));
        g2.drawRect(pieceX, PIECE_Y, PIECE_WIDTH, PIECE_HEIGHT);
        g2.dispose();

        String token = signToken(pieceX);

        return Map.of(
                "captchaKey", token,
                "bgImage", toBase64(bg),
                "sliderImage", toBase64(slider),
                "sliderY", PIECE_Y
        );
    }

    /**
     * 验证滑块位置（供前端即时校验和后端业务校验共用）
     */
    public static boolean verify(String captchaKey, int userX) {
        try {
            String[] parts = captchaKey.split("\\.");
            if (parts.length != 2) return false;

            String expectedSig = HMAC.digestHex(parts[0]);
            if (!expectedSig.equals(parts[1])) return false;

            JSONObject json = JSONUtil.parseObj(new String(Base64.getDecoder().decode(parts[0])));
            int expectedX = json.getInt("x");
            long timestamp = json.getLong("t");

            if (System.currentTimeMillis() - timestamp > 5 * 60 * 1000) return false;

            return Math.abs(userX - expectedX) <= TOLERANCE;
        } catch (Exception e) {
            return false;
        }
    }

    private static String signToken(int x) {
        JSONObject payload = new JSONObject();
        payload.set("x", x);
        payload.set("t", System.currentTimeMillis());
        String encoded = Base64.getEncoder().encodeToString(payload.toString().getBytes());
        return encoded + "." + HMAC.digestHex(encoded);
    }

    private static String toBase64(BufferedImage image) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", bos);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("图片编码失败", e);
        }
    }

    private static Color randomPastel() {
        return new Color(180 + RANDOM.nextInt(60), 180 + RANDOM.nextInt(60), 210 + RANDOM.nextInt(45));
    }
}
