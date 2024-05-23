package org.jeecg.modules.board.group.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 班组工器具预警
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-09-15
 */
@Data
@Accessors(chain = true)
public class BuGroupToolCheckAlert {

    @ApiModelProperty(value = "工器具id")
    private String toolId;

    @ApiModelProperty(value = "物资编码")
    private String materialTypeCode;

    @ApiModelProperty(value = "工器具编码")
    private String assetCode;

    @ApiModelProperty(value = "名称")
    private String toolName;

    @ApiModelProperty(value = "规格")
    private String toolModel;

    @ApiModelProperty(value = "下次送检时间 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date nextCheckTime;

    @ApiModelProperty(value = "逾期天数")
    private Integer overDays;

}
