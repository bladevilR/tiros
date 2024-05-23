package org.jeecg.modules.basemanage.workrecord.bean;

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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 作业记录明细分项
 * </p>
 *
 * @author youGen
 * @since 2020-09-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuWorkRecordCategory对象", description = "作业记录明细分项")
@TableName("bu_work_record_category")
public class BuWorkRecordCategory extends Model<BuWorkRecordCategory> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "作业记录表id", required = true)
    @NotBlank
    private String workRecId;

    @ApiModelProperty(value = "序号  大小限制2147483647", required = true)
    @NotNull
    private Integer recIndex;

    @ApiModelProperty(value = "名称 来自规程作业项的名字  长度限制64", required = true)
    private String reguTitle;

    @ApiModelProperty(value = "关联规程明细(作业项)id  一条规程作业项，可能对于多条作业记录明细")
    private String reguDetailId;

    @ApiModelProperty(value = "上级分项id")
    private String parentId;


    @ApiModelProperty(value = "规程名字")
    @TableField(exist = false)
    private String reguName;

    @ApiModelProperty(value = "作业明细列表")
    @TableField(exist = false)
    private List<BuWorkRecordDetail> detailList;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
