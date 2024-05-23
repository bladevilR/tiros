package org.jeecg.modules.basemanage.traininfo.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 车辆设备查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-30
 */
@Data
@Accessors(chain = true)
public class BuTrainAssetTreeQueryVO {

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "车辆结构明细id")
    private String structDetailId;

    @ApiModelProperty(value = "设备类型id")
    private String assetTypeId;

    @ApiModelProperty(value = "以树结构查询返回所有子设备", example = "false")
    private Boolean queryChild;

    @ApiModelProperty(value = "以列表查询返回所有子设备", example = "false")
    private Boolean queryAllChildren;

    /**
     * 前端传trainNo转为trainId
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String trainId;

}
