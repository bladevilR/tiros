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

/**
 * <p>
 * 参考工艺文件【模板】
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuWorkCheckTechLink对象", description = "")
@TableName("bu_work_check_tech_link")
public class BuWorkCheckTechLink extends Model<BuWorkCheckTechLink> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "检查表id")
    private String checkId;

    @ApiModelProperty(value = "工艺文件id")
    private String teckBookId;


    @ApiModelProperty(value = "工艺文件名称")
    @TableField(exist = false)
    private String teckBookName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
