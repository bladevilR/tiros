package org.jeecg.modules.basemanage.traininfo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 车型信息 如：A型车，B型车
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bu_train_type")
public class BuTrainType implements Serializable {
    @JsonIgnore
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.UUID)
    private String id;
    @ApiModelProperty(value = "编码")
    @NotBlank
    private String code;
    @ApiModelProperty(value = "名称")
    @NotBlank
    private String name;

    /**
     * 0 无效  1 有效
     */
    @ApiModelProperty(value = "状态")
    @Dict(dicCode = "bu_train_type_status")
    @NotNull
    private Integer status;
    @ApiModelProperty(value = "备注")
    private String remark;
    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @ApiModelProperty(value = "更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    @ApiModelProperty(value = "更新人")
    private String updateBy;

}
