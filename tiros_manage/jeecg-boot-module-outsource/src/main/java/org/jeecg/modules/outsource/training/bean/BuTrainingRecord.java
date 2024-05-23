package org.jeecg.modules.outsource.training.bean;

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
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author youGen
 * @since 2021-05-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("")
@ApiModel(value = "BuTrainingRecord对象", description = "")
public class BuTrainingRecord extends Model<BuTrainingRecord> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("合同id")
    private String contractId;

    @ApiModelProperty("培训厂商")
    private String supplierName;

    @ApiModelProperty(value = "培训标题", required = true)
    @NotBlank
    private String title;

    @ApiModelProperty(value = "培训开始时间 yyyy-MM-dd")
    @NotNull
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @ApiModelProperty(value = "培训开始时间结束,自动用开始日期+培训天数计算")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date finishDate;

    @ApiModelProperty(value = "培训天数", required = true)
    @NotNull
    private Integer days;

    @ApiModelProperty(value = "培训人次，从培训人员自动统计 ")
    private Integer manTimes;

    @ApiModelProperty(value = "备注")
    private String remark;

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

    @ApiModelProperty(value = "线路id")
    private String lineId;


    @ApiModelProperty(value = "合同名称")
    @TableField(exist = false)
    private String contractName;

    @ApiModelProperty(value = "合同编码")
    @TableField(exist = false)
    private String contractNo;

    @ApiModelProperty(value = "培训名单")
    @TableField(exist = false)
    private String trainingUsers;

    @ApiModelProperty(value = "培训人员列表")
    @TableField(exist = false)
    private List<BuTrainingUsers> trainingUsersList;

    @ApiModelProperty(value = "附件")
    @TableField(exist = false)
    private List<BuTrainingAnnex> trainingAnnexList;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
