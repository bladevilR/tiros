package org.jeecg.modules.produce.summary.bean.outsource;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 委外部件验收问题
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/18
 */
@Data
@Accessors(chain = true)
public class InProblemInfo {

    @ApiModelProperty(value = "截至日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date outDate;

    @ApiModelProperty(value = "委外项目数")
    private Integer itemCount;

    @ApiModelProperty(value = "总问题数")
    private Integer total;

    @ApiModelProperty(value = "已处理问题数")
    private Integer handled;

    @ApiModelProperty(value = "问题列表")
    private List<InProblem> problemList;

}
