package org.jeecg.modules.group.information.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 车间班组关联信息
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuMtrWorkshopGroup对象", description = "")
@TableName("bu_mtr_workshop_group")
public class BuMtrWorkshopGroup extends Model<BuMtrWorkshopGroup> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "车间id")
    private String workshopId;

    @ApiModelProperty(value = "班组名称")
    private String groupName;

    @ApiModelProperty(value = "班组描述")
    private String groupDesc;

    @ApiModelProperty(value = "部门id")
    private String departId;

    @ApiModelProperty(value = "班长id")
    private String monitor;

    @ApiModelProperty(value = "班长2id")
    private String monitor2;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;


    @ApiModelProperty(value = "运营公司id")
    @TableField(exist = false)
    private String companyId;

    @ApiModelProperty(value = "运营公司名称")
    @TableField(exist = false)
    private String companyName;

    @ApiModelProperty(value = "车辆段id")
    @TableField(exist = false)
    private String depotId;

    @ApiModelProperty(value = "车辆段名称")
    @TableField(exist = false)
    private String depotName;

    @ApiModelProperty(value = "车间名称")
    @TableField(exist = false)
    private String workshopName;

    @ApiModelProperty(value = "班组部门编号")
    @TableField(exist = false)
    private String orgCode;

    @ApiModelProperty(value = "工班人数")
    @TableField(exist = false)
    private Integer userCount;

    @ApiModelProperty(value = "线路id，多个逗号分割")
    @TableField(exist = false)
    private String lineId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
