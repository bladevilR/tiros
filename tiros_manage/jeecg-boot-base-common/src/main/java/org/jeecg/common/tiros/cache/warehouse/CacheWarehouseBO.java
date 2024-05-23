package org.jeecg.common.tiros.cache.warehouse;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 仓库缓存bo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/26
 */
@Data
@Accessors(chain = true)
public class CacheWarehouseBO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "仓库id")
    private String id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "编码")
    private String code;

    @ApiModelProperty(value = "仓库级别")
    private Integer whLevel;

    @ApiModelProperty(value = "仓库类别")
    private Integer type;

    @ApiModelProperty(value = "上级仓库id")
    private String parentId;

    @ApiModelProperty(value = "wbs")
    private String wbs;

    @ApiModelProperty(value = "EBS系统中的库位编码")
    private String sysHouseCode;

}
