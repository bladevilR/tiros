package org.jeecg.modules.basemanage.workstation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import java.io.Serializable;

/**
 * <p>
 * 工位表单关联
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuBaseRefStationForm对象", description = "工位表单关联")
@TableName("bu_base_ref_station_form")
public class BuBaseRefStationForm extends Model<BuBaseRefStationForm> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "工位id")
    private String workstationId;

    @ApiModelProperty(value = "表单类型 字典dictCode=bu_work_form_type")
    @Dict(dicCode = "bu_work_form_type")
    @TableField(value = "form_by")
    private Integer formType;

    @ApiModelProperty(value = "表单对象id")
    private String formId;


    @ApiModelProperty(value = "编码")
    @TableField(exist = false)
    private String code;

    @ApiModelProperty(value = "表单名称")
    @TableField(exist = false)
    private String formName;

    @ApiModelProperty(value = "表单状态  字典dictCode=bu_valid_status")
    @Dict(dicCode = "bu_valid_status")
    @TableField(exist = false)
    private Integer status;

    @ApiModelProperty(value = "在线表单(数据记录表)类型 表单类型formBy=1在线表单(数据记录表)时的在线表单类型")
    @Dict(dicCode = "bu_form_type")
    @TableField(exist = false)
    private Integer type;

    @ApiModelProperty(value = "备注")
    @TableField(exist = false)
    private String remark;

    @ApiModelProperty(value = "所属线路")
    @TableField(exist = false)
    private String lineId;

    @ApiModelProperty(value = "规程名称")
    @TableField(exist = false)
    private String reguName;

    @ApiModelProperty(value = "规程版本")
    @TableField(exist = false)
    private String reguVersion;

    @ApiModelProperty(value = "修程ID")
    @TableField(exist = false)
    private String repairProId;
    @ApiModelProperty(value = "修程名称")
    @TableField(exist = false)
    private String repairProName;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
