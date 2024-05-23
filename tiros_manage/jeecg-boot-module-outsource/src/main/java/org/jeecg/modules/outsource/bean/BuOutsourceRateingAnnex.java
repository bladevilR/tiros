package org.jeecg.modules.outsource.bean;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 评分附件
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="BuOutsourceRateingAnnex对象", description="评分附件")
@TableName("bu_outsource_rateing_annex")
public class BuOutsourceRateingAnnex extends Model<BuOutsourceRateingAnnex> {
    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;
    @ApiModelProperty(value = "所属评分")
    private String rateId;
    @ApiModelProperty(value = "文件名")
    private String name;
    @ApiModelProperty(value = "单位KB")
    private BigDecimal fileSize;
    @ApiModelProperty(value = "文件id")
    private String fileId;
    @ApiModelProperty(value = "文件类型", required = true)
    private String type;
    @ApiModelProperty(value = "文件路径", required = true)
    private String savepath;
    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;
    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
