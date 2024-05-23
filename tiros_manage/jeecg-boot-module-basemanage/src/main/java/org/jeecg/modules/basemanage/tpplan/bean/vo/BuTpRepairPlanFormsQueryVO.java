package org.jeecg.modules.basemanage.tpplan.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * <p>
 * 计划模板表单查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-17
 */
@Data
@Accessors(chain = true)
public class BuTpRepairPlanFormsQueryVO {

    @ApiModelProperty(value = "计划模板id")
    private String tpPlanId;

    @ApiModelProperty(value = "表单名称或编码")
    private String searchText;

    @ApiModelProperty(value = "表单类型(默认查所有) 字典dictCode=bu_work_form_type")
    private Integer formType;

    @ApiModelProperty(value = "表单对象id")
    private String objId;

    @ApiModelProperty(value = "关联的设备类型id")
    private String assetTypeId;

}
