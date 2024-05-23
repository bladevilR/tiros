package org.jeecg.modules.report.daily.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * <p>
 * 生产日报查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-08
 */
@Data
@Accessors(chain = true)
public class BuDayReportQueryVO {

    @ApiModelProperty(value = "日期 yyyy-MM-dd", required = true)
    @NotNull
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date day;

    @ApiModelProperty(value = "车号", required = true)
    @NotBlank
    private String trainNo;

    @ApiModelProperty(value = "填报车间id", required = true)
    @NotBlank
    private String workshopId;

    @ApiModelProperty(value = "填报车间名字")
    private String workshop;

}
