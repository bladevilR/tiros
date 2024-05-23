package org.jeecg.modules.quality.fault.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 故障信息查询VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/12
 */
@Data
public class BuFaultInfoQueryVODeprecated {

    @ApiModelProperty(value = "故障编号或描述")
    private String searchText;

    @ApiModelProperty(value = "故障状态  字典dictCode=bu_fault_status")
    private Integer status;

    @ApiModelProperty(value = "提交状态  字典dictCode=bu_fault_submit_status")
    private Integer submitStatus;

    @ApiModelProperty(value = "故障发现时间 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date happenTime;

}
