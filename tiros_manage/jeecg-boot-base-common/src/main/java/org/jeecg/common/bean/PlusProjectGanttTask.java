package org.jeecg.common.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * PlusProject甘特图对象-task
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-13
 */
public abstract class PlusProjectGanttTask {

    @ApiModelProperty(value = "必要任务UID(唯一性标识符)")
    private String UID;

    @ApiModelProperty(value = "上级任务id")
    private String ParentId;

    @ApiModelProperty(value = "任务名称")
    private String Name;

    @ApiModelProperty(value = "必要DateTime。开始日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date Start;

    @ApiModelProperty(value = "必要DateTime。完成日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date Finish;

    @ApiModelProperty(value = "必要DateTime。实际开始日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date ActualStart;

    @ApiModelProperty(value = "必要DateTime。实际完成日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date ActualFinish;

    @ApiModelProperty(value = "必要Number。工期")
    private Integer Duration;

    @ApiModelProperty(value = "必要Number。进度")
    private Double PercentComplete;

    @ApiModelProperty(value = "Number。序号")
    private Integer ID;

    @ApiModelProperty(value = "体现树形层次和顺序")
    private String OutlineNumber;

    @ApiModelProperty(value = "层次")
    private Integer OutlineLevel;

    @ApiModelProperty(value = "Number。工时")
    private Integer Work;

    @ApiModelProperty(value = "1或0。关键任务")
    private Integer Critical;

    @ApiModelProperty(value = "任务备注")
    private String Notes;

    @ApiModelProperty(value = "部门")
    private String Department;


    @JsonProperty("UID")
    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    @JsonProperty("ParentId")
    public String getParentId() {
        return ParentId;
    }

    public void setParentId(String ParentId) {
        this.ParentId = ParentId;
    }

    @JsonProperty("Name")
    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    @JsonProperty("Start")
    public Date getStart() {
        return Start;
    }

    public void setStart(Date Start) {
        this.Start = Start;
    }

    @JsonProperty("Finish")
    public Date getFinish() {
        return Finish;
    }

    public void setFinish(Date Finish) {
        this.Finish = Finish;
    }

    @JsonProperty("ActualStart")
    public Date getActualStart() {
        return ActualStart;
    }

    public void setActualStart(Date ActualStart) {
        this.ActualStart = ActualStart;
    }

    @JsonProperty("ActualFinish")
    public Date getActualFinish() {
        return ActualFinish;
    }

    public void setActualFinish(Date ActualFinish) {
        this.ActualFinish = ActualFinish;
    }

    @JsonProperty("Duration")
    public Integer getDuration() {
        return Duration;
    }

    public void setDuration(Integer Duration) {
        this.Duration = Duration;
    }

    @JsonProperty("PercentComplete")
    public Double getPercentComplete() {
        return PercentComplete;
    }

    public void setPercentComplete(Double PercentComplete) {
        this.PercentComplete = PercentComplete;
    }

    @JsonProperty("ID")
    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    @JsonProperty("OutlineNumber")
    public String getOutlineNumber() {
        return OutlineNumber;
    }

    public void setOutlineNumber(String OutlineNumber) {
        this.OutlineNumber = OutlineNumber;
    }

    @JsonProperty("OutlineLevel")
    public Integer getOutlineLevel() {
        return OutlineLevel;
    }

    public void setOutlineLevel(Integer OutlineLevel) {
        this.OutlineLevel = OutlineLevel;
    }

    @JsonProperty("Work")
    public Integer getWork() {
        return Work;
    }

    public void setWork(Integer Work) {
        this.Work = Work;
    }

    @JsonProperty("Critical")
    public Integer getCritical() {
        return Critical;
    }

    public void setCritical(Integer Critical) {
        this.Critical = Critical;
    }

    @JsonProperty("Notes")
    public String getNotes() {
        return Notes;
    }

    public void setNotes(String Notes) {
        this.Notes = Notes;
    }

    @JsonProperty("Department")
    public String getDepartment() {
        return Department;
    }

    public void setDepartment(String Department) {
        this.Department = Department;
    }

}
