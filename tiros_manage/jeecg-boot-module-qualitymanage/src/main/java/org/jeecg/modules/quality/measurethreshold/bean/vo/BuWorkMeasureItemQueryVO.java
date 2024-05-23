package org.jeecg.modules.quality.measurethreshold.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;

/**
 * <p>
 * 测量项查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/24
 */
@Data
@Accessors(chain = true)
public class BuWorkMeasureItemQueryVO {

    @ApiModelProperty(value = "在线表单(数据记录表)id")
    private String customId;

    @ApiModelProperty(value = "row1")

    private Integer row1;

    @ApiModelProperty(value = "row2")
    private Integer row2;

    @ApiModelProperty(value = "col1")
    private Integer col1;

    @ApiModelProperty(value = "col2")
    private Integer col2;


    public void checkParam() {
        if (StringUtils.isBlank(customId)) {
            throw new JeecgBootException("请输入在线表单(数据记录表)id");
        }
        if (null == row1 || null == row2 || null == col1 || null == col2) {
            throw new JeecgBootException("请输入行/列数据");
        }
    }

}
