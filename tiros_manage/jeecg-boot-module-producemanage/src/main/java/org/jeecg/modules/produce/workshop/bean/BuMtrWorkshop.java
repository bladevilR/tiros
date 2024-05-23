package org.jeecg.modules.produce.workshop.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 架修车间
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "架修车间", description = "架修车间")
@TableName("bu_mtr_workshop")
public class BuMtrWorkshop extends Model<BuMtrWorkshop> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "车辆段id")
    private String depotId;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "编码")
    private String code;

    @ApiModelProperty(value = "车间类型 1检修车间 2架大修车间  字典dictCode=bu_workshop_type")
    @Dict(dicCode = "bu_workshop_type")
    private Integer workType;

    @ApiModelProperty(value = "车间能力 表示车间运转能力，这里表示车间能同时对车辆作业的能力数值，如：架大修修改车间能同时对2列车进行架修，则填2")
    private Integer ability;

    @ApiModelProperty(value = "车间平面图 车间平面图存放地址")
    private String graphAddress;

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


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
