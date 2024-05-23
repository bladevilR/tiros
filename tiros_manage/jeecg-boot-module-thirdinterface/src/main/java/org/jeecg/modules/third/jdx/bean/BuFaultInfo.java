package org.jeecg.modules.third.jdx.bean;

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
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 故障信息
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "故障信息对象", description = "故障信息")
@TableName("bu_fault_info")
public class BuFaultInfo extends Model<BuFaultInfo> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属故障分类id  字典dictCode=(bu_fault_category,category_code,id)")
    private String categoryId;

    @ApiModelProperty(value = "所属故障代码id  字典dictCode=(bu_fault_code,fault_code,id,category_id={所属故障分类id})")
    private String faultCodeId;

    @ApiModelProperty(value = "故障编码 后端生成")
    private String faultSn;

    @ApiModelProperty(value = "故障描述", required = true)
    @NotBlank
    private String faultDesc;

    @ApiModelProperty(value = "所属系统id")
    private String sysId;

    @ApiModelProperty(value = "所属子系统id")
    private String subSysId;

    @ApiModelProperty(value = "故障部件id")
    private String faultAssetId;

    @ApiModelProperty(value = "故障发现时间 yyyy-MM-dd HH:mm")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date happenTime;

    @ApiModelProperty(value = "所属工单id  字典dictCode=(bu_work_order,order_name,id)")
    private String workOrderId;

    @ApiModelProperty(value = "所属工单任务id  字典dictCode=(bu_work_order_task,task_name,id,order_id={所属工单id})")
    private String orderTaskId;

    @ApiModelProperty(value = "所属线路id  字典dictCode=(bu_mtr_line,line_name,line_id)", required = true)
    @NotBlank
    private String lineId;

    @ApiModelProperty(value = "所属车辆编号  字典dictCode=(bu_train_info,train_no,train_no,line_id={所属线路id}", required = true)
    @NotBlank
    private String trainNo;

    @ApiModelProperty(value = "发现阶段 1架修期 2质保 3出保期   字典dictCode=bu_fault_phase", required = true)
    @NotNull
//    @Dict(dicCode = "bu_fault_phase")
    private Integer phase;

    @ApiModelProperty(value = "故障等级 1A 2B 3C 4D   字典dictCode=bu_fault_level")
//    @Dict(dicCode = "bu_fault_level")
    private Integer faultLevel;

    @ApiModelProperty(value = "是否正线   字典dictCode=bu_state")
//    @Dict(dicCode = "bu_state")
    @TableField("f_online")
    private Integer faultOnline;

    @ApiModelProperty(value = "是否有责   字典dictCode=bu_state")
//    @Dict(dicCode = "bu_state")
    private Integer hasDuty;

    @ApiModelProperty(value = "是否委外   字典dictCode=bu_state")
//    @Dict(dicCode = "bu_state")
    private Integer outsource;

    @ApiModelProperty(value = "提报班组id  字典dictCode=(bu_mtr_workshop_group,group_name,id)", required = true)
    @NotBlank
    private String reportGroupId;

    @ApiModelProperty(value = "提报人员id  接口获取/tiros/sys/user/listByGroupId  app端接口/tiros/app/user/listByGroupId", required = true)
    @NotBlank
    private String reportUserId;

    @ApiModelProperty(value = "提报日期 yyyy-MM-dd HH:mm")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date reportTime;

    @ApiModelProperty(value = "责任工程师id  字典dictCode=(sys_user,realname,id)")
    private String dutyEngineer;

    @ApiModelProperty(value = "故障状态 字典dictCode=bu_fault_status", required = true)
    @NotNull
