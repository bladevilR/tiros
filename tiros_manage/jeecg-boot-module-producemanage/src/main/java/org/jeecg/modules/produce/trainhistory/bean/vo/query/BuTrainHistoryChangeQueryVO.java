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
 * 车辆履历-更换记录 查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class BuTrainHistoryChangeQueryVO extends HistoryRecordsQueryVO {

    @ApiModelProperty(value = "部件名称")
    private String assetName;

    @ApiModelProperty(value = "系统id")
    private String sysId;

    @ApiModelProperty(value = "更换日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date changeDate;

}
