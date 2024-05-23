package org.jeecg.modules.dispatch.fault.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 090102003, 09 表示专业编码，01 表示系统编码  02 表示 子系统编码  003 表示部件编码
 * -&
 * </p>
 *
 * @author youGen
 * @since 2021-06-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuFaultCodeNew对象", description = "090102003, 09 表示专业编码，01 表示系统编码  02 表示 子系统编码  003 表示部件编码 ")
public class BuFaultCodeNew extends Model<BuFaultCodeNew> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;
    @ApiModelProperty(value = "故障代码", required = true)
    @NotBlank
    private String fltCode;
    @ApiModelProperty(value = "故障名称", required = true)
    @NotBlank
    private String fltName;

    @ApiModelProperty(value = "系统 1  子系统2  部件 3",required = true)
    @Dict(dicCode = "bu_train_asset_type")
    @NotNull
    private Integer fltLevel;

    @ApiModelProperty(value = "上级故障编码,为空表示顶级")
    private String parentCode;

    @ApiModelProperty(value = "固定 03")
    private String fltMajorCode;

    @ApiModelProperty(value = "固定：车辆")
    private String fltMajorName;

    @ApiModelProperty(value = "是否自建  字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer selfCreate;
    @ApiModelProperty(value = "是否有字节点",hidden = true)
    @TableField(exist = false)
    private Boolean hasChildren;
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
