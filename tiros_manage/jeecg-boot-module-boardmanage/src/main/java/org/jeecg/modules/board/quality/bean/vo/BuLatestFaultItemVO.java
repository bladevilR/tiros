package org.jeecg.modules.board.quality.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 最新故障--故障信息vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/17
 */
@Data
@Accessors(chain = true)
public class BuLatestFaultItemVO {

    @ApiModelProperty("序号")
    private Integer order;

    @ApiModelProperty(value = "故障名称(故障描述)")
    private String faultDesc;

    @ApiModelProperty(value = "故障工位  用于最新故障显示")
    private String workstationName;

    @ApiModelProperty(value = "委外部件  用于委外故障显示")
    private String assetName;

    @ApiModelProperty(value = "提报日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date reportTime;
    @ApiModelProperty("id")
    private String id;

}
