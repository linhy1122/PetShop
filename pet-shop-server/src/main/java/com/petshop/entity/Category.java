package com.petshop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.petshop.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品分类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_category")
public class Category extends BaseEntity {

    /** 分类名称 */
    private String name;
    /** 父分类ID（0表示顶级分类） */
    private Long parentId;
    /** 分类类型：1-宠物, 2-宠物周边 */
    private Integer type;
    /** 排序 */
    private Integer sort;
    /** 分类图标 */
    private String icon;
}
