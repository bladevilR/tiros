package org.jeecg.modules.group.information.bean;

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
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 工位信息
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "工位信息对象", description = "工位信息")
@TableName("bu_mtr_workstation")
public class BuMtrWorkstation extends Model<BuMtrWorkstation> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属车间id", required = true)
    @NotBlank
    private String workshopId;

    @ApiModelProperty(value = "工位号")
    private String stationNo;

    @ApiModelProperty(value = "工位名称", required = true)
    @NotBlank
    private String name;

    @ApiModelProperty(value = "位置")
    private String location;

    @ApiModelProperty(value = "作业内容")
    private String content;

    @ApiModelProperty(value = "状态  字典dictCode=bu_valid_status")
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

    @ApiModelProperty(value = "线路id")
    private String lineId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
