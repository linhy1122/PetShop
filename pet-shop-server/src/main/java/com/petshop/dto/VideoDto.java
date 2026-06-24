package com.petshop.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 视频表单 DTO
 */
@Data
public class VideoDto {

    @NotBlank(message = "视频标题不能为空")
    private String title;

    private String description;

    /** 封面图 URL */
    private String cover;

    @NotBlank(message = "请上传视频文件")
    private String videoUrl;

    /** 关联商品ID */
    private Long productId;

    /** 宠物分类ID */
    private Long categoryId;

    /** 视频时长（秒） */
    private Integer duration;

    /** 状态：0-审核中, 1-已发布, 2-已下架 */
    private Integer status;
}
