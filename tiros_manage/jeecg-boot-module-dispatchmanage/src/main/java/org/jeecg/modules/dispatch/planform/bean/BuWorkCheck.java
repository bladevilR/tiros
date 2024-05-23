package org.jeecg.modules.dispatch.planform.bean;

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

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 作业检查记录表【模板】
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuWorkCheck对象", description = "")
@TableName("bu_work_check")
public class BuWorkCheck extends Model<BuWorkCheck> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "检查表名称", required = true)
    @NotBlank
    private String title;

    @ApiModelProperty(value = "车号 作为模板时不需要")
    private String trainNo;

    @ApiModelProperty(value = "设备类型id", required = true)
    @NotBlank
    private String assetTypeId;

    @ApiModelProperty(value = "车辆结构明细id", required = true)
    @NotBlank
    private String assetStructId;

    @ApiModelProperty(value = "设备id 作为模板时不需要")
    private String assetId;

    @ApiModelProperty(value = "设备部件名称 作为模板时不需要")
    private String assetName;

    @ApiModelProperty(value = "设备部件号 作为模板时不需要")
    private String assetNo;

    @ApiModelProperty(value = "位置 作为模板时不需要")
    private String location;

    @ApiModelProperty(value = "作业周期 作为模板时不需要")
    private String period;

    @ApiModelProperty(value = "作业班组id 作为模板时不需要")
    private String groupId;

    @ApiModelProperty(value = "作业班组名称 作为模板时不需要")
    private String groupName;

    @ApiModelProperty(value = "作业评定 作为模板时不需要")
    private String judge;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建班组id")
    private String createGroupId;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "所属修程id")
    private String repairProId;

    @ApiModelProperty(value = "线路id")
    private String lineId;

    @ApiModelProperty(value = "是否有效")
    private Integer status;

    @ApiModelProperty(value = "编码")
    private String code;

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;


    @ApiModelProperty(value = "检查项列表")
    @TableField(exist = false)
    private List<BuWorkCheckItem> itemList;

    @ApiModelProperty(value = "工艺文件列表")
    @TableField(exist = false)
    private List<BuWorkCheckTechLink> techLinkList;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
