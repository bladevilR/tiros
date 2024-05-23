package org.jeecg.modules.outsource.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 委外评分
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuOutsourceRateing对象", description = "委外评分")
@TableName("bu_outsource_rateing")
public class BuOutsourceRateing extends Model<BuOutsourceRateing> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "出入库明细表", required = true)
    @NotBlank
    private String contractId;

    @ApiModelProperty(value = "自定义的字典项值,字典bu_rateing_item", required = true)
    @Dict(dicCode = "bu_rateing_item")
    @NotNull
    private String rateingItem;

    @ApiModelProperty(value = "分数", required = true)
    @NotNull
    private Integer score;

    @ApiModelProperty(value = "评分日期", required = true)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private Date rateDate;

    @ApiModelProperty(value = "评分说明")
    private String rateDesc;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;


    @TableField(exist = false)
    private Integer no;

    @TableField(exist = false)
    private List<BuOutsourceRateingAnnex> annexes;

    @ApiModelProperty(value = "厂商id")
    @TableField(exist = false)
    private String supplierId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
