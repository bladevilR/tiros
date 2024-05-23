package org.jeecg.modules.basemanage.workcheck.bean;

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

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 作业检查记录表检查项明细【模板】
 * </p>
 *
 * @author youGen
 * @since 2021-05-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuWorkCheckItem对象", description = "作业检查记录表检查项明细【模板】")
@TableName("bu_work_check_item")
public class BuWorkCheckItem extends Model<BuWorkCheckItem> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属检查表id")
    private String checkId;

    @ApiModelProperty(value = "序号", required = true)
    private Integer sortNo;

    @ApiModelProperty(value = "项点", required = true)
    private String title;

    @ApiModelProperty(value = "检查内容", required = true)
    private String content;

    @ApiModelProperty(value = "等级，数字表示几颗星", required = true)
    private Integer checkLevel;

    @ApiModelProperty(value = "“检查情况”一栏详细记录检查情况，作为模板时不需要")
    private String checkDesc;

    @ApiModelProperty(value = "“结果”一栏对检查合格的画“√”不合格的画“×”；1 合格 0 不合格，，作为模板时不需要,字典 bu_work_check_result")
    @Dict(dicCode = "bu_work_check_result")
    private Integer checkResult;

    @ApiModelProperty(value = "作业时间由作业班组填写， ，作为模板时不需要")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date workTime;

    @ApiModelProperty(value = "“检查方式”一栏写明检查手段，如：目视、测量、操作等。1 目视 2 测量 3 操作。，作为模板时不需要,字典bu_regu_method")
    @Dict(dicCode = "bu_regu_method")
    private Integer checkMethod;

    @ApiModelProperty(value = "检查人id，作为模板时不需要")
    private String checkUserId;

    @ApiModelProperty(value = "检查人名称，作为模板时不需要")
    private String checkUserName;

    @ApiModelProperty(value = "检查时间，作为模板时不需要")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date checkTime;

    @ApiModelProperty(value = "创建班组id")
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
