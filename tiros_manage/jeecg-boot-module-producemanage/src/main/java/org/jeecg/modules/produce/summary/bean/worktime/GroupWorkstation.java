package org.jeecg.modules.produce.summary.bean.worktime;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 班组+工位
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/22
 */
@Data
@Accessors(chain = true)
public class GroupWorkstation {

    @ApiModelProperty(value = "班组名称")
    private String groupName;

    @ApiModelProperty(value = "工位名称列表")
    private List<String> workstationNameList;

}
