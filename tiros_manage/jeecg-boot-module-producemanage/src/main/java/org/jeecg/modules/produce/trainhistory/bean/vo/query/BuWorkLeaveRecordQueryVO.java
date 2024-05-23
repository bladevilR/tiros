package org.jeecg.modules.produce.trainhistory.bean.vo.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yuyougen
 * @title: BuWorkLeaveRecordQueryVO
 * @projectName tiros_manage
 * @description: TODO
 * @date 2021/5/414:31
 */
@Data
public class BuWorkLeaveRecordQueryVO {
   @ApiModelProperty(value = "名称")
    private  String searchText;
    @ApiModelProperty(value = "线路")
    private  String lineId;
    @ApiModelProperty(value = "车号")
    private  String trainNo;
    @ApiModelProperty(value = "状态")
    private  Integer status;
}
