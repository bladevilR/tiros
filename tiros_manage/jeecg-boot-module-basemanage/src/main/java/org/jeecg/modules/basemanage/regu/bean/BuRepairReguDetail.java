package org.jeecg.modules.basemanage.regu.bean;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 规程内容, 注意：类型不同界面表单输入的不同
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bu_repair_regu_detail")
public class BuRepairReguDetail extends Model<BuRepairReguDetail> {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属规程信息ID")
    private String reguId;

    @ApiModelProperty(value = "序号，在同级别中的序号，自动生成，也可以手动输入")
    private String no;

    @ApiModelProperty(value = "名称")
    private String title;

    @ApiModelProperty(value = "类型  字典dictCode=bu_regu_type")
    @Dict(dicCode = "bu_regu_type")
    private Integer type;

    @ApiModelProperty(value = "安全提示")
    private String safeNotice;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "上级id 为null表示没有上级，为顶级，作业分类的上级id不能是作业项")
    private String parentId;

    @ApiModelProperty(value = "是否委外 字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    @TableField(strategy = FieldStrategy.IGNORED)
    private Integer outsource;

    @ApiModelProperty(value = "是否重要工序 字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer important;

    @ApiModelProperty(value = "维保手段  字典dictCode=bu_regu_method")
    @Dict(dicCode = "bu_regu_method")
    @TableField(strategy = FieldStrategy.IGNORED)
    private String method;

    @ApiModelProperty(value = "质量等级 字典dictCode=bu_regu_quality_level")
    @Dict(dicCode = "bu_regu_quality_level")
    @TableField(strategy = FieldStrategy.IGNORED)
    private String qualityLevel;

    @ApiModelProperty(value = "设备类型id")
    private String assetTypeId;

    @ApiModelProperty(value = "单位小时，可以小数")
    private Float workTime;

    @ApiModelProperty(value = "技术要求")
    private String requirement;

    @ApiModelProperty(value = "是否是必换件 0否1是  字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer mustReplace;

    @ApiModelProperty(value = "该结构是否需要测量 0否1是  字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer measure;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "是否委外2 字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer outsource2;


    @ApiModelProperty(value = "设备名称")
    @TableField(exist = false)
    private String assetTypeName;

    @ApiModelProperty(value = "是否有子结点")
    @TableField(exist = false)
    private Boolean hasChildren;

    @ApiModelProperty(value = "子结点")
    @TableField(exist = false)
    List<BuRepairReguDetail> children;

    @ApiModelProperty(value = "上级节点名称")
    @TableField(exist = false)
    private String parentName;

    @ApiModelProperty(value = "wbs")
    @TableField(exist = false)
    private String wbs;

    @ApiModelProperty(value = "计划模板已关联")
    @Dict(dicCode = "bu_state")
    @TableField(exist = false)
    private Integer tpPlanRelated;

    @ApiModelProperty(value = "计划模板已关联比例")
    @TableField(exist = false)
    private String tpPlanRelatedInfo;

    @ApiModelProperty(value = "关联工艺文件")
    @TableField(exist = false)
    private List<BuRepairReguTechFile> reguTechFiles;

    @ApiModelProperty(value = "额定物料")
    @TableField(exist = false)
    private List<BuRepairReguMaterial> reguMaterials;

    @ApiModelProperty(value = "需人员")
    @TableField(exist = false)
    private List<BuRepairReguPerson> reguPersons;

    @ApiModelProperty(value = "所需工器具")
    @TableField(exist = false)
    private List<BuRepairReguTools> reguTools;

    @ApiModelProperty(value = "关联指导书明细")
    @TableField(exist = false)
    private List<BuRepairReguTechBookDetail> techBookDetails;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
