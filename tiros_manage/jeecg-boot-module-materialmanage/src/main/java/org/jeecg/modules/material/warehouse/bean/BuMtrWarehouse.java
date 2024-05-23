package org.jeecg.modules.material.warehouse.bean;

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
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 仓库信息
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "仓库对象", description = "仓库信息")
@TableName("bu_mtr_warehouse")
public class BuMtrWarehouse extends Model<BuMtrWarehouse> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "名称  长度限制32", required = true)
    @NotBlank
    private String name;

    @ApiModelProperty(value = "编码  长度限制32", required = true)
    @NotBlank
    private String code;

    @ApiModelProperty(value = "仓库级别  从1到127表示仓库的级别，最大127，0 顶层为总库（默认创建）级别自动根据选择的上级仓库进行累计", required = true)
    @NotNull
    private Integer whLevel;

    @ApiModelProperty(value = "仓库类别  字典dicCode=bu_warehouse_type", required = true)
    @Dict(dicCode = "bu_warehouse_type")
    @NotNull
    private Integer type;

    @ApiModelProperty(value = "是否关账 字典dicCode=bu_state")
    @Dict(dicCode = "bu_state")
    //@TableField("`close`")
    private Integer close;

    @ApiModelProperty(value = "上级仓库id，为null表示顶级库")
    private String parentId;

    @ApiModelProperty(value = "wbs信息 为上级的wbs+点+本级编码,如：上级wbs.xxxx，没有上级则上级wbs为空  新增时自动生成")
    private String wbs;

    @ApiModelProperty(value = "EBS系统中的库位编码  长度限制64")
    private String sysHouseCode;

    @ApiModelProperty(value = "EBS系统中的库存组织")
    private String sysHouseCategory;

    @ApiModelProperty(value = "位置描述  长度限制64")
    private String location;

    @ApiModelProperty(value = "所属线路id")
    private String lineId;

    @ApiModelProperty(value = "所属车辆段id")
    private String depotId;

    @ApiModelProperty(value = "所属车间id")
    private String workshopId;

    @ApiModelProperty(value = "是否同步  字典dicCode=bu_state  表示这个信息是否是从其他系统同步的，如果是从其他系统获取的，则不能删除")
    @Dict(dicCode = "bu_state")
    private Integer sync;

    @ApiModelProperty(value = "状态 字典dicCode=bu_valid_status")
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


    @ApiModelProperty(value = "是否有子节点")
    @TableField(exist = false)
    private Boolean hasChildren;

    @ApiModelProperty(value = "上级仓库名称")
    @TableField(exist = false)
    private String parentName;

    @ApiModelProperty(value = "所属线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "所属车辆段名称")
    @TableField(exist = false)
    private String depotName;

    @ApiModelProperty(value = "所属车间名称")
    @TableField(exist = false)
    private String workshopName;

    @ApiModelProperty(value = "子仓库")
    @TableField(exist = false)
    private List<BuMtrWarehouse> children;

    @ApiModelProperty(value = "标识码图片url 生成过二维码后保存图片的路径，相对路径")
    @TableField(exist = false)
    private String qrcodeImgUrl;

    @ApiModelProperty(value = "有规则的仓库id")
    @TableField(exist = false)
    private String regularWarehouseId;

    /**
     * 需要插入，用于excel导入库位信息
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private Boolean needInsert;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
