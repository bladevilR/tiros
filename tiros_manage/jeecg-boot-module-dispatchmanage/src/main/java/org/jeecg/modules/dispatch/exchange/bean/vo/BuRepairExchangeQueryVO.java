package org.jeecg.modules.dispatch.exchange.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * <p>
 * 交接车管理查询VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/9/24
 */
@Data
public class BuRepairExchangeQueryVO {

    @ApiModelProperty(value = "交接类型 0接车 1交车 字典dictCode=bu_repair_exchange_tradeType", required = true)
    @NotNull
    private Integer tradeType;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "修程id")
    private String programId;

    @ApiModelProperty(value = "接收日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date acceptDate;

    @ApiModelProperty(value = "交付日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date sendDate;

    @ApiModelProperty(value = "状态 字典dictCode=bu_repair_exchange_status")
    private Integer status;

}
