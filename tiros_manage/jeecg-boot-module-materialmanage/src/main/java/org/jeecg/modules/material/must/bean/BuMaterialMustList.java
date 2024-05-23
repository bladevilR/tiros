package org.jeecg.modules.material.must.bean;

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

/**
 * <p>
 *
 * </p>
 *
 * @author youGen
 * @since 2021-04-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuMaterialMustList对象", description = "必换件清单")
@TableName("bu_material_must_list")
public class BuMaterialMustList extends Model<BuMaterialMustList> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty(value = "物资类型id", required = true)
    @NotBlank
    private String materialTypeId;

    @ApiModelProperty(value = "线路id", required = true)
    @NotBlank
    private String lineId;

    @ApiModelProperty(value = "修程id", required = true)
    @NotBlank
    private String repairProgramId;

    @ApiModelProperty("系统id")
    private String sysId;

    @ApiModelProperty("班组id")
    private String groupId;

    @ApiModelProperty("工位id")
    private String workstationId;

    @ApiModelProperty("每列所需")
    private Double needAmount;

    @ApiModelProperty(value = "选择车辆结构中的一个节点")
    private String location;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("替换件编码")
    private String canReplace;

    @ApiModelProperty("安装部位")
    private String locationDesc;

    @ApiModelProperty(value = "状态 字典dictCode=bu_valid_status 0无效1有效")
    @Dict(dicCode = "bu_valid_status")
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


    @ApiModelProperty("物资名称")
    @TableField(exist = false)
    private String name;

    @ApiModelProperty("物资编码")
    @TableField(exist = false)
    private String code;

    @ApiModelProperty("物资描述")
    @TableField(exist = false)
    private String spec;

    @ApiModelProperty("物资单位")
    @TableField(exist = false)
    private String unit;

    @ApiModelProperty("物资属性,字典bu_material_attr")
    @TableField(exist = false)
    @Dict(dicCode = "bu_material_attr")
    private String materialTypeCategory;

    @ApiModelProperty("线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty("修程名称")
    @TableField(exist = false)
    private String repairProgramName;

    @ApiModelProperty("系统")
    @TableField(exist = false)
    private String sysName;

    @ApiModelProperty("班组名称")
    @TableField(exist = false)
    private String groupName;

    @ApiModelProperty("工位号")
    @TableField(exist = false)
    private String workstationNo;

    @ApiModelProperty("工位名称")
    @TableField(exist = false)
    private String workstationName;

    @ApiModelProperty(value = "安装部位")
    @TableField(exist = false)
    private String locationName;

    @ApiModelProperty(value = "当前库存")
    @TableField(exist = false)
    private Double amount;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
