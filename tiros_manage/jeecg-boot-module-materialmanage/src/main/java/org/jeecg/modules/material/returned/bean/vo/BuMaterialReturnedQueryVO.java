package org.jeecg.modules.material.returned.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 退料查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-02
 */
@Data
@Accessors(chain = true)
public class BuMaterialReturnedQueryVO {

    @ApiModelProperty(value = "退料单编码或名称")
    private String searchText;

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @ApiModelProperty(value = "结束时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    @ApiModelProperty(value = "列计划id")
    private String planId;

}
