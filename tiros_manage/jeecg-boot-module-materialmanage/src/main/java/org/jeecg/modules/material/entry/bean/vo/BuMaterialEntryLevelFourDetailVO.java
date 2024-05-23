package org.jeecg.modules.material.entry.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * @author yuyougen
 * @title: BuMaterialEntryLevelFourDetailVO
 * @projectName tiros_manage
 * @description: TODO
 * @date 2021/7/714:56
 */
@Data
public class BuMaterialEntryLevelFourDetailVO {

    @ApiModelProperty(value = "入库明细id", required = true)
    @NotBlank
    private String buMaterialEntryDetailId;

    @ApiModelProperty(value = "生产日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date productionDate;

    @ApiModelProperty(value = "失效日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date expirDate;

    @ApiModelProperty(value = "有效期，有效天数")
    private Integer expirDay;

    @ApiModelProperty(value = "存放位置", notes = "架大修库位id")
    private String selfWarehouseId;

    @ApiModelProperty(value = "入库数量", notes = "确认入库的数量")
    private Double confirmAmount;

    @ApiModelProperty(value = "物料属性 1紧固件 2备品备件 3车体 4车上电气 5辅助 6牵引 7制定 8转向架   字典bu_material_attr")
    private String materialAttr;


    @ApiModelProperty(value = "所属入库单id", required = true)
    @NotBlank
    private String entryOrderId;
    @ApiModelProperty(value = "四级库入库数据")
    private List<LevelFourEntryData> confirmVO;
}
