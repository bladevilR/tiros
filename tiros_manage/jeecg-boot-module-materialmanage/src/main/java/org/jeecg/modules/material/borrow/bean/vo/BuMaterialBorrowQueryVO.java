package org.jeecg.modules.material.borrow.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.bean.TimeQuery;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 物资借用记录查询VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/9/17
 */
@Data
public class BuMaterialBorrowQueryVO {

    @ApiModelProperty(value = "借用部门或借用人")
    private String searchText;
    @ApiModelProperty(value = "借用类型")
    private Integer borrowType;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "借用开始时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @ApiModelProperty(value = "借用结束时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    @ApiModelProperty(value = "是否归还")
    private Boolean returnStatus;

}
