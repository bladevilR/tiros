package org.jeecg.modules.dispatch.planform.bean;

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
 * 作业检查记录表（实例）
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuPlanFormCheckRecord对象", description = "作业检查记录表（实例）")
@TableName("bu_pl_check_record")
public class BuPlanFormCheckRecord extends Model<BuPlanFormCheckRecord> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "列计划id")
    private String planId;

    @ApiModelProperty(value = "列计划表单id")
    private String planFormId;

    @ApiModelProperty(value = "表单对象id", required = true)
    @NotBlank
    private String formObjId;

    @ApiModelProperty(value = "检查表名称")
    private String title;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "设备类型id")
    private String assetTypeId;

    @ApiModelProperty(value = "车辆结构明细id")
    private String assetStructId;

    @ApiModelProperty(value = "设备id")
    private String assetId;

    @ApiModelProperty(value = "设备部件名称")
    private String assetName;

    @ApiModelProperty(value = "设备部件号")
    private String assetNo;

    @ApiModelProperty(value = "位置")
    private String location;

    @ApiModelProperty(value = "作业周期")
    private String period;

    @ApiModelProperty(value = "作业班组id")
    private String groupId;

    @ApiModelProperty(value = "作业班组名称")
    private String groupName;

    @ApiModelProperty(value = "是否已检查 字典dictCode=bu_check_status")
    @Dict(dicCode = "bu_check_status")
    private Integer status;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建班组id")
    private String createGroupId;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "表单序号")
    private Integer formIndex;


    @ApiModelProperty(value = "设备类型名称")
    @TableField(exist = false)
    private String assetTypeName;

    @ApiModelProperty(value = "车辆结构明细名称")
    @TableField(exist = false)
    private String assetStructName;

    @ApiModelProperty(value = "创建班组名称")
    @TableField(exist = false)
    private String createGroupName;

    @ApiModelProperty(value = "作业评定 检查表单实例=抽取质量评定列表拼接")
    @TableField(exist = false)
    private String judge;

    @ApiModelProperty(value = "检查项列表")
    @TableField(exist = false)
    private List<BuPlanFormCheckRecordItem> itemList;

    @ApiModelProperty(value = "质量评定列表")
    @TableField(exist = false)
    private List<BuPlanFormCheckRecordJudge> judgeList;

    @ApiModelProperty(value = "工艺文件列表")
    @TableField(exist = false)
    private List<BuWorkCheckTechLink> techLinkList;

    @ApiModelProperty(value = "检查项整改关联列表")
    @TableField(exist = false)
    private List<BuPlanFormCheckRecordRectify> rectifyList;

    @ApiModelProperty(value = "选择的填写明细")
    @TableField(exist = false)
    private String recordIds;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
