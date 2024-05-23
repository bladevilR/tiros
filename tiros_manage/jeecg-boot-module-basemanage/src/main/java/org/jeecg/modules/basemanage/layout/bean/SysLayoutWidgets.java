package org.jeecg.modules.basemanage.layout.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 布局组件
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SysLayoutWidgets对象", description = "布局组件")
@TableName("sys_layout_widgets")
public class SysLayoutWidgets extends Model<SysLayoutWidgets> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属界面布局id", required = true)
    @NotBlank
    private String layoutId;

    @ApiModelProperty(value = "桌面部件id", required = true)
    @NotBlank
    private String widgetId;

    @ApiModelProperty(value = "x轴坐标 最大100", required = true)
    @NotNull
    private Integer xPos;

    @ApiModelProperty(value = "y轴坐标", required = true)
    @NotNull
    private Integer yPos;

    @ApiModelProperty(value = "宽度 页面总宽度100")
    private Integer defWidth;

    @ApiModelProperty(value = "高度 实际显示未设置的数X20个像素高度")
    private Integer defHeight;

    @ApiModelProperty(value = "是否固定 字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer isStatic;

    @ApiModelProperty(value = "点击更多打开地址")
    private String moreUrl;

    @ApiModelProperty(value = "标题")
    private String title;


    @ApiModelProperty(value = "所属分类id")
    @TableField(exist = false)
    private String widgetCategory;

    @ApiModelProperty(value = "所属分类名称")
    @TableField(exist = false)
    private String widgetCategoryName;

    @ApiModelProperty(value = "部件名称")
    @TableField(exist = false)
    private String widgetName;

    @ApiModelProperty(value = "部件描述")
    @TableField(exist = false)
    private String widgetDesc;

    @ApiModelProperty(value = "组件路径")
    @TableField(exist = false)
    private String componentPath;

    @ApiModelProperty(value = "状态 字典dictCode=bu_valid_status 0无效1有效")
    @Dict(dicCode = "bu_valid_status")
    @TableField(exist = false)
    private Integer status;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }


    @JsonProperty(value = "xPos")
    public Integer getxPos() {
        return xPos;
    }

    @JsonProperty(value = "yPos")
    public Integer getyPos() {
        return yPos;
    }

}
