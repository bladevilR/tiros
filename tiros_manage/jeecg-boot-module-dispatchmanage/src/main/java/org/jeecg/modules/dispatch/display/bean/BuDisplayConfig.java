package org.jeecg.modules.dispatch.display.bean;

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
import org.jeecg.common.aspect.annotation.Dict;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 播放配置
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "播放配置对象", description = "播放配置")
@TableName("bu_display_config")
public class BuDisplayConfig extends Model<BuDisplayConfig> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "大屏id", required = true)
    @NotBlank
    private String screenId;

    @ApiModelProperty(value = "资源类型 1看板资源 2图片 3视频 4自定义内容  字典dictCode=bu_display_resource_type", required = true)
    @NotNull
    @Dict(dicCode = "bu_display_resource_type")
    private Integer resourceType;

    @ApiModelProperty(value = "看板资源id 如果是资源类型是看板资源，则来自看板资源id")
    private String resourceId;

    @ApiModelProperty(value = "资源标题", required = true)
    @NotBlank
    private String title;

    @ApiModelProperty(value = "资源地址 根据类型不同地址不同：1看板资源 则来自资源的地址url；2图片3视频 来自上传的文件地址；4自定义内容 则为自定义内容显示页面地址")
    private String address;


    @ApiModelProperty(value = "大屏类型 1LED大屏 2液晶电视  字典dictCode=bu_display_screen_type")
    @TableField(exist = false)
    @Dict(dicCode = "bu_display_screen_type")
    private Integer screenType;

    @ApiModelProperty(value = "大屏编码")
    @TableField(exist = false)
    private String screenCode;

    @ApiModelProperty(value = "大屏名称")
    @TableField(exist = false)
    private String screenName;

    @ApiModelProperty(value = "大屏地址")
    @TableField(exist = false)
    private String screenAddress;

    @ApiModelProperty(value = "所属工班id")
    @TableField(exist = false)
    private String groupId;

    @ApiModelProperty(value = "所属工班名称")
    @TableField(exist = false)
    private String groupName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
