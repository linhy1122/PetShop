package com.petshop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.petshop.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 宠物视频
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_video")
public class Video extends BaseEntity {

    /** 视频标题 */
    private String title;
    /** 视频描述 */
    private String description;
    /** 视频封面图 */
    private String cover;
    /** 视频URL */
    private String videoUrl;
    /** 关联商品ID（可选） */
    private Long productId;
    /** 宠物分类ID */
    private Long categoryId;
    /** 视频时长（秒） */
    private Integer duration;
    /** 播放量 */
    private Integer viewCount;
    /** 点赞数 */
    private Integer likeCount;
    /** 状态：0-审核中, 1-已发布, 2-已下架 */
    private Integer status;
}
