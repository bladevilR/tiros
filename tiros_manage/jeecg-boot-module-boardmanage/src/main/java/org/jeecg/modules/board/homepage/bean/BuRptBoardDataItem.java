package org.jeecg.modules.board.homepage.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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

/**
 * <p>
 * 统计数据项表（首页） 数据项固定初始化，包括首页的预警区和数据区的数据项，数据由后台程序自动更新
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "统计数据项表（首页）对象", description = "统计数据项表（首页） 数据项固定初始化，包括首页的预警区和数据区的数据项，数据由后台程序自动更新")
@TableName("bu_rpt_board_data_item")
public class BuRptBoardDataItem extends Model<BuRptBoardDataItem> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "item_code", type = IdType.UUID)
    @ApiModelProperty(value = "数据项编码")
    private String itemCode;

    @ApiModelProperty(value = "所属数据分类", required = true)
    @NotBlank
    private String categoryId;

    @ApiModelProperty(value = "数据项标题", required = true)
    @NotBlank
    private String itemTitle;

    @ApiModelProperty(value = "数据项值", required = true)
    @NotBlank
    private String itemValue;

    @ApiModelProperty(value = "数据项描述", required = true)
    @NotBlank
    private String itemDesc;

    @ApiModelProperty(value = "数据排序")
    private String itemSort;

    @ApiModelProperty(value = "颜色")
    @TableField(exist = false)
    private String itemColor;


    @Override
    protected Serializable pkVal() {
        return this.itemCode;
    }

}
