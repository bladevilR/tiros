package org.jeecg.modules.tiros.importer.bean;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 物资类型
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-19
 */
@Data
@Accessors(chain = true)
public class BuMaterialType {
    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 物资类型编码
     */
    private String code;

    /**
     * 物资类型名称
     */
    private String name;

    /**
     * 规格描述
     */
    private String spec;

    /**
     * 单位
     */
    private String unit;

    /**
     * 单价：单位元
     */
    private BigDecimal price;

    /**
     * 状态 0无效 1 有效
     */
    private Integer status;

    /**
     * 种类 字典dictCode=bu_material_kind  1物料 2工器具
     */
    private Integer kind;

    /**
     * 物料分类id
     */
    private String category;

    /**
     * 物料分类  字典dictCode=bu_material_type
     */
    private Integer category1;

    /**
     * 物料属性  字典dictCode=bu_material_attr
     */
    private String category2;

    /**
     * 所属分类3 备用分类，来自物资分类表，一般情况下不需要使用
     */
    private String category3;

    /**
     * 库存警戒 为-1表示没有设置，不需要预警，默认为-1，这里暂时不用，用物资属性表中的
     */
    private BigDecimal theshold;

    /**
     * 费用科目编码
     */
    private String subject;

    /**
     * 是否固定资产 0否1是
     */
    private Integer isAsset;

    /**
     * 是否总库直发 0否1是
     */
    private Integer fromHead;

    /**
     * 列管消耗 1消耗物资 2列管物资
     */
    private Integer isConsume;

    /**
     * 备注
     */
    private String remark;

    private Date createTime;

    private String createBy;

    private Date updateTime;
 
    private String updateBy;

}
