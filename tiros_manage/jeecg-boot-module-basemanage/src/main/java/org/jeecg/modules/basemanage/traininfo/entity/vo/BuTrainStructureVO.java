package org.jeecg.modules.basemanage.traininfo.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>
 * 车辆结构
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BuTrainStructureVO implements Serializable {

    @ApiModelProperty(value = "车辆结构id或者要复制的结构ID")
    private String id;

    @ApiModelProperty(value = "结构编码  长度限制32")
    @NotBlank
    private String code;

    @ApiModelProperty(value = "结构名称  长度限制32")
    @NotBlank
    private String name;

    @ApiModelProperty(value = "所属线路")
    @NotBlank
    private String lineId;

    @ApiModelProperty(value = "备注  长度限制256")
    private String remark;
}
