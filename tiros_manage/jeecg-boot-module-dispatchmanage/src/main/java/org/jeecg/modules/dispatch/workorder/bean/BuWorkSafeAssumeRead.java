package org.jeecg.modules.dispatch.workorder.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 安全预想阅读记录
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "安全预想阅读记录对象", description = "安全预想阅读记录")
@TableName("bu_work_safe_assume_read")
public class BuWorkSafeAssumeRead extends Model<BuWorkSafeAssumeRead> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "安全预想id", required = true)
    @NotBlank
    private String safeAssumeId;

    @ApiModelProperty(value = "阅读用户id", required = true)
    @NotBlank
    private String userId;

    @ApiModelProperty(value = "阅读时间 yyyy-MM-dd HH:mm:ss", required = true)
    @NotNull
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date readTime;


    @ApiModelProperty(value = "阅读用户名称")
    @TableField(exist = false)
    private String userName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
