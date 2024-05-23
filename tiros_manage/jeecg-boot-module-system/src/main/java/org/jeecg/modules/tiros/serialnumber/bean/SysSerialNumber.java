package org.jeecg.modules.tiros.serialnumber.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 流水号 对象
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "流水号对象", description = "流水号生成配置信息")
@TableName("sys_serial_number")
public class SysSerialNumber extends Model<SysSerialNumber> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "模块名称", required = true)
    @NotBlank
    private String moduleName;

    @ApiModelProperty(value = "模块编码", required = true)
    @NotBlank
    private String moduleCode;

    @ApiModelProperty(value = "前缀 为空的话默认=moduleCode")
    private String prefix;

    @ApiModelProperty(value = "使用日期作为中缀 0否1是 默认1是")
    private Integer infixDate;

    @ApiModelProperty(value = "尾缀长度 默认6")
    private Integer suffixLength;

    @ApiModelProperty(value = "尾缀是否用0填充 0否1是 默认1是")
    private Integer suffixFillZero;

    @ApiModelProperty(value = "当前尾缀的最大值")
    private String currentSuffixMaxValue;

    @ApiModelProperty(value = "预生成序列号存放到缓存的个数(批量生成流水号个数)")
    private Integer prepareBatchSize;

    @ApiModelProperty(value = "每天清理尾缀值(仅当使用日期中缀时有用) 0否1是 默认1是")
    private Integer clearSuffixEveryDay;

    @ApiModelProperty(value = "最后一次清理尾缀值日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date clearSuffixLastDay;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
