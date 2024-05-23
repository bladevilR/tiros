package org.jeecg.modules.basemanage.alertaccept.entity;

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
import org.jeecg.common.aspect.annotation.Dict;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 预警接受信息
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuBaseAlertAccept对象", description = "预警接受信息")
@TableName("bu_base_alert_accept")
public class BuBaseAlertAccept extends Model<BuBaseAlertAccept> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "预警类型 1物资库存预警 2物资到期预警 3工器具送检预警 4部件质保预警 5测量异常预警 6委外逾期预警 7工单逾期预警 8故障处理预警")
    @NotNull
    @Dict(dicCode = "bu_alert_type")
    private Integer alertType;

    @ApiModelProperty(value = "对象维度 1人员 2角色 3机构")
    @NotNull
    @Dict(dicCode = "bu_dimension")
    private Integer dimension;

    @ApiModelProperty(value = "接收对象 可多个，逗号分隔")
    private String target;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;


    @ApiModelProperty(value = "接收对象名字")
    @TableField(exist = false)
    private String targetName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
