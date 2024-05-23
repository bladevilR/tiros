package org.jeecg.modules.dispatch.workorder.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairTaskRegu;

import java.io.Serializable;
import java.util.List;

/**
 * @author yyg
 */
@Data
@Accessors(chain = true)
public class BuWorkOrderTaskRelatedInfo implements Serializable {

    @ApiModelProperty(value = "作业要求")
    private JobRequirement requirement;

    @ApiModelProperty(value = "必换件")
    private List<BuWorkOrderTaskMustReplace> mustReplaces;

    @ApiModelProperty(value = "物料消耗")
    private List<BuWorkOrderMaterial> materials;

    @ApiModelProperty(value = "工器具")
    private List<BuWorkOrderTool> tools;

    @ApiModelProperty(value = "其它资料（文档文件）")
    private List<BuOtherData> otherData;

    @ApiModelProperty(value = "任务人员安排")
    private List<BuRepairTaskStaffArrange> staffArranges;

    @ApiModelProperty(value = "工单附件列表")
    private List<BuWorkOrderAnnex> annexList;

    @ApiModelProperty(value = "目标设备")
    private List<BuWorkOrderTaskEqu> equipments;

    @ApiModelProperty(value = "关联规程（来自列计划（计划任务）或规程（作业项任务））")
    private List<BuRepairTaskRegu> regus;

    @ApiModelProperty(value = "作业指导书明细")
    private List<BuWorkOrderBookStep> bookSteps;

    @ApiModelProperty(value = "工单信息")
    private BuWorkOrder workOrder;

    @ApiModelProperty(value = "任务信息")
    private BuWorkOrderTask task;

    @ApiModelProperty(value = "maximo设备更换链接")
    private String maximoAssetChangeUrl;

}
