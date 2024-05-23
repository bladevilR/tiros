package org.jeecg.modules.basemanage.workshop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 车间位置信息
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bu_mtr_workshop_place")
public class BuMtrWorkshopPlace implements Serializable {
    @JsonIgnore
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.UUID)
    private String id;
    @ApiModelProperty(value = "所属车间ID")
    private String workshopId;
    @ApiModelProperty(value = "名字")
    private String name;
    @ApiModelProperty(value = "编码")
    private String code;
    @ApiModelProperty(value = "位置")
    private String address;
    @ApiModelProperty(value = "备注")
    private String remark;
    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;
}
