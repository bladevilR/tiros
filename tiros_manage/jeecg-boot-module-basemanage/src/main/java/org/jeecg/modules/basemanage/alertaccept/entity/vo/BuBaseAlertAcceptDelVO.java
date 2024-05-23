package org.jeecg.modules.basemanage.alertaccept.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 预警信息
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BuBaseAlertAcceptDelVO implements Serializable {
    @ApiModelProperty(value = "这条数据id", required = true)
    private String id;
    @ApiModelProperty(value = "接收对象id", required = true)
    private String targetId;
}
