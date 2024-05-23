package org.jeecg.modules.dispatch.plan.bean;

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

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>
 * 远期规划明细
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "远期规划明细对象", description = "远期规划明细")
@TableName("bu_repair_plan_far_detail")
public class BuRepairPlanFarDetail extends Model<BuRepairPlanFarDetail> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "远期规划id")
    private String longPlanId;

    @ApiModelProperty(value = "计划明细标题", required = true)
    @NotBlank
    private String title;

    @ApiModelProperty(value = "线路id   字典dictCode=(bu_mtr_line,line_name,line_id)", required = true)
    @NotBlank
    private String lineId;

    @ApiModelProperty(value = "开始年到结束年每年的架修数量，组成格式：2020:0#2021:5#2022:5... 冒号分隔年份和该年架修数量，#分隔不同年份，必须每年都设置")
    private String middleRepair;

    @ApiModelProperty(value = "开始年到结束年每年的大修数量，组成格式：2020:0#2021:5#2022:5... 冒号分隔年份和该年大修数量，#分隔不同年份，必须每年都设置")
    private String hightRepair;

    @ApiModelProperty(value = "线路名称")
    @TableField(exist = false)
    private String lineName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
