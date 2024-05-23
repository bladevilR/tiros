package org.jeecg.modules.dispatch.workorder.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderAnnex;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * <p>
 * 工单附件保存vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-19
 */
@Data
@Accessors(chain = true)
public class BuWorkOrderAnnexSaveVO {

    @ApiModelProperty(value = "工单id", required = true)
    @NotBlank
    private String orderId;

    @ApiModelProperty(value = "工单任务id")
    private String taskId;

    @ApiModelProperty(value = "工单附件列表")
    private List<BuWorkOrderAnnex> annexList;

    @ApiModelProperty(value = "是否删除以前的附件 默认是（删除）")
    private Boolean deleteBefore;

}
