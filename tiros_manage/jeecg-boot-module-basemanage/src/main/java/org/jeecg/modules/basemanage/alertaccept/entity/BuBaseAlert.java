package org.jeecg.modules.basemanage.alertaccept.entity;

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

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 预警信息
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuBaseAlert对象", description = "预警信息对象")
@TableName("bu_base_alert")
public class BuBaseAlert extends Model<BuBaseAlert> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "预警类型 1物资库存预警 2物资到期预警 3工器具送检预警 4部件质保预警 5测量异常预警 6委外逾期预警 7工单逾期预警 8故障处理预警")
    @NotNull
    @Dict(dicCode = "bu_alert_type")
    private Integer alertType;

    @ApiModelProperty(value = "预警对象id 1物资库存预警(物资类型id) 2物资到期预警(入库明细id) 3工器具送检预警(具体工器具id) 4部件质保预警(具体部件id) " +
            "5测量异常预(测量值id) 6委外逾期预警(出入场明细id) 7工单逾期预警(工单id) 8故障处理预警(故障id)")
    private String alertObjId;

    @ApiModelProperty(value = "预警提示信息 xxx物资当前库存量xxx，少于xxx； xxx物资入库日期为xxx，将到达质保期xxx； 等等类似")
    private String alertContent;

    @ApiModelProperty(value = "是否生成通知 0否 1是")
    @Dict(dicCode = "bu_state")
    private Integer genNotice;

    @ApiModelProperty(value = "状态 0新增 1已处理 2忽略")
    @Dict(dicCode = "bu_alert_status")
    private Integer status;

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

    @ApiModelProperty(value = "车间id")
    private String workshopId;

    @ApiModelProperty(value = "线路id")
    private String lineId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
