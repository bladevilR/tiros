package org.jeecg.modules.basemanage.traininfo.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yyg
 */
@Data
public class BuTrainAssetQueryVO implements Serializable {

    @ApiModelProperty(value = "上级结构id")
    private String id;

    @ApiModelProperty(value = "车辆编码", required = true)
    private String code;

    @ApiModelProperty(value = "名称或者编码")
    private String title;

    @ApiModelProperty(value = "状态：0：无效，1：有效")
    private Integer status;

    @ApiModelProperty(value = "所属系统id(设备类型结构ID)")
    private String systemId;

    @ApiModelProperty(value = "是否查询子节点")
    private Boolean needChildren;

}
