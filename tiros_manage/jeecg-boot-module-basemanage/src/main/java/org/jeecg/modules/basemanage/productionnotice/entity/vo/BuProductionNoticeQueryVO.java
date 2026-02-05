package org.jeecg.modules.basemanage.productionnotice.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "生产通知单查询对象", description = "生产通知单查询VO")
public class BuProductionNoticeQueryVO implements Serializable {

    @ApiModelProperty(value = "通知单号")
    private String noticeNo;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "通知单类型")
    private String noticeType;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "车型")
    private String trainType;

    @ApiModelProperty(value = "线别")
    private String line;
}
