package org.jeecg.modules.basemanage.workrecord.bean;

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
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 作业记录表明细, 一条规程项关联一条或多条作业记录明细，如果是多条表示对这条规程项的拆分
 * </p>
 *
 * @author youGen
 * @since 2020-08-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuWorkRecordDetail对象", description = "作业记录表明细, 一条规程项关联一条或多条作业记录明细，如果是多条表示对这条规程项的拆分")
@TableName("bu_work_record_detail")
public class BuWorkRecordDetail extends Model<BuWorkRecordDetail> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "作业表分类id", required = true)
    @NotBlank
    private String categoryId;

    @ApiModelProperty(value = "序号", required = true)
    @NotNull
    private Integer itemNo;

    @ApiModelProperty(value = "作业内容  长度限制256", required = true)
    @NotBlank
    private String workContent;

    @ApiModelProperty(value = "控制点分为H点和W点  字典dicCode=bu_check_level“H”点为该项工序操作过程必须经过专业工程师现场检查确认的工序，“W”点为班组长可在工序完工后确认的工序", required = true)
    @Dict(dicCode = "bu_check_level")
    @NotNull
    private Integer checkLevel;

    @ApiModelProperty(value = "技术要求  长度限制约20000", required = true)
    @NotBlank
    private String techRequire;

    @ApiModelProperty(value = "“作业情况”需根据实际情况填写。若该项工序无异常则由作业者在框中填入√；若该项工序存在异常，则将实际故障情况填入框内。作业内容中有数据要求的一并填入“作业情况”栏")
    private String workInfo;

    @ApiModelProperty(value = "是否异常 0无1有  字典dicCode=bu_abnormal  “结果”栏方框在完成该项作业及检查后，由工班长进行填写。若无异常则在方框内填入√，若有异常，工班长需对故障情况进行确认，在方框内填入×，并将故障在“备注”栏进行汇总。")
    @Dict(dicCode = "bu_abnormal")
    private Integer result;

    @ApiModelProperty(value = "自检用户id")
    private String selfCheck;

    @ApiModelProperty(value = "自检时间")
    private Date selfCheckTime;

    @ApiModelProperty(value = "互检用户id")
    private String guarderCheck;

    @ApiModelProperty(value = "互检时间")
    private Date guarderCheckTime;

    @ApiModelProperty(value = "专检用户id")
    private String monitorCheck;

    @ApiModelProperty(value = "专检时间")
    private Date monitorCheckTime;


    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;


    @ApiModelProperty(value = "自检用户名称")
    @TableField(exist = false)
    private String selfCheckName;

    @ApiModelProperty(value = "互检用户名称")
    @TableField(exist = false)
    private String guarderCheckName;

    @ApiModelProperty(value = "专检用户名称")
    @TableField(exist = false)
    private String monitorCheckName;

    @ApiModelProperty(value = "所属作业记录表id")
    @TableField(exist = false)
    private String workRecId;

    @ApiModelProperty(value = "分类序号")
    @TableField(exist = false)
    private Integer recIndex;

    @ApiModelProperty(value = "关联规程项ID")
    @TableField(exist = false)
    private String reguDetailId;

    @ApiModelProperty(value = "来自规程作业项的名字")
    @TableField(exist = false)
    private String reguTitle;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
