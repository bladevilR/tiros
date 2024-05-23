package org.jeecg.modules.system.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * @Title: DuplicateCheckVo
 * @Description: 重复校验VO
 * @Author 张代浩
 * @Date 2019-03-25
 * @Version V1.0
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "重复校验数据模型", description = "重复校验数据模型")
public class DuplicateCheckVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 表名
     */
    @ApiModelProperty(value = "表名", example = "sys_dict", required = true)
    private String tableName;

    /**
     * 字段名
     */
    @ApiModelProperty(value = "字段名", example = "dict_code", required = true)
    private String fieldName;

    /**
     * 字段值
     */
    @ApiModelProperty(value = "字段值", example = "status", required = true)
    private String fieldVal;

    /**
     * 数据ID
     */
    @ApiModelProperty(value = "数据id", example = "1")
    private String dataId;

    /**
     * 额外条件字段
     */
    @ApiModelProperty(value = "额外过滤条件字段列表", dataType = "java.util.List<DuplicateCheckFieldVO>")
    private List<DuplicateCheckFieldVO> filterFields;

}