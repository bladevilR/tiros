package org.jeecg.modules.report.cost.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 组织贡献度查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-09
 */
@Data
@Accessors(chain = true)
public class KpiQueryVO {

    @ApiModelProperty(value = "线路id")
    private String lineId;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @ApiModelProperty(value = "结束时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @ApiModelProperty(value = "统计维度 1个人 2班组 默认1")
    private Integer type;
    @ApiModelProperty(value = "班组id")
    private String groupId;

}
