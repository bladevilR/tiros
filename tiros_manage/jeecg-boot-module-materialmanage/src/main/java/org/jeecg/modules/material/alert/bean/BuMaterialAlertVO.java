package org.jeecg.modules.material.alert.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 物资预警查看VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/9/18
 */
@Data
@Accessors(chain = true)
public class BuMaterialAlertVO {

    @ApiModelProperty("物资id")
    private String materialId;

    @ApiModelProperty("物资编码")
    private String materialCode;

    @ApiModelProperty("物资名称")
    private String materialName;

    @ApiModelProperty("物资描述")
    private String materialSpec;

    @ApiModelProperty("物资预警类别 字典dictCode=bu_material_alert_type")
    @Dict(dicCode = "bu_material_alert_type")
    private Integer alertType;

    @ApiModelProperty(value = "物资类别 字典dictCode=bu_material_type")
    @Dict(dicCode = "bu_material_type")
    private Integer materialType;

    @ApiModelProperty("警戒库存")
    private Double alertStock;

    @ApiModelProperty("当前库存")
    private Double currentStock;

    @ApiModelProperty("差量")
    private Double diffStock;

    @ApiModelProperty(value = "出厂日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date leaveFactory;

    @ApiModelProperty(value = "生产日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date productionDate;

    @ApiModelProperty(value = "有效天数")
    private Integer expirDay;

    @ApiModelProperty(value = "有效日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date expirDate;

}
