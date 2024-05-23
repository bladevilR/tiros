package org.jeecg.modules.group.information.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.modules.material.material.bean.BuMaterialType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 班组工装/工器具添加或删除VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/23
 */
@Data
public class BuGroupToolsEditVO {
    @ApiModelProperty(value = "工装id/工具id", required = true)
    @NotNull
    List<String> toolsId;
    @ApiModelProperty(value = "班组id", required = true)
    @NotBlank
    String groupId;
}
