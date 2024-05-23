package org.jeecg.common.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * PlusProject甘特图对象
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-13
 */
public abstract class PlusProjectGantt<T extends PlusProjectGanttTask> {

    @ApiModelProperty(value = "计划模版id")
    private String UID;

    @ApiModelProperty(value = "计划模版名称")
    private String Name;

    @ApiModelProperty(value = "开始日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date StartDate;

    @ApiModelProperty(value = "结束日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date FinishDate;

    @ApiModelProperty(value = "日历id（空）")
    private Integer CalendarUID;

    @ApiModelProperty(value = "任务集合")
    private List<T> Tasks;


    @JsonProperty("UID")
    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    @JsonProperty("Name")
    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    @JsonProperty("StartDate")
    public Date getStartDate() {
        return StartDate;
    }

    public void setStartDate(Date StartDate) {
        this.StartDate = StartDate;
    }

    @JsonProperty("FinishDate")
    public Date getFinishDate() {
        return FinishDate;
    }

    public void setFinishDate(Date FinishDate) {
        this.FinishDate = FinishDate;
    }

    @JsonProperty("CalendarUID")
    public Integer getCalendarUID() {
        return CalendarUID;
    }

    public void setCalendarUID(Integer CalendarUID) {
        this.CalendarUID = CalendarUID;
    }

    @JsonProperty("Tasks")
    @ApiModelProperty(value = "任务集合")
    public List<T> getTasks() {
        return Tasks;
    }

    @ApiModelProperty(value = "任务集合")
    public void setTasks(List<T> Tasks) {
        this.Tasks = Tasks;
    }

}
