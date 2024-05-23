package org.jeecg.modules.basemanage.regu.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 规程人员需求
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bu_repair_regu_person")
public class BuRepairReguPerson extends Model<BuRepairReguPerson> {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "规程明细id")
    private String reguDetailId;

    @ApiModelProperty(value = "所需人数")
    private Integer amount;

    @ApiModelProperty(value = "要求人员岗位，可以多个岗位，逗号分隔")
    private String requirePostion;

    @ApiModelProperty(value = "证书要求 手动输入")
    private String requireCertificate;

    @ApiModelProperty(value = "技能要求 手动输入")
    private String requireTech;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
