package org.jeecg.modules.quality.recordconfirm.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 工单作业记录检查确认vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/12/11
 */
@Data
@Accessors(chain = true)
public class BuFormWorkRecordCheckVO {

    @ApiModelProperty(value = "作业记录实列id")
    private String id;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "检查结果 0未通过1通过  字典dicCode=bu_work_order_record_result")
    @Dict(dicCode = "bu_work_order_record_result")
    private Integer checkResult;

    @ApiModelProperty(value = "检查日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date checkDate;

    @ApiModelProperty(value = "质量负责人id")
    private String checkUserId;

}
