package org.jeecg.modules.material.material.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 物资分类
 * </p>
 *
 * @author yfy
 * @since 2021-05-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="BuMaterialCategory对象", description="物资分类")
public class BuMaterialCategory extends Model<BuMaterialCategory> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    private String code;

    @ApiModelProperty(value = "分类名称")
    private String name;

    @ApiModelProperty(value = "0 无效  1 有效")
    private Integer status;

    @ApiModelProperty(value = "为null时表示是顶级分类")
    private String parentId;

    private String remark;

    private Date createTime;

    private String createBy;

    private Date updateTime;

    private String updateBy;


    private List<BuMaterialCategory> children;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
