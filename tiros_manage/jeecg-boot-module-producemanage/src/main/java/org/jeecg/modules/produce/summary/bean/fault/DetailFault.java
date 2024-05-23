package org.jeecg.modules.produce.summary.bean.fault;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 维修故障详情
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/18
 */
@Data
@Accessors(chain = true)
public class DetailFault {

    @ApiModelProperty(value = "故障id")
    private String faultId;

    @ApiModelProperty(value = "序号")
    private Integer repairIndex;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "系统")
    private String systemName;

    @ApiModelProperty(value = "发现时间 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date happenTime;

    @ApiModelProperty(value = "故障情况")
    private String faultConditions;

    @ApiModelProperty(value = "原因描述")
    private String reasonDesc;

    @ApiModelProperty(value = "处理措施描述")
    private String solutionDesc;

    @ApiModelProperty(value = "涉及部件数量")
    private Integer faultAssetCount;

}
