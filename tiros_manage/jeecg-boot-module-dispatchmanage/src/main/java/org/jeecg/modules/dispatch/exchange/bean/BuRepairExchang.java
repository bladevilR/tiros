package org.jeecg.modules.dispatch.exchange.bean;

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
 * 交接车记录
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "交接车记录对象", description = "交接车记录")
@TableName("bu_repair_exchang")
public class BuRepairExchang extends Model<BuRepairExchang> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "交接类型 0接车 1交车 字典dictCode=bu_repair_exchange_tradeType    接车表示检修车间交给架修车间 交车表示架修车间交給检修车间", required = true)
    @NotNull
    @Dict(dicCode = "bu_repair_exchange_tradeType")
    private Integer tradeType;

    @ApiModelProperty(value = "线路id 字典dictCode=(bu_mtr_line,line_name,line_id)", required = true)
    @NotBlank
    private String lineId;

    @ApiModelProperty(value = "车号 字典dictCode=(bu_train_info,train_no,train_no,line_id={线路id})", required = true)
    @NotBlank
    private String trainNo;

    @ApiModelProperty(value = "修程id 字典dictCode=(bu_repair_program,name,id)", required = true)
    @NotBlank
    private String programId;

    @ApiModelProperty(value = "接修序号 表示第几列车", required = true)
    @NotNull
    private Integer trainIndex;

    @ApiModelProperty(value = "交付部门id   字典dictCode=(sys_depart,depart_name,id)")
    private String sendDeptId;

    @ApiModelProperty(value = "交付人员id   接口获取/tiros/sys/user/queryUserByDepId")
    private String sendUserId;

    @ApiModelProperty(value = "交付日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date sendDate;

    @ApiModelProperty(value = "接收部门id   字典dictCode=(sys_depart,depart_name,id)")
    private String acceptDeptId;

    @ApiModelProperty(value = "接收人员id   接口获取/tiros/sys/user/queryUserByDepId")
    private String acceptUserId;

    @ApiModelProperty(value = "接收日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date acceptDate;

    @ApiModelProperty(value = "接收公里")
    private Double acceptMileage;

    @ApiModelProperty(value = "计划完成(交车)时间 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date planFinishDate;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "状态 字典dictCode=bu_repair_exchange_status")
    @Dict(dicCode = "bu_repair_exchange_status")
    private Integer status;

    @ApiModelProperty(value = "项目序号，今年该修程的项目序号")
    private Integer itemNo;

    @ApiModelProperty(value = "车辆序号，今年的第几辆车")
    private Integer trainNumber;

    @ApiModelProperty(value = "计划模板id")
    private String planTempId;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "年计划明细id 匹配年份和车辆自动获取年计划明细")
    private String yearDetailId;

    @ApiModelProperty(value = "交车对应的接车id 当时交车时，保存为对应的接车记录id")
    private String receiveId;

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;

    @ApiModelProperty(value = "是否历史数据")
    @Dict(dicCode = "bu_state")
    private Integer historyData;


    @ApiModelProperty(value = "线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "车辆id")
    @TableField(exist = false)
    private String trainId;

    @ApiModelProperty(value = "修程名称")
    @TableField(exist = false)
    private String programName;

    @ApiModelProperty(value = "计划模板名称")
    @TableField(exist = false)
    private String planTempName;

    @ApiModelProperty(value = "修程质保天数")
    @TableField(exist = false)
    private Integer programServices;

    @ApiModelProperty(value = "交付部门名称")
    @TableField(exist = false)
    private String sendDeptName;

    @ApiModelProperty(value = "交付人员名称")
    @TableField(exist = false)
    private String sendUserName;

    @ApiModelProperty(value = "接收部门名称")
    @TableField(exist = false)
    private String acceptDeptName;

    @ApiModelProperty(value = "接收人员名称")
    @TableField(exist = false)
    private String acceptUserName;

    @ApiModelProperty(value = "审批流程状态")
    @TableField(exist = false)
    private String wfStatus;

    @ApiModelProperty(value = "流程当前处理人")
    @TableField(exist = false)
    private String processCandidate;

    @ApiModelProperty(value = "交接车开口项列表")
    @TableField(exist = false)
    private List<BuRepairExchangLeave> leaveList;

    @ApiModelProperty(value = "交接车整改项列表")
    @TableField(exist = false)
    private List<BuRepairExchangRectify> rectifyList;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
