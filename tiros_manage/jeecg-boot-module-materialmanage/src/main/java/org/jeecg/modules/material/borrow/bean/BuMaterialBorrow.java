package org.jeecg.modules.material.borrow.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 物资借用
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuMaterialBorrow对象", description = "物资借用")
@TableName("bu_material_borrow")
public class BuMaterialBorrow extends Model<BuMaterialBorrow> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "借用单号")
    private String billCode;

    @ApiModelProperty(value = "借用类型,字典 bu_borrow_type", required = true)
    @Dict(dicCode = "bu_borrow_type")
    @NotNull
    private Integer borrowType;

    @ApiModelProperty(value = "借用部门id   字典dictCode=(sys_depart,depart_name,id)", required = true)
    @NotBlank
    private String borrowDep;

    @ApiModelProperty(value = "借用人员id   接口获取/tiros/sys/user/queryUserByDepId")
    private String borrowUser;

    @ApiModelProperty(value = "借用日期 yyyy-MM-dd", required = true, example = "2020-01-01")
    @NotNull
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date borrowDate;

    @ApiModelProperty(value = "归还类型 1物料归还 2调拨归还 字典dictCode=bu_material_return_type", required = true)
    @Dict(dicCode = "bu_material_return_type")
    @NotNull
    private Integer returnType;

    @ApiModelProperty(value = "所属二级库   接口获取/tiros/material/warehouse/tree", required = true)
    @NotBlank
    private String warehouseId;

    @ApiModelProperty(value = "归还状态,字典:物料归还用这个bu_material_borrow_return_status,调拨归还这个bu_material_transfers_status", required = true)
    @Dict(dicCode = "bu_material_return_status")
    @NotNull
    private Integer returnStatus;

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

    @ApiModelProperty(value = "车间id")
    private String workshopId;

    @ApiModelProperty(value = "线路id")
    private String lineId;


    @ApiModelProperty(value = "仓库名称")
    @TableField(exist = false)
    private String warehouseName;

    @ApiModelProperty(value = "借用部门名称")
    @TableField(exist = false)
    private String deptName;

    @ApiModelProperty(value = "借用人员名称")
    @TableField(exist = false)
    private String borrowUserName;

    @ApiModelProperty(value = "物资借用明细")
    @TableField(exist = false)
    private List<BuMaterialBorrowDetail> borrowDetailList;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
