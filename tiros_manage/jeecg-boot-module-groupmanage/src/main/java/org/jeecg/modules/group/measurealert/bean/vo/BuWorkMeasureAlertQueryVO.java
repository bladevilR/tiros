package org.jeecg.modules.group.measurealert.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 测量预警记录查询VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/13
 */
@Data
public class BuWorkMeasureAlertQueryVO {

    @ApiModelProperty(value = "预警描述")
    private String alertDescribe;

    @ApiModelProperty(value = "处理状态  字典dictCode=bu_work_measure_alert_status")
    private Integer status;

    @ApiModelProperty(value = "预警时间 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date alertTime;

    @ApiModelProperty(value = "作业工位id")
    private String workstationId;

}
