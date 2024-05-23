package org.jeecg.modules.quality.recordconfirm.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 列计划作业记录明细结果
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuPlanFormWorkRecordDetail对象", description = "列计划作业记录明细结果")
@TableName("bu_pl_work_record_detail")
public class BuPlanFormWorkRecordDetail extends Model<BuPlanFormWorkRecordDetail> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属表单实列id 来自列计划表单实列(作业记录表)id", required = true)
    @NotBlank
    private String workRecordId;

    @ApiModelProperty(value = "所属作业记录明细id 来自作业记录表明细id", required = true)
    @NotBlank
    private String workRecDetailId;

    @ApiModelProperty(value = "规程明细id", required = true)
    @NotBlank
    private String reguDetailId;

    @ApiModelProperty(value = "作业情况 需根据实际情况填写。若该项工序无异常则由作业者在框中填入√；若该项工序存在异常，则将实际故障情况填入框内。作业内容中有数据要求的一并填入“作业情况”栏")
    private String workInfo;

    @ApiModelProperty(value = "结果 是否异常0无1有， “结果”栏方框在完成该项作业及检查后，由工班长进行填写。若无异常则在方框内填入√，若有异常，工班长需对故障情况进行确认，在方框内填入×，并将故障在“备注”栏进行汇总。")
    @Dict(dicCode = "bu_work_order_record_detail_result")
    private Integer result;

    @ApiModelProperty(value = "是否忽略填写 0否1是 字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer isIgnore;

    @ApiModelProperty(value = "忽略原因")
    private String ignoreDesc;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;


    @ApiModelProperty(value = "作业明细分类-id")
    @TableField(exist = false)
    private String categoryId;

    @ApiModelProperty(value = "作业明细分类-序号")
    @TableField(exist = false)
    private Integer recIndex;

    @ApiModelProperty(value = "作业明细分类-名称")
    @TableField(exist = false)
    private String reguTitle;

    @ApiModelProperty(value = "序号")
    @TableField(exist = false)
    private Integer itemNo;

    @ApiModelProperty(value = "控制点分为H点和W点  “H”点为该项工序操作过程必须经过专业工程师现场检查确认的工序，“W”点为班组长可在工序完工后确认的工序")
    @Dict(dicCode = "bu_check_level")
    @TableField(exist = false)
    private Integer checkLevel;

    @ApiModelProperty(value = "技术要求")
    @TableField(exist = false)
    private String techRequire;

    @ApiModelProperty(value = "作业内容")
    @TableField(exist = false)
    private String workContent;

    @ApiModelProperty(value = "自检用户")
    @TableField(exist = false)
    private String selfCheck;

    @ApiModelProperty(value = "互检用户")
    @TableField(exist = false)
    private String guarderCheck;

    @ApiModelProperty(value = "专检用户")
    @TableField(exist = false)
    private String monitorCheck;

    @ApiModelProperty(value = "专工验收")
    @TableField(exist = false)
    private String monitorAcceptance;

    @ApiModelProperty(value = "检查结果列表")
    @TableField(exist = false)
    private List<BuPlanFormWorkRecordChecks> checksList;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
