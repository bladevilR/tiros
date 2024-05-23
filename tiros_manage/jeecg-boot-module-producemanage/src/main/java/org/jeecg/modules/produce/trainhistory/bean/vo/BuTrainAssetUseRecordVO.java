package org.jeecg.modules.produce.trainhistory.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 部件履历--使用记录
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-25
 */
@Data
@Accessors(chain = true)
public class BuTrainAssetUseRecordVO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "车辆履历-更换记录id")
    private String id;

    @ApiModelProperty(value = "所属工单id")
    private String orderId;

    @ApiModelProperty(value = "所属工单名称")
    private String orderName;

    @ApiModelProperty(value = "作业车辆id", notes = "如果是从检修系统过来，则这里存放 资产设备编码")
    private String trainId;

    @ApiModelProperty(value = "作业车辆名称=车号")
    private String trainName;

    @ApiModelProperty(value = "设备id 具体车辆设备id")
    private String assetId;

    @ApiModelProperty(value = "设备名称")
    private String assetName;

    @ApiModelProperty(value = "设备编码")
    private String assetNo;

    @ApiModelProperty(value = "物资编码")
    private String materialCode;

    @ApiModelProperty(value = "资产编码")
    private String assetCode;

    @ApiModelProperty(value = "出厂编号")
    private String manufNo;

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

    @ApiModelProperty(value = "换上换下时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date exchangeTime;

    @ApiModelProperty(value = "操作类型名称  换上 换下")
    private String type;

    /**
     * 换下设备id，后端使用，用于生成操作类型名称type
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String downAssetId;

    /**
     * 换上设备id，后端使用，用于生成操作类型名称type
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String upAssetId;

}
