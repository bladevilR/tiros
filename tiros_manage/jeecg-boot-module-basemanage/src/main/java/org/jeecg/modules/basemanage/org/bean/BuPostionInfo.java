package org.jeecg.modules.basemanage.org.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 岗位信息
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuPositionInfo对象", description = "岗位信息")
@TableName("bu_postion_info")
public class BuPostionInfo extends Model<BuPostionInfo> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "岗位名称")
    private String positionName;

    @ApiModelProperty(value = "岗位描述")
    private String remark;

    @ApiModelProperty(value = "岗位工资")
    private BigDecimal wages;


    /**
     * 查询用户关联岗位使用
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private String userId;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
