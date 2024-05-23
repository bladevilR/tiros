package org.jeecg.modules.material.entry.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author yuyougen
 * @title: BuMaterialEntryLevelFourDetailArry
 * @projectName tiros_manage
 * @description: TODO
 * @date 2021/7/717:27
 */
@Data
public class LevelFourEntryData {
    @ApiModelProperty(value = "四级库id", required = true)
    @NotBlank
    private String selfWarehouseId;
    @ApiModelProperty(value = "入库数量", required = true)
    @NotNull
    private Double  amount;
    @ApiModelProperty(value = "四级库名称", hidden = true)
    private String selfWarehouseName;
}
