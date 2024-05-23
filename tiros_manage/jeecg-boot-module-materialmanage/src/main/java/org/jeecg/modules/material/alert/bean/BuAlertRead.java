package org.jeecg.modules.material.alert.bean;


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
import java.util.Date;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "预警已读对象")
@TableName("bu_alert_read")
public class BuAlertRead  extends Model<BuAlertRead> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "预警id", required = true)
    @NotBlank
    private String alertId;

    @ApiModelProperty(value = "用户id", required = true)
    @NotBlank
    private String userId;

    @JsonIgnore
    private Date createTime;
}
