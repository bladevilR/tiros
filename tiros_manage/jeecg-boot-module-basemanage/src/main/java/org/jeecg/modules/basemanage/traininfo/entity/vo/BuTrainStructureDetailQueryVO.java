package org.jeecg.modules.basemanage.traininfo.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


import java.io.Serializable;

/**
 * @author yyg
 */
@Data
public class BuTrainStructureDetailQueryVO implements Serializable {

    @ApiModelProperty(value = "所属车辆结构id")
    private String trainStructId;

    @ApiModelProperty(value = "结构名称或者编码")
    private String searchText;

    @ApiModelProperty(value = "状态  字典dictCode=bu_train_type_status")
    private Integer status;

    @ApiModelProperty(value = "上级id")
    private String id;

    @ApiModelProperty(value = "是否查询子节点")
    private Boolean needChildren;
    @ApiModelProperty(value = "线路Id")
    private String lineId;

}
