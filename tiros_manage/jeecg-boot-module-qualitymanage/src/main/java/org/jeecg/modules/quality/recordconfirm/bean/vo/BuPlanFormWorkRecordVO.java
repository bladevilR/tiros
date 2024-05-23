package org.jeecg.modules.quality.recordconfirm.bean.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

/**
 * <p>
 * 列计划表单选择vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-18
 */
@Data
@Accessors(chain = true)
public class BuPlanFormWorkRecordVO {

    @ApiModelProperty(value = "作业记录实列id")
    private String id;

    @ApiModelProperty(value = "表单名称")
    private String name;

    @ApiModelProperty(value = "表单序号")
    private Integer formIndex;

    @ApiModelProperty(value = "设备名称  有具体设备=具体设备名，没有具体设备=设备类型名")
    private String assetName;

    @ApiModelProperty(value = "进度  已完成作业明细比例(作业明细已有专检表示已完成)")
    private Integer progress;

    @ApiModelProperty(value = "状态 进度100=已完成，小于100=未完成")
    private String status;

    /**
     * 作业明细数
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private Integer detailCount;

    /**
     * 已完成作业明细数(作业明细已有专检表示已完成)
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private Integer finishDetailCount;

}
