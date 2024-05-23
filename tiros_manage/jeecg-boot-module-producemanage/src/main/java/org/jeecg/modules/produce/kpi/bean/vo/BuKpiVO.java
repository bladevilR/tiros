package org.jeecg.modules.produce.kpi.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 绩效返回对象vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-06
 */
@Data
@Accessors(chain = true)
public class BuKpiVO {

    @ApiModelProperty(value = "工时贡献度")
    private List<BuKpiTimeItemVO> timeList;

    @ApiModelProperty(value = "故障贡献度")
    private List<BuKpiFaultItemVO> faultList;

}
