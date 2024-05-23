package org.jeecg.modules.produce.trainhistory.bean.vo.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * <p>
 * 车辆设备查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
@Data
@Accessors(chain = true)
public class BuTrainAssetQueryVO {

    @ApiModelProperty(value = "车辆号", required = true)
    @NotBlank
    private String trainNo;

    @ApiModelProperty(value = "车辆结构明细id")
    private String structureDetailId;

    @ApiModelProperty(value = "上级车辆设备id 具体车辆设备")
    private String parentAssetId;

    @ApiModelProperty(value = "递归查询所有车辆结构明细")
    private Boolean recurseStructDetail;

    @ApiModelProperty(value = "递归查询所有车辆设备")
    private Boolean recurseAsset;

    @ApiModelProperty(value = "是否返回树形结构 true返回树形结构，false返回单层列表")
    private Boolean asTree;

    /**
     * 车辆结构明细id列表，后端使用，当recurseStructDetail=true时，由structureDetailId求出所有子车辆结构明细id
     */
    @ApiModelProperty(hidden = true)
    private List<String> structureDetailIdList;

    /**
     * 上级车辆设备wbs，后端使用，当recurseAsset=true时，由parentAssetId求出
     */
    @ApiModelProperty(hidden = true)
    private String parentAssetWbs;

}
