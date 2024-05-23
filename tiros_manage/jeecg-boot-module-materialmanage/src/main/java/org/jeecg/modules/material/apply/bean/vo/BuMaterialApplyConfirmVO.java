package org.jeecg.modules.material.apply.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.modules.material.apply.bean.BuMaterialApplyDetail;

import java.util.List;

/**
 * <p>
 * 物资领用备料确认VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-16
 */
@Data
@ApiModel(value = "备料确认")
public class BuMaterialApplyConfirmVO {

    @ApiModelProperty(value = "是否只保存 true=只保存（只保存时仅修改物料分配数据，不进行修改工单状态、领用量、更新统计数据等操作），false=保存加提交")
    private Boolean onlySave;

    @ApiModelProperty(value = "所属工单id")
    private String id;

    @ApiModelProperty(value = "备料确认领用明细对象列表")
    private List<BuMaterialApplyDetail> detailList;

}
