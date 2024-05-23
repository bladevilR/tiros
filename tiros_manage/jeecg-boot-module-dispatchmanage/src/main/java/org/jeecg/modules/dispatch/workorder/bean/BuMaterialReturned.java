package org.jeecg.modules.dispatch.workorder.bean;

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
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 退料
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuMaterialReturned对象", description = "退料")
@TableName("bu_material_returned")
public class BuMaterialReturned extends Model<BuMaterialReturned> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "退料单编码 自动生成")
    private String billCode;

    @ApiModelProperty(value = "退料名称 自动生成：工单名称+【退料】", required = true)
    @NotBlank
    private String billName;

    @ApiModelProperty(value = "退料日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date billDate;

    @ApiModelProperty(value = "工单id", required = true)
    @NotBlank
    private String workOrderId;

    @ApiModelProperty(value = "班组id 自动从工地带出")
    private String groupId;

    @ApiModelProperty(value = "办理人员id")
    private String handleUserId;

    @ApiModelProperty(value = "状态 0草稿1已确认 确认后走maximo进行减库存")
    @Dict(dicCode = "bu_material_returned_status")
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

    @ApiModelProperty(value = "物料退料发送时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date syncTime;

    @ApiModelProperty(value = "物料退料返回状态")
    private String syncResult;

    @ApiModelProperty(value = "物料退料返回状态时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date syncResultTime;

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;

    @ApiModelProperty(value = "线路id")
    private String lineId;


    @ApiModelProperty(value = "工单编码")
    @TableField(exist = false)
    private String workOrderCode;

    @ApiModelProperty(value = "工单名称")
    @TableField(exist = false)
    private String workOrderName;

    @ApiModelProperty(value = "班组名称")
    @TableField(exist = false)
    private String groupName;

    @ApiModelProperty(value = "办理人员名称")
    @TableField(exist = false)
    private String handleUserName;

    @ApiModelProperty(value = "退料明细列表")
    @TableField(exist = false)
    List<BuMaterialReturnedDetail> detailList;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
