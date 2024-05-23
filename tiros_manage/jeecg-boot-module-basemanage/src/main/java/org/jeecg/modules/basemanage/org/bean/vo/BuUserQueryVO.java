package org.jeecg.modules.basemanage.org.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 人员信息查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-19
 */
@Data
@Accessors(chain = true)
public class BuUserQueryVO {

    @ApiModelProperty(value = "姓名或工号")
    private String searchText;

    @ApiModelProperty(value = "运营公司id")
    private String companyId;

    @ApiModelProperty(value = "车辆段id")
    private String depotId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;

    @ApiModelProperty(value = "班组id")
    private String groupId;

    @ApiModelProperty(value = "角色id")
    private String roleId;

}
