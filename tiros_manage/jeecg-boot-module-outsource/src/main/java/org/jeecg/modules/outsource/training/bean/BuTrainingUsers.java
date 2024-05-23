package org.jeecg.modules.outsource.training.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * 
 * </p>
 *
 * @author youGen
 * @since 2021-05-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="BuTrainingUsers对象", description="")
public class BuTrainingUsers extends Model<BuTrainingUsers> {

    private static final long serialVersionUID=1L;
    @TableId(value = "id", type = IdType.UUID)
    @ApiModelProperty("id")
    private String id;
    @ApiModelProperty("培训记录id")
    private String trainingId;
    @ApiModelProperty("培训人id")
    private String userId;

    @ApiModelProperty(value = "用户真实姓名")
    private String realName;
    @ApiModelProperty(value = "班组")
    @TableField(exist = false)
    private String groupName;
    @ApiModelProperty(value = "工号")
    @TableField(exist = false)
    private String workNo;


    @Override
    protected Serializable pkVal() {
        return null;
    }

}
