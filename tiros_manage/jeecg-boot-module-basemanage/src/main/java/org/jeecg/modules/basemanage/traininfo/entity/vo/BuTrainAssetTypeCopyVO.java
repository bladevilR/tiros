package org.jeecg.modules.basemanage.traininfo.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 设备类型结构
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BuTrainAssetTypeCopyVO implements Serializable {

    @ApiModelProperty(value = "子节点id,多个用英文逗号分隔")
    private String childId;

    @ApiModelProperty(value = "上级结构id 为null则表示是顶级节点")
    private String parentId;

    @ApiModelProperty(value = "所属车辆结构id")
    private String trainStructId;

    @ApiModelProperty(value = "车辆编码")
    private String trainNo;

}
