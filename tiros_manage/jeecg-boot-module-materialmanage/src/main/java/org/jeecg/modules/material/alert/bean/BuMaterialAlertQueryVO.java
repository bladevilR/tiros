package org.jeecg.modules.material.alert.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 物资预警查询VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/9/18
 */
@Data
public class BuMaterialAlertQueryVO {

    @ApiModelProperty("物资编码或名称")
    private String searchText;

    @ApiModelProperty(value = "物资类别 字典dictCode=bu_material_type")
    private Integer materialType;

    @ApiModelProperty("物质预警类别 字典dictCode=bu_material_alert_type")
    private Integer alertType;

    @ApiModelProperty("仓库id")
    private String warehouseId;

    @ApiModelProperty("是否已读")
    private Boolean  read;

    @ApiModelProperty(value = "用户id",hidden = true)
    private String userId;

    @ApiModelProperty(value = "物资到期日期 yyyy-MM-dd",hidden = true)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date materialExpireDateLine;

}
