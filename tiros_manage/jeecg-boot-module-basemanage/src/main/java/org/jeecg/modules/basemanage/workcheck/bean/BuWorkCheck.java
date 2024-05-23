package org.jeecg.modules.basemanage.workcheck.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Table;
import org.jeecg.common.aspect.annotation.Dict;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 *
 * </p>
 *
 * @author youGen
 * @since 2021-05-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuWorkCheck对象", description = "")
@TableName("bu_work_check")
public class BuWorkCheck extends Model<BuWorkCheck> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "检查表名称", required = true)
    @NotBlank
    private String title;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "设备类型id", required = true)
    private String assetTypeId;

    @ApiModelProperty(value = "车辆结构id")
    private String assetStructId;

    @ApiModelProperty(value = "设备id")
    private String assetId;

    @ApiModelProperty(value = "部件名称")
    private String assetName;

    @ApiModelProperty(value = "部件编号")
    private String assetNo;

    @ApiModelProperty(value = "作业位置")
    private String location;

    @ApiModelProperty(value = "作业周期")
    private String period;

    @ApiModelProperty(value = "作业班组id")
    private String groupId;

    @ApiModelProperty(value = "班组名称")
    private String groupName;

    @ApiModelProperty(value = "编码")
    private String code;

    @ApiModelProperty(value = "作业评定")
    private String judge;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建班组id")
    private String createGroupId;

    @ApiModelProperty(value = "线路id", required = true)
    @NotBlank
    private String lineId;

    @ApiModelProperty(value = "修程id", required = true)
    @NotBlank
    private String repairProId;

    @ApiModelProperty(value = "是否有效，字典bu_effective")
    @Dict(dicCode = "bu_effective")
    private Integer status;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;


    @ApiModelProperty(value = "表单类型  字典dictCode=bu_work_form_type")
    @Dict(dicCode = "bu_work_form_type")
    @TableField(exist = false)
    private Integer formType;

    @ApiModelProperty(value = "作业记录表类型 0=不是作业记录表 1=老版作业记录表 2=新版excel作业记录表")
    @TableField(exist = false)
    private Integer workRecordType;

    @ApiModelProperty(value = "线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "修程名")
    @TableField(exist = false)
    private String repairProName;

    @ApiModelProperty(value = "设备类型名称")
    @TableField(exist = false)
    private String assetTypeName;

    @ApiModelProperty(value = "检查项列表")
    @TableField(exist = false)
    private List<BuWorkCheckItem> itemList;

    @ApiModelProperty(value = "工艺文件列表")
    @TableField(exist = false)
    private List<BuWorkCheckTechLink> techLinkList;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
