package org.jeecg.modules.produce.trainhistory.bean.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 车辆履历相关表单
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-04
 */
@Data
@Accessors(chain = true)
public class BuTrainHistoryFormRecordVO {

    @ApiModelProperty(value = "表单记录类型 1作业记录表2数据记录表3检查记录表4工单附件")
    private Integer formRecordType;

    @ApiModelProperty(value = "表单记录类型名称")
    private String formRecordTypeName;

    @ApiModelProperty(value = "表单id 根据类型分别为：1=作业记录表实例id、2=数据记录表实例id、3=检查记录表实例id、4=工单附件id")
    private String formRecordId;

    @ApiModelProperty(value = "表单名称")
    private String formRecordName;

    @ApiModelProperty(value = "工单id")
    private String orderId;

    @ApiModelProperty(value = "工单编码")
    private String orderCode;

    @ApiModelProperty(value = "工单名称")
    private String orderName;

    @ApiModelProperty(value = "工单时间（计划开始时间）")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date orderTime;

    @ApiModelProperty(value = "修程id")
    private String repairProgramId;

    @ApiModelProperty(value = "修程名称")
    private String repairProgramName;

    @ApiModelProperty(value = "班组id")
    private String groupId;

    @ApiModelProperty(value = "班组名称")
    private String groupName;

    @ApiModelProperty(value = "表单类型4=工单附件时，附件的路径")
    private String orderAnnexPath;

    @ApiModelProperty(value = "列计划表单id")
    private String planFormId;

    @ApiModelProperty(value = "表单id")
    private String formObjId;

    @ApiModelProperty(value = "表单类型 字典dictCode=bu_work_form_type")
    @Dict(dicCode = "bu_work_form_type")
    private Integer formType;

    @ApiModelProperty(value = "作业记录表类型 0=不是作业记录表 1=老版作业记录表 2=新版excel作业记录表")
    private Integer workRecordType;

}
