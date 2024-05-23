package org.jeecg.modules.basemanage.layout.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 桌面部件
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SysWidgets对象", description = "桌面部件")
@TableName("sys_widgets")
public class SysWidgets extends Model<SysWidgets> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属分类id 来自系统分类表sys_category")
    private String widgetCategory;

    @ApiModelProperty(value = "部件名称", required = true)
    @NotBlank
    private String widgetName;

    @ApiModelProperty(value = "部件描述")
    private String widgetDesc;

    @ApiModelProperty(value = "组件路径", required = true)
    @NotBlank
    private String componentPath;

    @ApiModelProperty(value = "默认宽度 页面总宽度100")
    private Integer defWidth;

    @ApiModelProperty(value = "默认高度 实际显示为设置的数X20个像素高度")
    private Integer defHeight;

    @ApiModelProperty(value = "状态 字典dictCode=bu_valid_status 0无效1有效")
    @Dict(dicCode = "bu_valid_status")
    private Integer status;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;


    @ApiModelProperty(value = "所属分类名称")
    @TableField(exist = false)
    private String widgetCategoryName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
