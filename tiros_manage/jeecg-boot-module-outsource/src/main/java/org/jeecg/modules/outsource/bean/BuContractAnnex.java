package org.jeecg.modules.outsource.bean;

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

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 合同附件
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuContractAnnex对象", description = "合同附件")
@TableName("bu_contract_annex")
public class BuContractAnnex extends Model<BuContractAnnex> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "合同id")
    private String contractId;

    @ApiModelProperty(value = "文件id或标识 这里为minio中存放文件的id或标识，没有则不需要提供")
    private String fileId;

    @ApiModelProperty(value = "文件名称")
    private String name;

    @ApiModelProperty(value = "存放小写的文件扩展名称,如：.pdf")
    private String type;

    @ApiModelProperty(value = "文件路径 文件存放的路径，这里存放的是相对路径（包括存放在minio上也是相对路径）")
    private String savepath;

    @ApiModelProperty(value = "文件大小 单位为KB")
    private Long fileSize;

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
