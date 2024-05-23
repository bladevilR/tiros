package org.jeecg.modules.dispatch.workorder.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 委外出入库单(交接)
 * </p>
 *
 * @author youGen
 * @since 2020-10-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuOutsourceOutin对象", description = "委外出入库单(交接)")
@TableName("bu_outsource_outin")
public class BuOutsourceOutin extends Model<BuOutsourceOutin> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "线路id 字典 bu_mtr_line,line_name,line_id", required = true)
    @NotBlank
    private String lineId;

    @ApiModelProperty(value = "车号", required = true)
    @NotBlank
    private String trainNo;

    @ApiModelProperty(value = "单号")
    private String billNo;

    @ApiModelProperty(value = "类型  字典dicCode=bu_bill_type   0出库单1入库单", required = true)
    @NotNull
    @Dict(dicCode = "bu_bill_type")
    private Integer billType;

    @ApiModelProperty(value = "送修班组id 字典bu_mtr_workshop_group,group_name,id", required = true)
    @NotBlank
    private String sendGroupId;

    @ApiModelProperty(value = "移交日期", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private Date transferDate;

    @ApiModelProperty(value = "委外厂商id  字典bu_base_supplier,name,id", required = true)
    @NotBlank
    private String supplierId;

    @ApiModelProperty(value = "委外合同id  字典bu_contract_info,contract_name,id", required = true)
    @NotBlank
    private String contractId;

    @ApiModelProperty(value = "移交人员id")
    private String transferUserId;

    @ApiModelProperty(value = "接收人员id")
    private String receiveUser;

    @ApiModelProperty(value = "工程师id", required = true)
    @NotBlank
    private String engineerId;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "工单id")
    private String workOrderId;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("移交单名")
    private String outinName;

    @ApiModelProperty(value = "工单任务id")
    private String orderTaskId;

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;


    @ApiModelProperty(value = "线路名称", hidden = true)
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "送修班组名称")
    @TableField(exist = false)
    private String sendGroupName;

    @ApiModelProperty(value = "委外厂商名称")
    @TableField(exist = false)
    private String supplierName;

    @ApiModelProperty(value = "委外合同名称")
    @TableField(exist = false)
    private String contractName;

    @ApiModelProperty(value = "工单号")
    @TableField(exist = false)
    private String workOrderNo;

    @ApiModelProperty(value = "移交人员名称")
    @TableField(exist = false)
    private String transferUserName;

    @ApiModelProperty(value = "接收人员名称")
    @TableField(exist = false)
    private String receiveUserName;

    @ApiModelProperty(value = "工程师名称")
    @TableField(exist = false)
    private String engineerName;

    @ApiModelProperty(value = "维修所需天数")
    @TableField(exist = false)
    private Integer needDay;

    @ApiModelProperty("交接部件")
    @TableField(exist = false)
    private List<BuOutsourceOutinDetail> outinDetails;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
