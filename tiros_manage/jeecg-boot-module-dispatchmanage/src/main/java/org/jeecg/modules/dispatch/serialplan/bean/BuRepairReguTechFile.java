package org.jeecg.modules.dispatch.serialplan.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 规程工艺文件,作业分类关联的文件会被下面的作业项继承
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuRepairReguTechFile对象", description = "规程工艺文件,作业分类关联的文件会被下面的作业项继承")
@TableName("bu_repair_regu_tech_file")
public class BuRepairReguTechFile extends Model<BuRepairReguTechFile> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "规程明细id")
    private String reguDetailId;

    @ApiModelProperty(value = "文件id")
    private String fileId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
