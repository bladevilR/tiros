package org.jeecg.modules.basemanage.regu.bean.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;

import java.io.Serializable;

/**
 * @author yyg
 */
@Data
public class BuRepairReguInfoCopyVO implements Serializable {
    @ApiModelProperty(value = "复制的规程id", required = true)
    private String copyReguInfoId;
    @ApiModelProperty(value = "规程编码", required = true)
    private String code;
    @ApiModelProperty(value = "规程名称", required = true)
    private String name;
    @ApiModelProperty(value = "所属修程ID")
    private String repairProId;
    @ApiModelProperty(value = "所属线路ID", required = true)
    private String lineId;

    /**
     * 根据选择线路后台设置，界面不需要展示
     */
    @ApiModelProperty(value = "所属车型ID")
    @JsonIgnore
    private String trainTypeId;
    @ApiModelProperty(value = "所属车间ID")
    private String workshopId;

    /**
     * 如果同一线路存在多个规程，则默认取版本号最大的
     */
    @ApiModelProperty(value = "版本号")
    private String version;

    /**
     * 0 无效  1 有效
     */
    @ApiModelProperty(value = "状态：0 无效  1 有效")
    @Dict(dicCode = "bu_valid_status")
    private Integer status;


}
