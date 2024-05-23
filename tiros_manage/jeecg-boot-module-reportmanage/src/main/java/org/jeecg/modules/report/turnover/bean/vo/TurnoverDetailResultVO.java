package org.jeecg.modules.report.turnover.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 周转件明细结果vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-31
 */
@Data
@Accessors(chain = true)
public class TurnoverDetailResultVO {

    @ApiModelProperty(value = "序号")
    private Long index;

    @ApiModelProperty(value = "车辆履历更换记录id")
    private String id;

    @ApiModelProperty(value = "部件名称")
    private String assetName;

    @ApiModelProperty(value = "所属系统")
    private String sysName;

    @ApiModelProperty(value = "原部件序列号")
    private String downAssetSn;

    @ApiModelProperty(value = "原部件安装位置")
    private String downAssetLocation;

    @ApiModelProperty(value = "原部件责任人")
    private String downAssetDutyUser;

    @ApiModelProperty(value = "原部件时间")
    private Date downAssetTime;

    @ApiModelProperty(value = "新部件序列号")
    private String upAssetSn;

    @ApiModelProperty(value = "新部件原车号")
    private String upAssetTrainNo;

    @ApiModelProperty(value = "新部件原安装位置")
    private String upAssetLocation;

    @ApiModelProperty(value = "新部件责任人")
    private String upAssetDutyUser;

    @ApiModelProperty(value = "新部件时间")
    private Date upAssetTime;

    @ApiModelProperty(value = "备注")
    private String remark;

}
