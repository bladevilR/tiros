package org.jeecg.modules.report.turnover.bean;

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
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 车辆履历-更换记录
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuTrainHistoryChange对象", description = "车辆履历-更换记录")
@TableName("bu_train_history_change")
public class BuTrainHistoryChange extends Model<BuTrainHistoryChange> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属工单id")
    private String orderId;

    @ApiModelProperty(value = "所属工单名称")
    private String orderName;

    @ApiModelProperty(value = "作业负责人名称")
    private String dutyUser;

    @ApiModelProperty(value = "作业操作人名称，多个逗号分隔")
    private String workUser;

    @ApiModelProperty(value = "序号 工单上存在多个换上换下记录时")
    private String exchangeNo;

    @ApiModelProperty(value = "操作方式 字典dictCode=bu_work_change_type", notes = "1换上换下 2拆分 3交换")
    @Dict(dicCode = "bu_work_change_type")
    private String method;

    @ApiModelProperty(value = "作业车辆id", notes = "如果是从检修系统过来，则这里存放 资产设备编码")
    private String trainId;

    @ApiModelProperty(value = "作业车辆名称")
    private String trainName;

    @ApiModelProperty(value = "更换设备id 具体车辆设备id")
    private String trainAssetId;

    @ApiModelProperty(value = "更换设备名称")
    private String trainAssetName;

    @ApiModelProperty(value = "所属系统id")
    private String sysId;

    @ApiModelProperty(value = "所属系统名称")
    private String sysName;

    @ApiModelProperty(value = "所属子系统id")
    private String subSysId;

    @ApiModelProperty(value = "所属子系统名称")
    private String subSysName;

    @ApiModelProperty(value = "设备类型id")
    private String assetTypeId;

    @ApiModelProperty(value = "设备类型名称")
    private String assetTypeName;

    @ApiModelProperty(value = "物资类型id")
    private String materialTypeId;

    @ApiModelProperty(value = "物资类型名称")
    private String materialTypeName;

    @ApiModelProperty(value = "换下设备id")
    private String downAssetId;

    @ApiModelProperty(value = "换下设备名称")
    private String downAssetName;

    @ApiModelProperty(value = "换下设备出厂编码")
    private String downAssetSn;

    @ApiModelProperty(value = "换上设备id")
    private String upAssetId;

    @ApiModelProperty(value = "换上设备名称")
    private String upAssetName;

    @ApiModelProperty(value = "换上设备出厂编码")
    private String upAssetSn;

    @ApiModelProperty(value = "换上换下时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date exchangeTime;

    @ApiModelProperty(value = "归档来源 0架大修自身数据 1检修系统同步 默认0")
    private Integer archiveType;

    @ApiModelProperty(value = "备注")
    private String remark;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
