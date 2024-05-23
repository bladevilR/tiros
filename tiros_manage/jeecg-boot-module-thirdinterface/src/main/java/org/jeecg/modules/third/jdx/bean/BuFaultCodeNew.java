package org.jeecg.modules.third.jdx.bean;

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

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 故障编码(新)
 * 010102003, 01 表示专业编码，01 表示系统编码  02 表示 子系统编码  003 表示部件编码
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bu_fault_code_new")
@ApiModel(value = "BuFaultCodeNew对象", description = "故障编码(新)")
public class BuFaultCodeNew extends Model<BuFaultCodeNew> {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "编码")
    private String fltCode;

    @ApiModelProperty(value = "名称")
    private String fltName;

    @ApiModelProperty(value = "级别  1系统2子系统3部件")
    private Integer fltLevel;

    @ApiModelProperty(value = "上级编码")
    private String parentCode;

    @ApiModelProperty(value = "专业编码")
    private String fltMajorCode;

    @ApiModelProperty(value = "专业名称")
    private String fltMajorName;

    @ApiModelProperty(value = "是否自建")
    private Integer selfCreate;

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
