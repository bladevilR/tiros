package org.jeecg.modules.produce.trainhistory.bean;

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
 * 整改附件
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "整改附件对象", description = "整改附件")
@TableName("bu_work_rectify_annex")
public class BuWorkRectifyAnnex extends Model<BuWorkRectifyAnnex> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    @ApiModelProperty(value = "文件id  文件服务返回的文件id", required = true)
    @NotBlank
    private String id;

    @ApiModelProperty(value = "所属整改id", required = true)
    @NotBlank
    private String rectifyId;

    @ApiModelProperty(value = "附件标题", required = true)
    @NotBlank
    private String title;

    @ApiModelProperty(value = "附件地址", required = true)
    @NotBlank
    private String address;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
