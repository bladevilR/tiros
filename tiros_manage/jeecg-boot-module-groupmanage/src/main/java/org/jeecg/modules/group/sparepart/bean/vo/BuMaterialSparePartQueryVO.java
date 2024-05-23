package org.jeecg.modules.group.sparepart.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 周转金请求VO
 * @Author: ZhaiYanTao
 * @Date: 2020/9/14 15:54
 */
@Data
public class BuMaterialSparePartQueryVO {

    @ApiModelProperty(value = "名称或编码")
    private String searchText;

    @ApiModelProperty(value = "资产编码或序列号")
    private String assetCode;

    @ApiModelProperty(value = "状态 1使用中 2待使用 3维修中 4已报废，在进行设备更换时更新  字典dictCode=bu_turnover_status")
    private Integer status;

    @ApiModelProperty(value = "所属线路id  字典dictCode=(bu_mtr_line,line_name,line_id)")
    private String lineId;

}
