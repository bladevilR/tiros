package org.jeecg.modules.material.apply.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.modules.material.apply.bean.BuMaterialAssignDetail;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * <p>
 * 物料分配明细 保存vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
@Data
@Accessors(chain = true)
public class BuMaterialAssignSaveVO {

    @ApiModelProperty(value = "物料分配明细列表")
    List<BuMaterialAssignDetail> assignDetailList;

}
