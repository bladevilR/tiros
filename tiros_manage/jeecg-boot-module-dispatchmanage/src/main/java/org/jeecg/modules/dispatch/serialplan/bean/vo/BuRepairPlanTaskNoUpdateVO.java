package org.jeecg.modules.dispatch.serialplan.bean.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 计划模板任务序号更新--条目VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/3
 */
@Data
@Accessors(chain = true)
public class BuRepairPlanTaskNoUpdateVO {

    @ApiModelProperty(value = "任务id")
    private String UID;
    @ApiModelProperty(value = "任务序号")
    private Integer ID;
    @ApiModelProperty(value = "计划模板id")
    private String planId;

    @JsonProperty("UID")
    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    @JsonProperty("ID")
    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

}
