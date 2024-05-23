package org.jeecg.modules.board.workstation.bean.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.util.DateUtils;

import javax.validation.constraints.NotBlank;
import java.util.Calendar;
import java.util.Date;

/**
 * <p>
 * 工位看板（车间）--工位数据信息查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/18
 */
@Data
@Accessors(chain = true)
public class BuWorkstationDataQueryVO {

    @ApiModelProperty(value = "工班id")
    private String groupId;

    @ApiModelProperty(value = "工位id", required = true)
    @NotBlank
    private String workstationId;

    /**
     * 今天0点时间，后端自动设置
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private Date todayStart;

    /**
     * 今天23:59时间，后端自动设置
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private Date todayEnd;

    public void initTodayDate() {
        Calendar calendar = Calendar.getInstance();
        this.todayEnd = DateUtils.transToDayEnd(calendar).getTime();
        this.todayStart = DateUtils.transToDayStart(calendar).getTime();
    }

}
