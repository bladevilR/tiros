package org.jeecg.modules.basemanage.holiday.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
 * 节假日信息表，用于存放节假日信息，包括要上班的特殊日期
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bu_base_holiday")
public class BuBaseHoliday implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "年份", required = true)
    @NotNull
    private Integer year;

    @ApiModelProperty(value = "节日名称  长度限制20", required = true)
    @NotBlank
    private String name;

    @ApiModelProperty(value = "开始时间", example = "2020-02-02", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @NotNull
    private Date startTime;

    @ApiModelProperty(value = "结束时间", example = "2020-02-02", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @NotNull
    private Date end;

    @ApiModelProperty(value = "天数")
    private Integer days;

    @ApiModelProperty(value = "类型  字典dictCode=bu_day_type", required = true)
    @Dict(dicCode = "bu_day_type")
    @NotNull
    private Integer work;

    @ApiModelProperty(value = "备注  长度限制200")
    private String remark;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

}
