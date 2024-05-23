package org.jeecg.modules.quality.measurethreshold.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;

/**
 * <p>
 * 数据记录表查询VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/21
 */
@Data
public class BuBaseFormTemplateQueryVO {

    @ApiModelProperty(value = "表单名称")
    private String name;

    @ApiModelProperty(value = "表单类型")
    private Integer formType;

    @ApiModelProperty(value = "线路Id")
    private String lineId;

    @ApiModelProperty(value = "修程id")
    private String repairProgramId;

    @ApiModelProperty(value = "规程id")
    private String reguId;

    @ApiModelProperty(value = "有效状态 字典dictCode=bu_valid_status")
    @Dict(dicCode = "bu_valid_status")
    private Integer status;

    /**
     * 数据记录表分类编码，后端硬编码
     */
    @ApiModelProperty(hidden = true)
    private String dataRecordFormCategoryCode;

}
