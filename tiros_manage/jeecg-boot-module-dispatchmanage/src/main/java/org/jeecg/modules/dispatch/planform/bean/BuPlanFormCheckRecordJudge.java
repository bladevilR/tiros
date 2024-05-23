package org.jeecg.modules.dispatch.planform.bean;

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
import java.util.Date;

/**
 * <p>
 * 质量评定
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuPlanFormCheckRecordJudge对象", description = "质量评定")
@TableName("bu_pl_check_record_judge")
public class BuPlanFormCheckRecordJudge extends Model<BuPlanFormCheckRecordJudge> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "检查表实例id")
    private String checkInstId;

    @ApiModelProperty(value = "评定人员id")
    private String jdUserId;

    @ApiModelProperty(value = "评定内容")
    private String jdContent;

    @ApiModelProperty(value = "评定时间")
    private Date jdTime;


    @ApiModelProperty(value = "检查表名称")
    @TableField(exist = false)
    private String checkInstName;

    @ApiModelProperty(value = "评定人员名称")
    @TableField(exist = false)
    private String jdUserName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
