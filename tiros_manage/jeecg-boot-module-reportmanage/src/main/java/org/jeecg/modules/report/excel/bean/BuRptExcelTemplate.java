package org.jeecg.modules.report.excel.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 报表中心 表格模板
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuRptExcelTemplate对象", description = "")
@TableName("bu_rpt_excel_template")
public class BuRptExcelTemplate extends Model<BuRptExcelTemplate> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "编码")
    private String tempCode;

    @ApiModelProperty(value = "名称")
    private String tempName;

    @ApiModelProperty(value = "内容")
    private String tempContent;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;

    @ApiModelProperty(value = "线路id")
    private String lineId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
