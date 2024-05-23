package org.jeecg.modules.basemanage.workrecord.bean.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 工单作业记录检查bo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-16
 */
@Data
@Accessors(chain = true)
public class BuWorkOrderRecordCheckBO {

    @ApiModelProperty(value = "工单作业记录检查id")
    private String id;

    @ApiModelProperty(value = "作业记录表明细id")
    private String workRecordDetailId;

    @ApiModelProperty(value = "检查类型 1自检2互检3专检4抽检5过程检6过程检确认7交接检8交接检确认  字典dictCode=bu_work_order_record_check_type")
    @Dict(dicCode = "bu_work_order_record_check_type")
    private Integer checkType;

    @ApiModelProperty(value = "检查人id")
    private String checkUserId;

    @ApiModelProperty(value = "检查时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date checkTime;

    @ApiModelProperty(value = "检查人名称")
    private String checkUserName;

}
