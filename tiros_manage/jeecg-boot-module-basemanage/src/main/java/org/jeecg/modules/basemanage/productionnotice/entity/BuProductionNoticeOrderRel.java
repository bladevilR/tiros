package org.jeecg.modules.basemanage.productionnotice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("bu_production_notice_order_rel")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "生产通知单工单关联对象", description = "生产通知单与工单关联关系")
public class BuProductionNoticeOrderRel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.UUID)
    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "生产通知单ID")
    private String noticeId;

    @ApiModelProperty(value = "工单ID")
    private String orderId;

    @ApiModelProperty(value = "工单编号")
    private String orderCode;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "删除标记")
    private Integer delFlag;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}

