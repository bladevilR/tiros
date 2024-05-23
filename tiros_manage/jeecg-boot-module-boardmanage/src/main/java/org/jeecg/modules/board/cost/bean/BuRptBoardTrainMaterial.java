package org.jeecg.modules.board.cost.bean;

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

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 物料成本统计-中间表-车辆维度
 * 以车辆为基本单位统计，每次完成列计划自动放入
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuRptBoardTrainMaterial对象", description = "以车辆为基本单位统计，每次完成列计划自动放入")
@TableName("bu_rpt_board_train_material")
public class BuRptBoardTrainMaterial extends Model<BuRptBoardTrainMaterial> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属车辆段id")
    private String depotId;

    @ApiModelProperty(value = "所属车间id")
    private String workshopId;

    @ApiModelProperty(value = "所属线路id")
    private String lineId;

    @ApiModelProperty(value = "所属周期id  来自架修周期记录表的id，获取对应车辆的最后一条记录id")
    private String repairPeriod;

    @ApiModelProperty(value = "所属车辆id")
    private String trainId;

    @ApiModelProperty(value = "车辆号")
    private String trainNo;

    @ApiModelProperty(value = "架修序号")
    private Integer repairIndex;

    @ApiModelProperty(value = "年份")
    private Integer year;

    @ApiModelProperty(value = "月份 这里为列计划的完成时间")
    private Integer month;

    @ApiModelProperty(value = "修程id")
    private String programId;

    @ApiModelProperty(value = "必换件成本")
    private BigDecimal mustCost;

    @ApiModelProperty(value = "偶换件成本")
    private BigDecimal randomCost;

    @ApiModelProperty(value = "耗材成本")
    private BigDecimal consumeCost;

    @ApiModelProperty(value = "公司id")
    private String companyId;


    @ApiModelProperty(value = "总成本")
    @TableField(exist = false)
    private BigDecimal totalCost;

    @ApiModelProperty(value = "所属车辆段名称")
    @TableField(exist = false)
    private String depotName;

    @ApiModelProperty(value = "修程名称")
    @TableField(exist = false)
    private String programName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
