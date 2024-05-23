//package org.jeecg.modules.basemanage.plan.bean;
//
//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.annotation.TableId;
//import com.baomidou.mybatisplus.annotation.TableName;
//import com.baomidou.mybatisplus.extension.activerecord.Model;
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.experimental.Accessors;
//
//import java.io.Serializable;
//
///**
// * <p>
// * 作业位置
// * </p>
// *
// * @author youGen
// * @since 2020-08-03
// */
//@Data
//@EqualsAndHashCode(callSuper = false)
//@Accessors(chain = true)
//@ApiModel(value = "BuTpRepairPlanPlace对象", description = "作业位置")
//@TableName("bu_tp_repair_plan_place")
//public class BuTpRepairPlanPlace extends Model<BuTpRepairPlanPlace> {
//    public static BuTpRepairPlanPlace DO = new BuTpRepairPlanPlace();
//    @JsonIgnore
//    private static final long serialVersionUID = 1L;
//
//    @TableId(value = "id", type = IdType.UUID)
//    private String id;
//    @ApiModelProperty(value = "任务id")
//    private String taskId;
//    @ApiModelProperty(value = "位置序号")
//    private String placeNo;
//    @ApiModelProperty(value = "前端添加时，可批量添加，输入前缀和添加的数量，则自动生成前缀+序号作为名称的位置信息")
//    private String placeName;
//    @ApiModelProperty(value = "备注")
//    private String remark;
//
//
//    @Override
//    protected Serializable pkVal() {
//        return this.id;
//    }
//
//}
