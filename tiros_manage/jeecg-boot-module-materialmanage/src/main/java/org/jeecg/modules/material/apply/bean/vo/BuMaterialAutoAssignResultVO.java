package org.jeecg.modules.material.apply.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.modules.material.apply.bean.BuMaterialApplyDetail;

import java.util.List;

/**
 * <p>
 * 自动分配物料结果vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-24
 */
@Data
@Accessors(chain = true)
public class BuMaterialAutoAssignResultVO {

    @ApiModelProperty(value = "自动分配物料结果信息")
    private String assignResultMessage;

    @ApiModelProperty(value = "领用明细列表")
    private List<BuMaterialApplyDetail> applyDetailList;

}
