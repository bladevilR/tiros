package org.jeecg.modules.basemanage.regu.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 规程信息
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bu_repair_regu_info")
public class BuRepairReguInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "规程编码", required = true)
    private String code;

    @ApiModelProperty(value = "规程名称", required = true)
    private String name;

    @ApiModelProperty(value = "所属修程id")
    private String repairProId;

    @ApiModelProperty(value = "所属线路id", required = true)
    private String lineId;

    @ApiModelProperty(value = "所属车型id  根据选择线路后台设置，界面不需要展示")
    private String trainTypeId;

    @ApiModelProperty(value = "所属车间id")
    private String workshopId;

    @ApiModelProperty(value = "版本号  如果同一线路存在多个规程，则默认取版本号最大的")
    private String version;

    @ApiModelProperty(value = "状态：0 无效  1 有效")
    @Dict(dicCode = "bu_valid_status")
    private Integer status;

    @ApiModelProperty(value = "备注")
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


    @ApiModelProperty(value = "所属修程名称")
    @TableField(exist = false)
    private String repairProName;

    @ApiModelProperty(value = "所属线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "所属车间名称")
    @TableField(exist = false)
    private String workshopName;

}