//    @Dict(dicCode = "bu_fault_status")
    private Integer status;

    @ApiModelProperty(value = "关闭人员id  字典dictCode=(sys_user,realname,id)")
    private String closeUserId;

    @ApiModelProperty(value = "关闭时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date closeTime;

    @ApiModelProperty(value = "提交状态 字典dictCode=bu_fault_submit_status", required = true)
    @NotNull
//    @Dict(dicCode = "bu_fault_submit_status")
    private Integer submitStatus;

    @ApiModelProperty(value = "工位id 如果是从工单中创建故障默认将工单任务所属工位带入")
    private String workStationId;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "创建类型 字典dictCode=bu_fault_create_type 1调度创建2工班创建")
//    @Dict(dicCode = "bu_fault_create_type")
    private Integer createType;

    @ApiModelProperty(value = "车辆结构明细id")
    private String trainStructureId;

    @ApiModelProperty(value = "处理状态 ", notes = "1=设备正常，2=已修复，2=临时修复，3=未修复")
//    @Dict(dicCode = "bu_handlestatus")
    private Integer handleStatus;

    @ApiModelProperty(value = "处理工单id")
    private String handleOrderId;

    @ApiModelProperty(value = "委外厂商id")
    private String outSupplierId;

    @ApiModelProperty(value = "数据值记录id", notes = "来自bu_pl_data_record_value表的id，当前保存故障时，如果该字段有值，则需要将bu_pl_data_record_value表中id为该值得记录状态改成2")
    private String formValueId;

    @ApiModelProperty(value = "列计划id")
    private String planId;

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;

    @ApiModelProperty(value = "跟踪信息")
    private String trackMessage;

    @ApiModelProperty(value = "故障来源 1手动创建 2导入历史故障 3同步历史质保故障")
    private Integer fromType;


    @ApiModelProperty(value = "所属故障分类编码")
    @TableField(exist = false)
    private String categoryCode;
    @ApiModelProperty(value = "所属故障分类描述")
    @TableField(exist = false)
    private String categoryCodeDes;

    @ApiModelProperty(value = "所属故障代码编码")
    @TableField(exist = false)
    private String faultCodeCode;
    @ApiModelProperty(value = "所属故障代码描述")
    @TableField(exist = false)
    private String faultCodeCodeDes;

    @ApiModelProperty(value = "所属线路编码")
    @TableField(exist = false)
    private String lineNum;

    @ApiModelProperty(value = "所属线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "所属系统名称")
    @TableField(exist = false)
    private String sysName;

    @ApiModelProperty(value = "所属子系统名称")
    @TableField(exist = false)
    private String subSysName;

    @ApiModelProperty(value = "车辆结构明细名称")
    @TableField(exist = false)
    private String trainStructureName;

    @ApiModelProperty(value = "故障部件编码")
    @TableField(exist = false)
    private String faultAssetCode;

    @ApiModelProperty(value = "故障部件名称")
    @TableField(exist = false)
    private String faultAssetName;

    @ApiModelProperty(value = "提报班组名称")
    @TableField(exist = false)
    private String reportGroupName;

    @ApiModelProperty(value = "提报人员名称")
    @TableField(exist = false)
    private String reportUserName;

    @ApiModelProperty(value = "提报人员工号")
    @TableField(exist = false)
    private String reportUserWorkNo;

    @ApiModelProperty(value = "提报人员名称")
    @TableField(exist = false)
    private String reportUserPhone;

    @ApiModelProperty(value = "责任工程师名称")
    @TableField(exist = false)
    private String dutyEngineerName;

    @ApiModelProperty(value = "关闭人员名称")
    @TableField(exist = false)
    private String closeUserName;

    @ApiModelProperty(value = "委外厂商名称")
    @TableField(exist = false)
    private String outSupplierName;

    @ApiModelProperty(value = "列计划名称")
    @TableField(exist = false)
    private String planName;

    @ApiModelProperty(value = "处理记录列表")
    @TableField(exist = false)
    private List<BuFaultHandleRecord> handleRecordList;
//
//    @ApiModelProperty(value = "附件列表")
//    @TableField(exist = false)
//    private List<BuFaultInfoAnnex> annexList;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
