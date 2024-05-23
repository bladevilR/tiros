package org.jeecg.modules.material.apply.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * APP领用工单查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-20
 */
@Data
@Accessors(chain = true)
public class AppApplyOrderQueryVO {

    @ApiModelProperty(value = "物资名称或编码")
    private String materialType;

    @ApiModelProperty(value = "领用状态 字典dictCode=bu_material_apply_status", required = true)
    @NotNull
    private Integer status;

}
