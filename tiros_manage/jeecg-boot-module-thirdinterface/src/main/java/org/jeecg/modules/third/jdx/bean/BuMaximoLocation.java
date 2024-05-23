package org.jeecg.modules.third.jdx.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * maximo资产位置
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bu_maximo_location")
@ApiModel(value = "BuMaximoLocation对象", description = "maximo资产位置")
public class BuMaximoLocation extends Model<BuMaximoLocation> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "编号")
    private String code;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "上级编号")
    private String parentCode;

    @ApiModelProperty(value = "编号wbs")
    private String wbs;

    @ApiModelProperty(value = "状态 字典dictCode=bu_valid_status 0无效1有效")
    private Integer status;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "maximo中状态")
    private String maximoStatus;

    @ApiModelProperty(value = "maximo中orgid")
    private String maximoOrgId;

    @ApiModelProperty(value = "maximo中siteid")
    private String maximoSiteId;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
