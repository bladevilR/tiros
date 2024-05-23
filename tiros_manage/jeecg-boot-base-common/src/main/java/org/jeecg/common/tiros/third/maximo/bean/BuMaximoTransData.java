package org.jeecg.common.tiros.third.maximo.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 检修系统maximo数据同步中间表
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuMaximoTransData对象", description = "检修系统maximo数据同步中间表")
@TableName("bu_maximo_trans_data")
public class BuMaximoTransData extends Model<BuMaximoTransData> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "数据类型 1新增工单2关闭工单3物料消耗4物料退库5新增故障6人员工时7修改工单8修改故障9删除故障")
    private Integer type;

    @ApiModelProperty(value = "数据对象id 1=工单id 2=工单id 3=分配明细id 4=退料明细id 5=故障id 6=任务人员安排id 7=工单id 8=故障id 9=故障id")
    private String objId;

    @ApiModelProperty(value = "数据对象json")
    private String objJson;

    @ApiModelProperty(value = "创建时间 同步时需按时间顺序处理数据")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "maximo交互transId 用于物料消耗/退库成功时的状态处理")
    private Long transId;

    @ApiModelProperty(value = "成功标志 0初始1成功2失败")
    private Integer successStatus;

    @ApiModelProperty(value = "处理标志 0未处理1已处理")
    private Integer handleStatus;

    @ApiModelProperty(value = "消息 用于记录maximo失败信息等")
    private String message;

    @ApiModelProperty(value = "同步到maximo时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date transTime;

    @ApiModelProperty(value = "maximo处理成功时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date successTime;



    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}

