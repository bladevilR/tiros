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
 * 故障应对措施
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "故障应对措施对象", description = "故障应对措施")
@TableName("bu_fault_solution")
public class BuFaultSolution extends Model<BuFaultSolution> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "应对措施代码", required = true)
    @NotBlank
    private String solutionCode;

    @ApiModelProperty(value = "应对措施描述", required = true)
    @NotBlank
    private String solutionDesc;

    @ApiModelProperty(value = "所属分类id")
    private String categoryId;

    @ApiModelProperty(value = "所属故障代码id")
    private String faultCodeId;

    @ApiModelProperty(value = "所属故障原因id")
    private String faultReasonId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
