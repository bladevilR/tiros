package org.jeecg.modules.produce.trainhistory.bean;

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
import org.jeecg.common.aspect.annotation.Dict;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 车辆位置信息
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "车辆位置信息对象", description = "车辆位置信息")
@TableName("bu_train_place")
public class BuTrainPlace extends Model<BuTrainPlace> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "上级位置id")
    private String parentId;

    @ApiModelProperty(value = "编码")
    private String placeCode;

    @ApiModelProperty(value = "名称", required = true)
    @NotBlank
    private String placeName;

    @ApiModelProperty(value = "描述")
    private String remark;

    @ApiModelProperty(value = "位置类型 1设备方位2设备位置  字典dictCode=bu_train_place_type", required = true)
    @NotNull
    @Dict(dicCode = "bu_train_place_type")
    private Integer placeType;

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
