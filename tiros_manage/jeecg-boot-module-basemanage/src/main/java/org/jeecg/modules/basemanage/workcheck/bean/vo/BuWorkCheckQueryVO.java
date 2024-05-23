package org.jeecg.modules.basemanage.workcheck.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yuyougen
 * @title: BuWorkCheckQueryVO
 * @projectName tiros_manage
 * @description: TODO
 * @date 2021/5/1716:02
 */
@Data
public class BuWorkCheckQueryVO {

    @ApiModelProperty("作业记录表名称")
    private String name;

    @ApiModelProperty("所属规程id")
    private String repairProId;

    @ApiModelProperty("线路id")
    private String lineId;

}
