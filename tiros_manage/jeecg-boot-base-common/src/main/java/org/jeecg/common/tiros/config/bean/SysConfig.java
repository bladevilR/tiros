package org.jeecg.common.tiros.config.bean;

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
 * 系统配置-该表存放系统一些配置数据
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SysConfig对象", description = "系统配置-该表存放系统一些配置数据")
@TableName("sys_config")
public class SysConfig extends Model<SysConfig> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "config_code", type = IdType.UUID)
    @ApiModelProperty(value = "编码")
    private String configCode;

    @ApiModelProperty(value = "值")
    private String configValue;

    @ApiModelProperty(value = "名称")
    private String configName;

    @ApiModelProperty(value = "备注")
    private String configRemark;


    @Override
    protected Serializable pkVal() {
        return this.configCode;
    }

}
