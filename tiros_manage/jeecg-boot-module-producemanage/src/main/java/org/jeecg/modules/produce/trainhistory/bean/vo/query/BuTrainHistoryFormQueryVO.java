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
public class BuTrainHistoryFormQueryVO extends HistoryRecordsQueryVO {

    @ApiModelProperty("表单编号或者名称")
    private String searchText;

    @ApiModelProperty(value = "修程id")
    private String repairProgramId;

    @ApiModelProperty(value = "班组id")
    private String groupId;

    @ApiModelProperty(value = "表单记录类型 1作业记录表2数据记录表3检查记录表4工单附件")
    private Integer formRecordType;

    @ApiModelProperty("工单时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date orderTime;

}
