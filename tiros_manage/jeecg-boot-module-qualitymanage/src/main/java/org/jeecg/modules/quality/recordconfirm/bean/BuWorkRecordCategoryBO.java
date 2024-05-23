package org.jeecg.modules.quality.recordconfirm.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 作业记录表分类bo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-18
 */
@Data
@Accessors(chain = true)
public class BuWorkRecordCategoryBO {

    @ApiModelProperty(value = "分类id")
    private String id;

    @ApiModelProperty(value = "序号  大小限制11位整数")
    private Integer recIndex;

    @ApiModelProperty(value = "名称 来自规程作业项的名字  长度限制64")
    private String reguTitle;

    @ApiModelProperty(value = "记录明细列表")
    @TableField(exist = false)
    private List<BuPlanFormWorkRecordDetail> detailList;

}
