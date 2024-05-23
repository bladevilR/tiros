package org.jeecg.modules.group.safeassume.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * <p>
 * 安全预想查询VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/12
 */
@Data
public class BuWorkSafeAssumeQueryVO {

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date readDate;

    @ApiModelProperty(value = "工班id", required = true)
    @NotBlank
    private String groupId;

}
