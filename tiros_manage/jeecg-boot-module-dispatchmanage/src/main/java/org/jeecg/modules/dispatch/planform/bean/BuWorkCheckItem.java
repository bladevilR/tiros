package org.jeecg.modules.dispatch.planform.bean;

import com.baomidou.mybatisplus.annotation.IdType;
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

/**
 * <p>
 * 检查项明细【模板】
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuWorkCheckItem对象", description = "")
@TableName("bu_work_check_item")
public class BuWorkCheckItem extends Model<BuWorkCheckItem> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属检查表id")
    private String checkId;

    @ApiModelProperty(value = "序号", required = true)
    @NotNull
    private Integer sortNo;

    @ApiModelProperty(value = "项点", required = true)
    @NotBlank
    private String title;

    @ApiModelProperty(value = "检查内容", required = true)
    @NotBlank
    private String content;

    @ApiModelProperty(value = "等级 数字表示几颗星", required = true)
    @NotNull
    private Integer checkLevel;

    @ApiModelProperty(value = "检查情况 “检查情况”一栏详细记录检查情况，作为模板时不需要")
    private String checkDesc;

    @ApiModelProperty(value = "结果 “结果”一栏对检查合格的画“√”不合格的画“×”；1 合格 0 不合格，作为模板时不需要")
    @Dict(dicCode = "bu_work_check_result")
    private Integer checkResult;

    @ApiModelProperty(value = "作业时间 由作业班组填写，作为模板时不需要")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date workTime;

    @ApiModelProperty(value = "检查方式 “检查方式”一栏写明检查手段，如：目视、测量、操作等，作为模板时不需要")
    @Dict(dicCode = "bu_regu_method")
    private Integer checkMethod;

    @ApiModelProperty(value = "检查人id，作为模板时不需要")
    private String checkUserId;

    @ApiModelProperty(value = "检查人名称，作为模板时不需要")
    private String checkUserName;

    @ApiModelProperty(value = "检查时间，作为模板时不需要")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkTime;

    @ApiModelProperty(value = "创建班组")
    private String createGroupId;



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
