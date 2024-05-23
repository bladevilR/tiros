package org.jeecg.modules.basemanage.traininfo.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class BuTrainStructureDetailImportVO implements Serializable {
    @JsonIgnore
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "子节点id,多个用英文逗号分隔")
    private String childId;
    /**
     * 上级结构ID，为null（不设置任何值）是则表示是顶级节点
     */
    @ApiModelProperty(value = "上级结构")
    private String parentId;
    @ApiModelProperty(value = "所属车辆结构id",required = true)
    private String trainStructId;
    @ApiModelProperty(value = "车辆编码",required = true)
    private String trainNo;

}
