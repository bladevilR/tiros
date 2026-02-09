package org.jeecg.modules.basemanage.quotabom.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel(value = "定额BOM树节点", description = "定额BOM结构树节点")
public class BuQuotaBomTreeNodeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "节点唯一键")
    private String key;

    @ApiModelProperty(value = "父节点键")
    private String parentKey;

    @ApiModelProperty(value = "显示标题")
    private String title;

    @ApiModelProperty(value = "节点层级：0车型 1线别 2位置 3系统 4二级模块 5三级模块 6BOM")
    private Integer level;

    @ApiModelProperty(value = "BOM主键，仅叶子节点存在")
    private String bomId;

    @ApiModelProperty(value = "子节点")
    private List<BuQuotaBomTreeNodeVO> children = new ArrayList<>();
}

