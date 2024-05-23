package org.jeecg.modules.material.material.bean;

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
 * 可替换物资
 * </p>
 *
 * @author zhaiyantao
 * @since 2022-07-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bu_material_type_replace")
@ApiModel(value = "BuMaterialTypeReplace对象", description = "")
public class BuMaterialTypeReplace extends Model<BuMaterialTypeReplace> {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键=物资id=物资编码")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "可替换物资编码，多个逗号分隔")
    private String canReplace;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
