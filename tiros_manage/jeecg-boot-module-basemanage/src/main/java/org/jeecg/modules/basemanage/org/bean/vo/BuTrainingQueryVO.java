package org.jeecg.modules.basemanage.org.bean.vo;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 培训查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-20
 */
@Data
@Accessors(chain = true)
public class BuTrainingQueryVO {

    @ApiModelProperty(value = "培训名称")
    private String title;

    @ApiModelProperty(value = "培训时间 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date trainTime;

    @ApiModelProperty(value = "用户id")
    private String userId;

}
