package org.jeecg.modules.basemanage.traininfo.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

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
public class BuTrainAssetListQueryVOForApp {

    @ApiModelProperty(value = "线路id")
    private String lineId;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "所属系统id")
    private String systemId;

    @ApiModelProperty(value = "部件名称")
    private String assetName;

    @ApiModelProperty(value = "是否以列表返回所有子设备", example = "false")
    private Boolean queryAllChildren;

}
