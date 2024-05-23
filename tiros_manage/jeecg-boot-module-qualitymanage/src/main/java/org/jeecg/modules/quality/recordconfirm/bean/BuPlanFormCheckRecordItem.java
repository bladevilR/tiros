package org.jeecg.modules.quality.recordconfirm.bean;

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

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 作业检查项明细(实例)
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuPlanFormCheckRecordItem对象", description = "作业检查项明细(实例)")
@TableName("bu_pl_check_record_item")
public class BuPlanFormCheckRecordItem extends Model<BuPlanFormCheckRecordItem> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属检查表id")
    private String checkId;

    @ApiModelProperty(value = "序号")
    private Integer sortNo;

    @ApiModelProperty(value = "项点")
    private String title;

    @ApiModelProperty(value = "检查内容")
    private String content;

    @ApiModelProperty(value = "等级 数字表示几颗星")
    private Integer checkLevel;

    @ApiModelProperty(value = "检查情况 “检查情况”一栏详细记录检查情况")
    private String checkDesc;

    @ApiModelProperty(value = "结果 “结果”一栏对检查合格的画“√”不合格的画“×”；1合格 0不合格")
    @Dict(dicCode = "bu_work_check_result")
    private Integer checkResult;

    @ApiModelProperty(value = "作业时间 由作业班组填写")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date workTime;

    @ApiModelProperty(value = "检查方式 “检查方式”一栏写明检查手段，如：目视、测量、操作等")
    @Dict(dicCode = "bu_regu_method")
    private Integer checkMethod;

    @ApiModelProperty(value = "检查人id")
    private String checkUserId;

    @ApiModelProperty(value = "检查人名称")
    private String checkUserName;

    @ApiModelProperty(value = "检查时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkTime;

    @ApiModelProperty(value = "创建班组id")
    private String createGroupId;

    @ApiModelProperty(value = "模板明细id")
    private String  checkDetailId;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;


    @ApiModelProperty(value = "创建班组名称")
    @TableField(exist = false)
    private String createGroupName;

    @ApiModelProperty(value = "检查项列表")
    @TableField(exist = false)
    private List<BuPlanFormCheckRecordRectify> rectifyList;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
