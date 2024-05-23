package org.jeecg.modules.basemanage.org.bean;

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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 人员技能
 * 后端程序自动根据每个人参与过的作业记录信息次数，技能项为，作业记录对应规程明细中的规程分类，后端程序每次统计后记录更新日
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuUserSkill对象", description = "人员技能 后端程序自动根据每个人参与过的作业记录信息次数，技能项为，作业记录对应规程明细中的规程分类，后端程序每次统计后记录更新日")
@TableName("bu_user_skill")
public class BuUserSkill extends Model<BuUserSkill> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "用户id", required = true)
    @NotBlank
    private String userId;

    /**
     * 通过作业记录，关联到规程明细，再通过规程明细所属的作业分类统计次数
     */
    @ApiModelProperty(value = "技能项id，对应统计项的id", required = true)
    @NotBlank
    private String skillId;

    @ApiModelProperty(value = "技能项名, 对应统计项的名称", required = true)
    @NotBlank
    private String skillName;

    @ApiModelProperty(value = "分数，为参与技能项的次数", required = true)
    @NotNull
    private Long score;

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
