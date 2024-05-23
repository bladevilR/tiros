package org.jeecg.modules.dispatch.display.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>
 * 看板资源
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "看板资源对象", description = "看板资源")
@TableName("bu_display_resource")
public class BuDisplayResource extends Model<BuDisplayResource> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "标题", required = true)
    @NotBlank
    private String title;

    @ApiModelProperty(value = "地址", required = true)
    @NotBlank
    private String url;

    @ApiModelProperty(value = "预览 该资源的预览图片的base64编码的内容")
    private String preview;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
