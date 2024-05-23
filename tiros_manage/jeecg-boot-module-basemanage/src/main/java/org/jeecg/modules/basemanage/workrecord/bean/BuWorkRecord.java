package org.jeecg.modules.basemanage.workrecord.bean;

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

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 作业记录表
 * </p>
 *
 * @author youGen
 * @since 2020-08-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuWorkRecord对象", description = "作业记录表")
@TableName("bu_work_record")
public class BuWorkRecord extends Model<BuWorkRecord> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "编码  长度限制16")
    private String code;

    @ApiModelProperty(value = "记录表名称  长度限制64", required = true)
    @NotBlank
    private String title;

    @ApiModelProperty(value = "所属线路id  字典dictCode=(bu_mtr_line,line_name,line_id)", required = true)
    @NotBlank
    private String lineId;

    @ApiModelProperty(value = "所属修程id 字典dictCode=(bu_repair_program,name,id)", required = true)
    @NotBlank
    private String repairProId;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "作业时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date workDate;

    @ApiModelProperty(value = "完工时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date finishDate;

    @ApiModelProperty(value = "作业班组")
    private String workGroupId;

    @ApiModelProperty(value = "备注  长度限制256")
    private String remark;

    @ApiModelProperty(value = "0 无效  1 有效")
    @Dict(dicCode = "bu_valid_status")
    private Integer status;

    @ApiModelProperty(value = "0 未通过  1 通过")
    @Dict(dicCode = "bu_check_result")
    private Integer checkResult;

    @ApiModelProperty(value = "检查日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date checkDate;

    @ApiModelProperty(value = "质量负责人")
    private String checkUserId;

    @ApiModelProperty(value = "设备类型id")
    private String assetTypeId;

    @ApiModelProperty(value = "是否拆装  字典dicCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer updown;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "创建班组id")
    private String createGroupId;

    @ApiModelProperty(value = "规程id")
    private String reguId;


    @ApiModelProperty(value = "所属线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "所属修程名称")
    @TableField(exist = false)
    private String repairProName;

    @ApiModelProperty(value = "作业班组名称")
    @TableField(exist = false)
    private String workGroupName;

    @ApiModelProperty(value = "设备类型名称")
    @TableField(exist = false)
    private String assetTypeName;

    @ApiModelProperty(value = "创建班组名称")
    @TableField(exist = false)
    private String createGroupName;

    @ApiModelProperty(value = "规程名称")
    @TableField(exist = false)
    private String reguName;

    @ApiModelProperty(value = "规程版本")
    @TableField(exist = false)
    private String reguVersion;

    @ApiModelProperty(value = "作业明细分类列表")
    @TableField(exist = false)
    private List<BuWorkRecordCategory> categoryList;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
