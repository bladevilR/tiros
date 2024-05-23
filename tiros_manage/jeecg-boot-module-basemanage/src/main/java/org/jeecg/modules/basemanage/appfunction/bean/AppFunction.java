package org.jeecg.modules.basemanage.appfunction.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * APP功能模块 用于设置工作台功能
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "AppFunction对象", description = "APP功能模块 用于设置工作台功能")
@TableName("app_function")
public class AppFunction extends Model<AppFunction> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "功能编码")
    private String funCode;

    @ApiModelProperty(value = "功能名称")
    private String funName;

    @ApiModelProperty(value = "排序")
    private Integer funSort;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "url")
    private String url;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
