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
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 界面布局
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SysLayouts对象", description = "界面布局")
@TableName("sys_layouts")
public class SysLayouts extends Model<SysLayouts> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "布局名称", required = true)
    @NotBlank
    private String layoutName;

    @ApiModelProperty(value = "布局编码", required = true)
    @NotBlank
    private String layoutCode;

    @ApiModelProperty(value = "描述")
    private String layoutDesc;

    @ApiModelProperty(value = "是否默认主面板部件 字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer isMain;

    @ApiModelProperty(value = "使用范围 字典dictCode=bu_layout_scope 0系统公用1私人订制", required = true)
    @NotNull
    @Dict(dicCode = "bu_layout_scope")
    private Integer layoutScope;

    @ApiModelProperty(value = "所属人员 当是私人订制类型时，写入所属人员id, 公用则写入：global")
    private String byUserId;

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


    @ApiModelProperty(value = "所属人员名称")
    @TableField(exist = false)
    private String byUserName;

    @ApiModelProperty(value = "布局组件列表")
    @TableField(exist = false)
    private List<SysLayoutWidgets> layoutWidgetsList;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
