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
 * 培训参与人员记录
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuTraiiningAttend对象", description = "培训参与人员记录")
@TableName("bu_traiining_attend")
public class BuTraiiningAttend extends Model<BuTraiiningAttend> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "培训id")
    private String trainingId;

    @ApiModelProperty(value = "人员id")
    private String userId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
