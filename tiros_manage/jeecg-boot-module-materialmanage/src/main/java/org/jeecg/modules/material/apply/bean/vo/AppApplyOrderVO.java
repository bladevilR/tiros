package org.jeecg.modules.material.apply.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * APP领用工单vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-20
 */
@Data
@Accessors(chain = true)
public class AppApplyOrderVO {

    @ApiModelProperty(value = "工单id")
    private String orderId;

    @ApiModelProperty(value = "工单号")
    private String orderCode;

    @ApiModelProperty(value = "工单名称")
    private String orderName;

    @ApiModelProperty(value = "申请数量")
    private BigDecimal applyAmount;

    @ApiModelProperty(value = "未发数量")
    private BigDecimal undeliveredAmount;

    @ApiModelProperty(value = "领用状态 字典dictCode=bu_material_apply_status")
    @Dict(dicCode = "bu_material_apply_status")
    private Integer status;

    @ApiModelProperty(value = "班组id")
    private String groupId;

    @ApiModelProperty(value = "班组名称")
    private String groupName;

    @ApiModelProperty(value = "工单时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date orderTime;

}
