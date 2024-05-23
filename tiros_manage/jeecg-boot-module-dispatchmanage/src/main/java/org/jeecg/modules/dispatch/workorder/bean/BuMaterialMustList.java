package org.jeecg.modules.dispatch.workorder.bean;

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
 * 必换件清单
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuMaterialMustList对象", description = "必换件清单")
@TableName("bu_material_must_list")
public class BuMaterialMustList extends Model<BuMaterialMustList> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "物资类型id", required = true)
    @NotBlank
    private String materialTypeId;

    @ApiModelProperty(value = "线路id", required = true)
    @NotBlank
    private String lineId;

    @ApiModelProperty(value = "修程id", required = true)
    @NotBlank
    private String repairProgramId;

    @ApiModelProperty(value = "所属系统id")
    private String sysId;

    @ApiModelProperty(value = "所属工班id")
    private String groupId;

    @ApiModelProperty(value = "所属工位id")
    private String workstationId;

    @ApiModelProperty(value = "每列需求数量")
    private Double needAmount;

    @ApiModelProperty(value = "选择车辆结构中的一个节点")
    private String location;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty("替换件编码")
    private String canReplace;
    
    @ApiModelProperty("安装部位")
    private String  locationDesc;

    @ApiModelProperty(value = "状态 字典dictCode=bu_valid_status 0无效1有效")
    @Dict(dicCode = "bu_valid_status")
    private Integer status;

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

    @ApiModelProperty(value = "车间id")
    private String workshopId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
