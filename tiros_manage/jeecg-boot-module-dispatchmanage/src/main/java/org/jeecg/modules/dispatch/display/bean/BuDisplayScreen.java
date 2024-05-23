package org.jeecg.modules.dispatch.display.bean;

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

/**
 * <p>
 * 大屏信息
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "大屏信息对象", description = "大屏信息")
@TableName("bu_display_screen")
public class BuDisplayScreen extends Model<BuDisplayScreen> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "大屏类型 1LED大屏 2液晶电视  字典dictCode=bu_display_screen_type", required = true)
    @NotNull
    @Dict(dicCode = "bu_display_screen_type")
    private Integer screenType;

    @ApiModelProperty(value = "大屏编码", required = true)
    @NotBlank
    private String screenCode;

    @ApiModelProperty(value = "大屏名称", required = true)
    @NotBlank
    private String screenName;

    @ApiModelProperty(value = "大屏地址")
    private String address;

    @ApiModelProperty(value = "所属工班id  字典dictCode=(bu_mtr_workshop_group,group_name,id)")
    private String groupId;

    @ApiModelProperty(value = "备注")
    private String remark;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;


    @ApiModelProperty(value = "所属工班名称")
    @TableField(exist = false)
    private String groupName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
