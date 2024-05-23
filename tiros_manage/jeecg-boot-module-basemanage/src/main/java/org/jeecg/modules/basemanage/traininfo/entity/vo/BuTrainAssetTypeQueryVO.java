package org.jeecg.modules.basemanage.traininfo.entity.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;

import java.io.Serializable;
import java.util.List;

/**
 * @author yyg
 */
@Data
public class BuTrainAssetTypeQueryVO implements Serializable {

    @ApiModelProperty(value = "上级结构id")
    private String id;

    @ApiModelProperty(value = "结构名称或者编码")
    private String searchText;

    @ApiModelProperty(value = "状态 字典dictCode=bu_valid_status  0无效 1有效")
    private Integer status;

    @ApiModelProperty(value = "所属车型id", required = true)
    private String trainTypeId;

    @ApiModelProperty(value = "结构类型  字典dictCode=bu_train_asset_type")
    @Dict(dicCode = "bu_train_asset_type")
    private Integer structType;

    @ApiModelProperty(value = "是否查询子节点")
    private Boolean needChildren;


//    @JsonIgnore
//    @ApiModelProperty(hidden = true)
//    private Boolean isChild;
//
//    @ApiModelProperty(hidden = true)
//    private List<String> parentIds;

}
