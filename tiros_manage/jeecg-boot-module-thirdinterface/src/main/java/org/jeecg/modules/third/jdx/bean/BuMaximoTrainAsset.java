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
 * maximo资产设备
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bu_maximo_train_asset")
@ApiModel(value = "BuMaximoTrainAsset对象", description = "maximo资产设备")
public class BuMaximoTrainAsset extends Model<BuMaximoTrainAsset> {
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

    @ApiModelProperty(value = "位置编号")
    private String locationCode;

    @ApiModelProperty(value = "线路id")
    private String lineId;

    @ApiModelProperty(value = "车辆号")
    private String trainNo;

    @ApiModelProperty(value = "状态 字典dictCode=bu_valid_status 0无效1有效")
    private Integer status;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "maximo中状态")
    private String maximoStatus;

    @ApiModelProperty(value = "maximo中设备类型")
    private String maximoAssetType;

    @ApiModelProperty(value = "maximo中专业")
    private String maximoSpecialty;

    @ApiModelProperty(value = "maximo中orgid")
    private String maximoOrgId;

    @ApiModelProperty(value = "maximo中siteid")
    private String maximoSiteId;

    @ApiModelProperty(value = "maximo中dept")
    private String maximoDept;

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
