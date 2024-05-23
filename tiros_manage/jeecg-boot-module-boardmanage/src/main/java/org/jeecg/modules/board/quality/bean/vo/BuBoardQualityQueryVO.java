package org.jeecg.modules.board.quality.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.util.DateUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Calendar;
import java.util.Date;

/**
 * <p>
 * 质量看板（车间）查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/11
 */
@Data
@Accessors(chain = true)
public class BuBoardQualityQueryVO {

    @ApiModelProperty(value = "所属车辆段id")
    private String depotId;

    @ApiModelProperty(value = "所属线路id")
    private String lineId;

    @ApiModelProperty(value = "车辆号")
    private String trainNo;

    @ApiModelProperty(value = "修程类型 1架修 2大修 0或不传查所有")
    private Integer programType;

    @ApiModelProperty(value = "开始时间 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @ApiModelProperty(value = "结束时间 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @ApiModelProperty(value = "故障来源 1手动创建 2导入历史故障 3同步历史质保故障")
    private Integer fromType;

    @ApiModelProperty(value = "最新故障/委外故障条数")
    private Integer faultQuantity;

    @ApiModelProperty(value = "故障排名条数")
    private Integer rankingQuantity;

    public void setCurrentMonthQueryDate() {
        Calendar calendar = Calendar.getInstance();
        this.endDate = DateUtils.transToDayEnd(DateUtils.transToMonthEndDay(calendar)).getTime();
        this.startDate = DateUtils.transToDayStart(DateUtils.transToMonthStartDay(calendar)).getTime();
    }

    public void setLatestTwoMonthQueryDate() {
        Calendar calendar = Calendar.getInstance();
        this.endDate = DateUtils.transToDayEnd(DateUtils.transToMonthEndDay(calendar)).getTime();
        calendar.add(Calendar.MONTH, -1);
        this.startDate = DateUtils.transToDayStart(DateUtils.transToMonthStartDay(calendar)).getTime();
    }

}
