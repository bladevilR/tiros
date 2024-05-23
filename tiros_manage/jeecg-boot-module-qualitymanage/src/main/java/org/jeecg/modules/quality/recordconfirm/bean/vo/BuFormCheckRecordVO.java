package org.jeecg.modules.quality.recordconfirm.bean.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.modules.quality.recordconfirm.bean.BuPlanFormCheckRecordJudge;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 记录/数据表单实例vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-02
 */
@Data
@Accessors(chain = true)
public class BuFormCheckRecordVO {

    @ApiModelProperty(value = "表单实例id")
    private String id;

    @ApiModelProperty(value = "表单编码")
    private String code;

    @ApiModelProperty(value = "表单名称")
    private String name;

    @ApiModelProperty(value = "表单类型  字典dictCode=bu_work_form_type")
    @Dict(dicCode = "bu_work_form_type")
    private Integer formType;

    @ApiModelProperty(value = "对应的记录表单id/在线表单id")
    private String formObjId;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "工单id")
    private String orderId;

    @ApiModelProperty(value = "工单编码")
    private String orderCode;

    @ApiModelProperty(value = "工单名称")
    private String orderName;

    @ApiModelProperty(value = "工单时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date orderTime;

    @ApiModelProperty(value = "列计划id")
    private String planId;

    @ApiModelProperty(value = "列计划名称")
    private String planName;

    @ApiModelProperty(value = "线路id")
    private String lineId;

    @ApiModelProperty(value = "线路名称")
    private String lineName;

    @ApiModelProperty(value = "修程id")
    private String repairProgramId;

    @ApiModelProperty(value = "修程名称")
    private String repairProgramName;

    @ApiModelProperty(value = "设备名称 有具体设备=具体设备名，没有具体设备=设备类型名")
    private String assetName;

    @ApiModelProperty(value = "是否已检查 字典dictCode=bu_check_status")
    @Dict(dicCode = "bu_check_status")
    private Integer status;

    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "任务与表单实例的关联关系id", required = true)
    private String task2InstId;



    @ApiModelProperty(value = "质量评定列表")
    private List<BuPlanFormCheckRecordJudge> judgeList;


    /**
     * 表单实例和工单任务关联中的表单实例类型，当表单实例是由工单添加而非来自列计划时formType会为空，此时用instType覆盖formType
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private Integer instType;

}
