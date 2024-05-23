package org.jeecg.modules.basemanage.workrecord.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 作业记录表明细查询VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-28
 */
@Data
@Accessors(chain = true)
public class BuWorkRecordDetailQueryVO {

    @ApiModelProperty(value = "作业记录表id", required = true)
    private String id;

    @ApiModelProperty(value = "作业记录表分类id")
    private String categoryId;

    @ApiModelProperty(value = "作业内容")
    private String workContent;

    @ApiModelProperty(value = "技术要求")
    private String techRequire;

}
