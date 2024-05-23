package org.jeecg.modules.basemanage.alertaccept.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 预警信息
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BuBaseAlertAcceptVO implements Serializable {
    @ApiModelProperty(value = " 预警类型：1. 物资库存预警  2 物资到期预警  3 工器具送检预警  4 部件质保预警  5 测量异常预警  6 委外逾期预警  7 工单逾期预警  8  故障处理预警")
    private Integer alertType;
}
