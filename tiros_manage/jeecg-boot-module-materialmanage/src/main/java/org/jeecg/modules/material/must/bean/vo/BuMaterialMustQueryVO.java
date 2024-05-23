package org.jeecg.modules.material.must.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author yuyougen
 * @title: BuMaterialMustQueryVo
 * @projectName tiros-manage-parent
 * @description: TODO
 * @date 2021/4/3018:25
 */
@Data
public class BuMaterialMustQueryVO {

    @ApiModelProperty("物资编码或名称" )
    private String searchText;

    @ApiModelProperty("所属线路" )
    private String lineId;

    @ApiModelProperty("所属修程" )
    private String repairProgramId;

    @ApiModelProperty("所属系统" )
    private String sysId;

    @ApiModelProperty("工位" )
    private String workstationId;

    @ApiModelProperty("工班" )
    private String groupId;

    @ApiModelProperty(value = "物资编码列表")
    private List<String> materialTypeCodeList;

}
