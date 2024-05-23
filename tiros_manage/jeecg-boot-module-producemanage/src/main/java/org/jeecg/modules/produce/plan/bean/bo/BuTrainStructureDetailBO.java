package org.jeecg.modules.produce.plan.bean.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 车辆结构明细bo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-25
 */
@Data
@Accessors(chain = true)
public class BuTrainStructureDetailBO {
    private String id;
    private String name;
    private String wbs;
    private String parentId;
    private Integer structType;
    private String sortNo;
    private String assetTypeId;
    private Boolean hasChildren;
    private List<BuTrainStructureDetailBO> children;
    private List<String> childAssetTypeIdList;
}
