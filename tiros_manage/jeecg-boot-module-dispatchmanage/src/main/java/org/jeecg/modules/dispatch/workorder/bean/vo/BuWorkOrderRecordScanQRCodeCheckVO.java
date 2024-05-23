package org.jeecg.modules.dispatch.workorder.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 工单作业记录扫我的二维码检查检查vo
 * </p>
 *
 * @author lidafeng
 * @since 2021/03/07
 */
@Data
@Accessors(chain = true)
public class BuWorkOrderRecordScanQRCodeCheckVO extends CheckCommonVO {

    @ApiModelProperty(value = "检查人用户ID", required = true)
    @NotBlank
    private String checkUserId;

    @ApiModelProperty(value = "检查人二维码唯一标识", required = true)
    @NotBlank
    private String checkUserQRCodeUUID;
}
