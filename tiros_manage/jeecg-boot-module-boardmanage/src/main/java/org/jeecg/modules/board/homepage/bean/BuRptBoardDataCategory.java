package org.jeecg.modules.board.homepage.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 数据分类表
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "数据分类表对象", description = "数据分类表")
@TableName("bu_rpt_board_data_category")
public class BuRptBoardDataCategory extends Model<BuRptBoardDataCategory> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "分类名称", required = true)
    @NotBlank
    private String categoryName;

    @ApiModelProperty(value = "分类描述")
    private String categoryDesc;

    @ApiModelProperty(value = "统计数据项")
    private List<BuRptBoardDataItem> items;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
