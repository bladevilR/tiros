package org.jeecg.modules.group.partchange.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 部件更换查询VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/13
 */
@Data
public class BuWorkPartChangeQueryVO {

    @ApiModelProperty("名称或序列号")
    private String searchText;

    @ApiModelProperty(value = "车辆号")
    private String trainNo;

    @ApiModelProperty(value = "所属系统id")
    private String systemId;

    @ApiModelProperty(value = "更换日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date changeDate;

}
