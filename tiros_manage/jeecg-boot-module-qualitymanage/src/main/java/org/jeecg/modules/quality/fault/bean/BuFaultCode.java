package org.jeecg.modules.quality.fault.bean;

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
 * 故障(问题)代码
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "故障代码对象", description = "故障(问题)代码")
@TableName("bu_fault_code")
public class BuFaultCode extends Model<BuFaultCode> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属分类id")
    private String categoryId;

    @ApiModelProperty(value = "故障代码", required = true)
    @NotBlank
    private String faultCode;

    @ApiModelProperty(value = "故障描述", required = true)
    @NotBlank
    private String faultDesc;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
