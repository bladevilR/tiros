package org.jeecg.modules.basemanage.regu.bean;

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

/**
 * <p>
 * 规程明细指导书明细关联
 * 添加作业指导明细后，如果作业指导有物资、工器具数据，自动带入
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuRepairReguTechBookDetail对象", description = "规程明细指导书明细关联：添加作业指导明细后，如果作业指导有物资、工器具数据，自动带入")
@TableName("bu_repair_regu_techbook_detail")
public class BuRepairReguTechBookDetail extends Model<BuRepairReguTechBookDetail> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "规程明细id")
    private String reguDetailId;

    @ApiModelProperty(value = "作业指导书id")
    private String techBookId;

    @ApiModelProperty(value = "作业指导书明细id")
    private String bookDetailId;

    @ApiModelProperty(value = "作业指导书步骤序号")
    private Integer bookStepNo;

    @ApiModelProperty(value = "作业指导书步骤标题")
    private String bootStepTitle;


    @ApiModelProperty(value = "作业指导书步骤内容")
    @TableField(exist = false)
    private String bootStepContent;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
