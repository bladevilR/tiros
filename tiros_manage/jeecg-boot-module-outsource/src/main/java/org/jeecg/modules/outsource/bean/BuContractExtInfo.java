package org.jeecg.modules.outsource.bean;

import com.baomidou.mybatisplus.annotation.IdType;
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

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 合同扩展信息
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuContractExtInfo对象", description = "合同扩展信息")
@TableName("bu_contract_ext_info")
public class BuContractExtInfo extends Model<BuContractExtInfo> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "合同id")
    private String contractId;

    @ApiModelProperty(value = " 监造周期，单位天")
    private Integer superPeriod;

    @ApiModelProperty(value = "监造每批人数单位个，表示每批次多少人")
    private Integer superNumber;

    @ApiModelProperty(value = "培训周期 单位天")
    private Integer trainPeriod;

    @ApiModelProperty(value = "培训人数")
    private Integer trainNumber;

    @ApiModelProperty(value = "培训开始日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date trainBegin;

    @ApiModelProperty(value = "监修信息、手动输入")
    private String superInfo;

    @ApiModelProperty(value = "培训信息、手动输入")
    private String trainInfo;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
