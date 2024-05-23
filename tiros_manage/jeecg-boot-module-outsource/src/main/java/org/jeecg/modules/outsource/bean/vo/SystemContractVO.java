package org.jeecg.modules.outsource.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 系统-合同vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-21
 */
@Data
@Accessors(chain = true)
public class SystemContractVO {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "类型 1系统(设备类型的系统) 2合同(委外合同)")
    private Integer type;

    @ApiModelProperty(value = "子节点  此处为：第一层=系统，第二层=合同")
    private List<SystemContractVO> children;

    @ApiModelProperty(value = "合同ids，多个逗号分割 type=1系统时=系统下所有合同ids，type=2合同时=合同id")
    private String contractIds;

}
