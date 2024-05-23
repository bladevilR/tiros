package org.jeecg.modules.material.plan.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yuyougen
 * @title: BuMaterialMustQueryVo
 * @projectName tiros-manage-parent
 * @description: TODO
 * @date 2021/4/3018:25
 */
@Data
public class BuMaterialYearPlanQueryVO {
    @ApiModelProperty("物资编码或名称" )
    private String searchText;
    @ApiModelProperty("年份" )
    private Integer sbYear;
    @ApiModelProperty("所属线路id" )
    private String lineId;
    @ApiModelProperty("所属修程id" )
    private String repairProgramId;
    @ApiModelProperty("提报人员id" )
    private String sbUserId;
    @ApiModelProperty("提报班组id" )
    private String sbDeptId;
    @ApiModelProperty("是否汇总" )
    private Boolean summary;
    @ApiModelProperty(hidden = true)
    private String id;
    @ApiModelProperty("车辆数量")
    private Integer materialYearTrain;
}
