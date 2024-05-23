package org.jeecg.modules.outsource.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author yyg
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuOutsourceOutinQuality对象", description = "委外部件质保期")
@TableName("bu_outsource_outin_quality")
public class BuOutsourceOutinQuality extends Model<BuOutsourceOutinQuality> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "委外部件详情id", required = true)
    @NotBlank
    private String detailId;

    @ApiModelProperty(value = "部件编号")
    private String assetNo;

    @ApiModelProperty(value = "部件名")
    private String assetName;

    @ApiModelProperty(value = "系统id")
    private String systemId;

    @ApiModelProperty(value = "数量", required = true)
    @NotNull
    private Integer amount;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "周转件id")
    private String turnoverAssetId;

    @ApiModelProperty(value = "验收日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date checkDate;

    @ApiModelProperty(value = "质保开始日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @ApiModelProperty(value = "质保结束日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @ApiModelProperty(value = "质保天数")
    private Integer dayCount;


    @ApiModelProperty(value = "合同id")
    @TableField(exist = false)
    private String contractId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
