package org.jeecg.modules.produce.cost.bean.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.bean.TimeQuery;

import java.util.List;

/**
 * <p>
 * 物料消耗查询VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/26
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class BuCostQueryVO extends TimeQuery {

    @ApiModelProperty(value = "车辆段(所属车辆段id)   字典dictCode=(bu_mtr_depot,name,id)")
    private String depotId;

    @ApiModelProperty(value = "线路(所属线路id)   字典dictCode=(bu_mtr_line,line_name,line_id, line_id in (select line_id from bu_mtr_depot_line where depot_id = '{所属车辆段id}'))")
    private String lineId;

    @ApiModelProperty(value = "车辆(所属车辆编号)   字典dictCode=(bu_train_info,train_no,train_no,line_id='{所属线路id}')")
    private String trainNo;

    @ApiModelProperty(value = "工班(班组id)   字典dictCode=(bu_mtr_workshop_group,group_name,id)")
    private String groupId;

    @ApiModelProperty(value = "系统(所属系统id)   字典dictCode=(bu_train_asset_type,name,id,struct_type=1 and parent_id is null)")
    private String sysId;

    @ApiModelProperty(value = "部件(部件id)   接口获取/tiros/trainAsset/listChildren 传入code=所属车辆编号，systemId={所属系统id}")
    private String assetId;

    @ApiModelProperty(value = "列成本(比较分析中使用) 列成本必须指定车辆号trainNo")
    private Boolean single;

    @ApiModelProperty(value = "平均成本(比较分析中使用) 消耗/车辆数量")
    private Boolean average;

//    /**
//     * 设备类型id列表，当系统id(sysId)存在时，根据系统id获取系统下的所有设备类型，用于查询
//     */
//    @JsonIgnore
//    @ApiModelProperty(hidden = true)
//    private List<String> assetTypeIdList;

}
