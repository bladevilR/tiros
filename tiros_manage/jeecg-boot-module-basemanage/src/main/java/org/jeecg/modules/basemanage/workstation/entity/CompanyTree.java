package org.jeecg.modules.basemanage.workstation.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author yyg
 */
@Data
@Accessors(chain = true)
public class CompanyTree {

    @ApiModelProperty(value = "类型 1运营公司 2车辆段 3车间 4工班")
    private Integer type;

    @ApiModelProperty(value = "运营公司名称")
    private String name;

    @ApiModelProperty(value = "运营公司id")
    private String id;

    @ApiModelProperty(value = "车辆段列表")
    private List<DepotTree> children;

}
