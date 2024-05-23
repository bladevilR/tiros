package org.jeecg.modules.quality.measurethreshold.bean;

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
 * 数据记录表模版信息
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "数据记录表模版信息对象", description = "数据记录表模版信息")
@TableName("bu_base_form_template")
public class BuBaseFormTemplate extends Model<BuBaseFormTemplate> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "编码")
    private String code;

    @ApiModelProperty(value = "表单名称", required = true)
    @NotBlank
    private String name;

    @ApiModelProperty(value = "所属分类", required = true)
    @NotBlank
    private String category;

    @ApiModelProperty(value = "有效状态  字典dictCode=bu_valid_status")
    @Dict(dicCode = "bu_valid_status")
    private Integer status;

    @ApiModelProperty(value = "表单类型 字典dictCode=bu_form_type", required = true)
    @NotNull
    @Dict(dicCode = "bu_form_type")
    private Integer type;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "模版内容")
    private String content;

    @ApiModelProperty(value = "数据模版 模版中的数据项，如：[{id:xxxx,name:xxxx,element:'text/select/....',value:xxxx},......]")
    private String datajson;

    @ApiModelProperty(value = "设备类型id")
    private String assetTypeId;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "线路id")
    private String lineId;

    @ApiModelProperty(value = "修程id")
    private String repairProgramId;

    @ApiModelProperty(value = "创建班组id")
    private String createGroupId;

    @ApiModelProperty(value = "规程id")
    private String reguId;

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;


    @ApiModelProperty(value = "是否已设置测量阈值 0否1是")
    @TableField(exist = false)
    private Integer thresholdStatus;

    @ApiModelProperty(value = "所属分类名称")
    @TableField(exist = false)
    private String categoryName;

    @ApiModelProperty(value = "设备类型名称")
    @TableField(exist = false)
    private String assetTypeName;

    @ApiModelProperty(value = "线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "创建班组名称")
    @TableField(exist = false)
    private String createGroupName;

    @ApiModelProperty(value = "规程名称")
    @TableField(exist = false)
    private String reguName;

    @ApiModelProperty(value = "规程版本")
    @TableField(exist = false)
    private String reguVersion;

    @ApiModelProperty(value = "修程名称")
    @TableField(exist = false)
    private String repairProgramName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
