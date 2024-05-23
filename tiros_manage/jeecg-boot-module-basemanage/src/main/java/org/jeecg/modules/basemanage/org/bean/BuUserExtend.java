package org.jeecg.modules.basemanage.org.bean;

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
 * 用户扩展信息
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuUserExtend对象", description = "用户扩展信息")
@TableName("bu_user_extend")
public class BuUserExtend extends Model<BuUserExtend> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "user_id", type = IdType.UUID)
    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "岗位id")
    private String postionId;

    @ApiModelProperty(value = "系统id")
    private String systemId;


    @Override
    protected Serializable pkVal() {
        return this.userId;
    }

}
