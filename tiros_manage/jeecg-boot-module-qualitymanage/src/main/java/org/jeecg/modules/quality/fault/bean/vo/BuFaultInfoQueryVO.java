package org.jeecg.modules.quality.fault.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.bean.TimeQuery;

/**
 * <p>
 * 故障信息查询VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/9/29
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class BuFaultInfoQueryVO extends TimeQuery {

    @ApiModelProperty(value = "线路(所属线路id)   字典dictCode=(bu_mtr_line,line_name,line_id)")
    private String lineId;

    @ApiModelProperty(value = "车辆(所属车辆编号)   字典dictCode=(bu_train_info,train_no,train_no,line_id={所属线路id})")
    private String trainNo;

    @ApiModelProperty(value = "工班(提报班组id)   字典dictCode=(bu_mtr_workshop_group,group_name,id)")
    private String reportGroupId;

    @ApiModelProperty(value = "系统(所属系统id)   字典dictCode=(bu_train_asset_type,name,id,struct_type=1 and parent_id is null)")
    private String sysId;

    @ApiModelProperty(value = "部件(故障部件id)   接口获取/tiros/trainAsset/listChildren 传入code=所属车辆编号，systemId={所属系统id}")
    private String faultAssetId;

    @ApiModelProperty(value = "工位(工位id)   字典dictCode=(bu_mtr_workstation,name,id,id in (select workstation_id from bu_group_workstation where group_id = '{工班id}')) 注意加''")
    private String workstationId;

    @ApiModelProperty(value = "等级(故障等级) 1A 2B 3C 4D   字典dictCode=bu_fault_level")
    private Integer level;

    @ApiModelProperty(value = "完成(故障状态) 字典dictCode=bu_fault_status")
    private Integer status;

    @ApiModelProperty(value = "阶段(发现阶段) 1架修期 2质保 3出保期   字典dictCode=bu_fault_phase")
    private Integer phase;

    @ApiModelProperty(value = "是否正线   字典dictCode=bu_state")
    private Integer online;

    @ApiModelProperty(value = "是否有责   字典dictCode=bu_state")
    private Integer hasDuty;

    @ApiModelProperty(value = "是否委外   字典dictCode=bu_state")
    private Integer outsource;

    @ApiModelProperty(value = "是否取消   字典dictCode=bu_state")
    private Integer canceled;

    @ApiModelProperty(value = "故障来源 1手动创建 2导入历史故障 3同步历史质保故障")
    private Integer fromType;

}
