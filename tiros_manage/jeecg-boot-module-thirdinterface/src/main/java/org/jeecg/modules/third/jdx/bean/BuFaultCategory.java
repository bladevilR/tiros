package org.jeecg.modules.third.jdx.bean;

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
 * 故障分类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "故障分类对象", description = "故障分类")
@TableName("bu_fault_category")
public class BuFaultCategory extends Model<BuFaultCategory> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "分类编码", required = true)
    @NotBlank
    private String categoryCode;

    @ApiModelProperty(value = "分类描述", required = true)
    @NotBlank
    private String categoryDesc;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
