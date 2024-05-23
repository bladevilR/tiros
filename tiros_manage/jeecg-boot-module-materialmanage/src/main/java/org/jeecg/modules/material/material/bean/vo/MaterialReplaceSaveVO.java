package org.jeecg.modules.material.material.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.modules.material.material.bean.BuMaterialType;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * <p>
 * 可替换物资保存vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2022/8/1
 */
@Data
@Accessors(chain = true)
public class MaterialReplaceSaveVO {

    @ApiModelProperty(value = "物资id")
    @NotBlank
    private String id;

    @ApiModelProperty(value = "可替换物资列表")
    private List<BuMaterialType> canReplaceList;

}
