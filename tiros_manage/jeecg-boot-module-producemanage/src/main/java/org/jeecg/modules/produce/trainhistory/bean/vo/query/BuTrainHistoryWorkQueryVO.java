package org.jeecg.modules.produce.trainhistory.bean.vo.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 车辆履历-故障记录 查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class BuTrainHistoryWorkQueryVO extends HistoryRecordsQueryVO {

    @ApiModelProperty("工单编号或者名称")
    private String searchText;

    @ApiModelProperty("工单状态")
    private Integer status;

    @ApiModelProperty("工单时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date workOrderDate;

}
