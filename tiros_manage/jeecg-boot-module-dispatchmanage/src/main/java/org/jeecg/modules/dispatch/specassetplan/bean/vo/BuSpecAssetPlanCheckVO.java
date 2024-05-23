package org.jeecg.modules.dispatch.specassetplan.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * <p>
 * 特种设备运用/保养计划冲突检查VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/9
 */
@Data
@Accessors(chain = true)
public class BuSpecAssetPlanCheckVO {

    @ApiModelProperty(value = "特种设备id", required = true)
    @NotBlank
    private String specAssetId;

    @ApiModelProperty(value = "特种设备计划id")
    private String specAssetPlanId;

    @ApiModelProperty(value = "开始时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @ApiModelProperty(value = "结束时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 当前时间，后端生成不需要传参
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private Date nowTime;

}
