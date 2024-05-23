package org.jeecg.modules.basemanage.workstation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 工位信息
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bu_mtr_workstation")
public class BuBaseWorkstation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属车间id", required = true)
    @NotBlank
    private String workshopId;

    @ApiModelProperty(value = "工位号  长度限制32", required = true)
    @NotBlank
    private String stationNo;

    @ApiModelProperty(value = "工位名称  长度限制20", required = true)
    @NotBlank
    private String name;

    @ApiModelProperty(value = "位置  长度限制100", required = true)
    @NotBlank
    private String location;

    @ApiModelProperty(value = "作业内容  长度限制1000", required = true)
    @NotBlank
    private String content;

    @ApiModelProperty(value = "状态  字典dicCode=bu_valid_status", required = true)
    @NotNull
    @Dict(dicCode = "bu_valid_status")
    private Integer status;

    @ApiModelProperty(value = "备注  长度限制500")
    private String remark;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "线路id")
    private String lineId;


    @ApiModelProperty(value = "公司名字")
    @TableField(exist = false)
    private String companyName;

    @ApiModelProperty(value = "车路段名字")
    @TableField(exist = false)
    private String depotName;

    @ApiModelProperty(value = "车间名字")
    @TableField(exist = false)
    private String workshopName;

    @ApiModelProperty(value = "班组id")
    @TableField(exist = false)
    private String groupId;

    @ApiModelProperty(value = "班组名字")
    @TableField(exist = false)
    private String groupName;

}
