package org.jeecg.modules.basemanage.workstation.entity.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

/**
 * <p>
 * 工位关联表单-表单信息
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-14
 */
@Data
@Accessors(chain = true)
public class WorkstationFormInfoVO {

    @ApiModelProperty(value = "表单类型 字典dictCode=bu_work_form_type")
    @Dict(dicCode = "bu_work_form_type")
    private Integer formBy;

    @ApiModelProperty(value = "表单对象id")
    private String formId;

    @ApiModelProperty(value = "编码")
    private String code;

    @ApiModelProperty(value = "表单名称")
    private String formName;

    @ApiModelProperty(value = "表单状态  字典dictCode=bu_valid_status")
    @Dict(dicCode = "bu_valid_status")
    private Integer status;

    @ApiModelProperty(value = "在线表单(数据记录表)类型 表单类型formBy=1在线表单(数据记录表)时的在线表单类型")
    @Dict(dicCode = "bu_form_type")
    private Integer type;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建班组id")
    private String createGroupId;

    @ApiModelProperty(value = "创建班组名称")
    private String createGroupName;

}
