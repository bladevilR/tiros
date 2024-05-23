package org.jeecg.modules.produce.trainhistory.bean.vo.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 整改信息查询VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/15
 */
@Data
public class BuWorkRectifyQueryVO {

    @ApiModelProperty(value = "编号或名称")
    private String searchText;

    @ApiModelProperty(value = "整改状态  字典dictCode=bu_work_rectify_status")
    private Integer status;

    @ApiModelProperty(value = "下发日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date sendDate;

    @ApiModelProperty(value = "整改类型  字典dictCode=bu_work_rectify_type")
    private Integer rectifyType;
    @ApiModelProperty(value = "车号")
    private String trainNo;

}
