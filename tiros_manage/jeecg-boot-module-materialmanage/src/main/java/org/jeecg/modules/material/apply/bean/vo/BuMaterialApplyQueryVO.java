package org.jeecg.modules.material.apply.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 物料申请查询VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-16
 */
@Data
public class BuMaterialApplyQueryVO {

    @ApiModelProperty(value = "物资名称或编码")
    private String materialType;

    @ApiModelProperty(value = "所属申请单名称或编码")
    private String applyOrder;

    @ApiModelProperty(value = "领用人员或工班")
    private String receiver;

    @ApiModelProperty(value = "领用日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date receiveTime;

    @ApiModelProperty(value = "领用状态 字典dictCode=bu_material_apply_status")
    private Integer status;

    @ApiModelProperty(value = "备料状态 字典dictCode=bu_material_apply_ready_status")
    private Integer readyStatus;

}
