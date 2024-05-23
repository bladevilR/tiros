package org.jeecg.modules.dispatch.workorder.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yyg
 */
@Data
public class BuOutsourceRepairRecord implements Serializable {
    private static final long serialVersionUID=1L;
    private String id;
    @ApiModelProperty(value = "出入库单Id")
    private String outinId;
    @ApiModelProperty(value = "部件名称")
    private String assetName;
    @ApiModelProperty(value = "部件类型id")
    private String assetTypeId;
    @ApiModelProperty(value = "车辆id")
    private String trainId;
    @ApiModelProperty(value = "车号")
    private String trainNo;
    @ApiModelProperty(value = "线路名称")
    private String lineName;
    @ApiModelProperty(value = "线路id")
    private  String lineId;
    @ApiModelProperty(value = "委外厂商id")
    private String supplierId;
    @ApiModelProperty(value = "委外厂商名")
    private String supplierName;
    @ApiModelProperty(value = "合同id")
    private String contractId;
    @ApiModelProperty(value = "合同名字")
    private String contractName;
    @ApiModelProperty(value = "送修班组id")
    private String sendGroupId;
    @ApiModelProperty(value = "送修班组名称")
    private String sendGroupName;
    @ApiModelProperty(value = "移交日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date transferDate;
    @ApiModelProperty(value = "监造人")
    private String supervisor;
    @ApiModelProperty(value = "计划返厂时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date returnTime;
    @ApiModelProperty(value = "实际返厂时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date actReturnTime;
    @ApiModelProperty(value = "是否返厂0 否 1 是,字典bu_state")
    @Dict(dicCode = "bu_state")
    private Integer returnStatus;
    @ApiModelProperty(value = "设置的逾期返厂原因")
    private String delayReason;
    @ApiModelProperty(value = "逾期天数")
    private Integer delayDays;
    @ApiModelProperty(value = "状态")
    @Dict(dicCode = "bu_perform_status")
    private Integer status;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "返厂明细Id")
    private String outDetailId;
    @ApiModelProperty(value = "当前车辆")
    private String curTrain;
    @ApiModelProperty(value = "第几列车")
    private Integer trainIndex;


}
