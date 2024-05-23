package org.jeecg.modules.material.must.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 设置必换件清单的工班vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-16
 */
@Data
@Accessors(chain = true)
public class BuMaterialMustListSetGroupVO {

    @ApiModelProperty(value = "必换件清单id列表", required = true)
    @NotNull
    private List<String> idList;

    @ApiModelProperty(value = "班组id", required = true)
    @NotBlank
    private String groupId;

}
