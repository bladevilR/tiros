package org.jeecg.modules.produce.fault.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.bean.TimeQuery;

import java.util.List;

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

    @ApiModelProperty(value = "车辆段(所属车辆段id)   字典dictCode=(bu_mtr_depot,name,id)")
    private String depotId;

    @ApiModelProperty(value = "线路(所属线路id)   字典dictCode=(bu_mtr_line,line_name,line_id, line_id in (select line_id from bu_mtr_depot_line where depot_id = '{所属车辆段id}'))")
    private String lineId;

    @ApiModelProperty(value = "车辆(所属车辆编号)   字典dictCode=(bu_train_info,train_no,train_no,line_id='{所属线路id}')")
    private String trainNo;

    @ApiModelProperty(value = "工班(提报班组id)   字典dictCode=(bu_mtr_workshop_group,group_name,id)")
    private String groupId;

    @ApiModelProperty(value = "系统(所属系统id)   字典dictCode=(bu_train_asset_type,name,id,struct_type=1 and parent_id is null)")
    private String sysId;

    @ApiModelProperty(value = "部件(故障部件id)   接口获取/tiros/trainAsset/listChildren 传入code=所属车辆编号，systemId={所属系统id}")
    private String assetId;

    @ApiModelProperty(value = "故障种类(比较分析使用) 1架修期故障 2架修期A/B故障 3质保期故障 4质保期A/B故障 5质保期正线故障 6质保期正线有责故障")
    private List<Integer> faultTypes;

    @ApiModelProperty(value = "故障来源 1手动创建 2导入历史故障 3同步历史质保故障")
    private Integer fromType;

}
