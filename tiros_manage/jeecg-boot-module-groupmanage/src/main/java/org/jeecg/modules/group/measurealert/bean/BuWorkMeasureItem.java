package org.jeecg.modules.group.measurealert.bean;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * 测量项定义表
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuWorkMeasureItem对象", description = "")
@TableName("bu_work_measure_item")
public class BuWorkMeasureItem extends Model<BuWorkMeasureItem> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "在线表单(数据记录表)id", required = true)
    @NotBlank
    private String customId;

    @ApiModelProperty(value = "测量项", required = true)
    @NotBlank
    private String itemName;

    @ApiModelProperty(value = "对应着数据记录表中的数据输入项，如果是excel表单，则是行列表示，如：$1$1 表第1行第1列，$1$1:$4$3，表示第1行1列到4行3列的区域都关联这个值，如果是非excel表单，则表示对应的字段名称，如：xxxx，则表示为xxxx字段")
    private String linkDomain;

    @ApiModelProperty(value = "操作符 1等于2小于3小于等于4大于5大于等于6不等于  字典dictCode=bu_operator", required = true)
    @NotNull
    @Dict(dicCode = "bu_operator")
    private Integer operator;

    @ApiModelProperty(value = "阈值", required = true)
    @NotNull
    private Double threshold;

    @ApiModelProperty(value = "提示模版 TC1-一端位 BCo 测量值为 {{value}}，超过预警值:xxx")
    private String template;

    @ApiModelProperty(value = "设备类型id 测量的设备类型，如果为空则不限制，只要是该表单该值都预警，如果设置了只有是这种设备类型的才预警")
    private String assetTypeId;

    @ApiModelProperty(value = "厂商id 对应的设备厂商，如果为空则不限制，如果有厂商则必须有设备类型，只有该厂商的该设备类型才预警")
    private String supplierId;

    @ApiModelProperty(value = "是否预警 0否1是  字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer genAlert;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "row1")
    private Integer row1;

    @ApiModelProperty(value = "row2")
    private Integer row2;

    @ApiModelProperty(value = "col1")
    private Integer col1;

    @ApiModelProperty(value = "col2")
    private Integer col2;

    @ApiModelProperty(value = "操作符2")
    @Dict(dicCode = "bu_operator")
    private Integer operator2;

    @ApiModelProperty(value = "阈值2")
    private Double threshold2;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
