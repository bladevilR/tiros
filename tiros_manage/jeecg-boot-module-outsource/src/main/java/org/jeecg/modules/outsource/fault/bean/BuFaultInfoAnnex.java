package org.jeecg.modules.outsource.fault.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/**
 * <p>
 * 故障附件
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "故障附件对象", description = "故障附件")
@TableName("bu_fault_info_annex")
public class BuFaultInfoAnnex extends Model<BuFaultInfoAnnex> {


    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "故障信息id", required = true)
    @NotBlank
    private String faultId;

    @ApiModelProperty(value = "附件类型 1提报附件 2处理附件   字典dictCode=bu_fault_annexType", required = true)
    @NotNull
    @Dict(dicCode = "bu_fault_annexType")
    private Integer annexType;

    @ApiModelProperty(value = "文件id")
    private String fileId;
    @ApiModelProperty(value = "文件名称")
    private String name;
    @ApiModelProperty(value = "文件类型")
    private String type;
    @ApiModelProperty(value = "文件地址")
    private String savepath;
    @ApiModelProperty(value = "文件大小")
    private BigInteger fileSize;
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
