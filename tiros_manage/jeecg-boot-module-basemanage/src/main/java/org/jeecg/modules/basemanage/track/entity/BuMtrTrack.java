package org.jeecg.modules.basemanage.track.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 股道信息
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bu_mtr_track")
public class BuMtrTrack extends Model<BuMtrTrack> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属车辆段id", required = true)
    @NotBlank
    private String depotId;

    @ApiModelProperty(value = "名字  后台设置=编码")
    private String name;

    @ApiModelProperty(value = "编码  长度限制32", required = true)
    @NotBlank
    private String code;

    @ApiModelProperty(value = "0 无效  1 有效")
    @Dict(dicCode = "bu_valid_status")
    @NotNull
    private Integer status;

    @ApiModelProperty(value = "备注  长度限制256")
    private String remark;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;

    @ApiModelProperty(value = "线路id")
    private String lineId;


    @ApiModelProperty(value = "序号")
    @TableField(exist = false)
    private Integer rowNum;

    @ApiModelProperty(value = "所属车辆段名称")
    @TableField(exist = false)
    private String depot;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
