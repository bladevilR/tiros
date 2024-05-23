package org.jeecg.modules.quality.recordconfirm.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 工单作业记录查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/12/11
 */
@Data
@Accessors(chain = true)
public class BuFormRecordQueryVO {

    @ApiModelProperty(value = "线路id")
    private String lineId;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "修程id")
    private String repairProgramId;

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @ApiModelProperty(value = "结束时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @ApiModelProperty(value = "表单编码或名称")
    private String formSearchText;

    @ApiModelProperty(value = "表单类型 字典dictCode=bu_work_form_type")
    private Integer formType;

    @ApiModelProperty(value = "列计划id")
    private String planId;

    @ApiModelProperty(value = "班组id")
    private String groupId;

}
