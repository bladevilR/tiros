package org.jeecg.modules.dispatch.exchange.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 交接车整改项
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "交接车整改项对象", description = "交接车整改项")
@TableName("bu_repair_exchang_rectify")
public class BuRepairExchangRectify extends Model<BuRepairExchangRectify> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属交接车id", required = true)
    @NotBlank
    private String exchangeId;

    @ApiModelProperty(value = "整改名称", required = true)
    @NotBlank
    private String changeName;

    @ApiModelProperty(value = "整改描述", required = true)
    @NotBlank
    private String changeDesc;

    @ApiModelProperty(value = "责任部门id   字典dictCode=(sys_depart,depart_name,id)", required = true)
    @NotBlank
    private String dept;

    @ApiModelProperty(value = "责任人员id   接口获取/tiros/sys/user/queryUserByDepId", required = true)
    @NotBlank
    private String dutyMan;

    @ApiModelProperty(value = "整改日期 yyyy-MM-dd", required = true)
    @NotNull
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date changeDate;

    @ApiModelProperty(value = "检查日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date checkDate;

    @ApiModelProperty(value = "整改附件 附件存放地址")
    private String annex;

    @ApiModelProperty(value = "整改情况")
    private String changeResult;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "责任部门名称")
    @TableField(exist = false)
    private String deptName;

    @ApiModelProperty(value = "责任人员名称")
    @TableField(exist = false)
    private String dutyManName;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
