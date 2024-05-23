package org.jeecg.modules.material.tools.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * @Description: 工艺装备设置电动
 * @Author: ZhaiYanTao
 * @Date: 2020/9/15 10:58
 */
@Data
@Accessors(chain = true)
public class BuMaterialToolsSettingVO {
    @ApiModelProperty(value = "工艺装备ids，多个逗号分隔")
    private String ids;
    @ApiModelProperty(value = "是否电动工具")
    private Integer isElectric;

    @ApiModelProperty(value = "是否更新日期")
    private Boolean updateTime;

    @ApiModelProperty(value = "电动工具有效期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date expirDate;
}
