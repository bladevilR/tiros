package org.jeecg.modules.basemanage.jobguidebook.bean;

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

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuJobGuideBookDetail对象", description = "作业指导书明细")
@TableName("bu_job_guide_book_detail")
public class BuJobGuideBookDetail extends Model<BuJobGuideBookDetail> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属指导书id")
    private String bookId;

    @ApiModelProperty(value = "步骤序号", required = true)
    @NotNull
    private Integer stepNum;

    @ApiModelProperty(value = "标题", required = true)
    @NotBlank
    private String stepTitle;

    @ApiModelProperty(value = "内容")
    private String stepContent;

    @ApiModelProperty(value = "步骤物资列表")
    @TableField(exist = false)
    private List<BuJobGuideBookMaterial> materialList;

    @ApiModelProperty(value = "步骤工器具列表")
    @TableField(exist = false)
    private List<BuJobGuideBookTools> toolList;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
