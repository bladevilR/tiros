package org.jeecg.modules.produce.trainhistory.bean.vo.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 车辆履历-记录 查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
@Data
@Accessors(chain = true)
public class HistoryRecordsQueryVO {

    @ApiModelProperty(value = "车辆号")
    private String trainNo;

    @ApiModelProperty(value = "车辆结构明细id")
    private String structureDetailId;

    @ApiModelProperty(value = "车辆设备id 具体车辆设备")
    private String assetId;

    @ApiModelProperty(value = "查询类型：1车辆履历 2部件履历；为2时只会根据assetId查询车辆设备自身数据")
    private Integer queryType;

    /**
     * 查询开始时间，后端使用，用于查询时间段内的记录
     */
    @ApiModelProperty(hidden = true)
    private Date startTime;

    /**
     * 查询结束时间，后端使用，用于查询时间段内的记录
     */
    @ApiModelProperty(hidden = true)
    private Date endTime;

}
