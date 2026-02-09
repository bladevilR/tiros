package org.jeecg.modules.basemanage.techbook.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yuyougen
 * @title: BuRepairTechBookQuery
 * @projectName tiros_manage
 * @description: TODO
 * @date 2021/5/1910:05
 */
@Data
public class BuRepairTechBookQueryVO {
    @ApiModelProperty(value = "线路")
    private String lineId;
    @ApiModelProperty(value = "修程")
    private String repairProId;
    @ApiModelProperty(value = "名称或编码")
    private String formName;
    @ApiModelProperty(value = "是否模板 0-否 1-模板")
    private Integer templateFlag;
}
