package org.jeecg.modules.board.progress.bean.vo;

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
 * 进度看板(车间)查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/13
 */
@Data
@Accessors(chain = true)
public class BuBoardProgressQueryVO {

    @ApiModelProperty(value = "车辆段id  字典dictCode=(bu_mtr_depot,name,id)")
    private String depotId;

    @ApiModelProperty(value = "线路id  字典dictCode=(bu_mtr_line,line_name,line_id,line_id in (select line_id from bu_mtr_depot_line where depot_id = '{车辆段id}'))")
    private String lineId;

    @ApiModelProperty(value = "车辆号")
    private String trainNo;

    @ApiModelProperty(value = "开始时间 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @ApiModelProperty(value = "结束时间 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @ApiModelProperty(value = "工班id 获取班组任务时用到")
    private String groupId;

    public void setCurrentMonthQueryDate() {
        Calendar calendar = Calendar.getInstance();
        this.endDate = DateUtils.transToDayEnd(DateUtils.transToMonthEndDay(calendar)).getTime();
        this.startDate = DateUtils.transToDayStart(DateUtils.transToMonthStartDay(calendar)).getTime();
    }

}
