package org.jeecg.modules.produce.plan.bean.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.common.aspect.annotation.DictIgnore;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 计划监控-车辆结构方式vo（车辆结构树）
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-25
 */
@DictIgnore
@Data
@Accessors(chain = true)
public class BuRepairPlanTrainStructVO {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "wbs")
    private String wbs;

    @ApiModelProperty(value = "排序(同级)")
    private String sortNo;

    @ApiModelProperty(value = "上级id")
    private String parentId;

    @ApiModelProperty(value = "完成进度 百分比 0~100的数字")
    private Double progress;

    @ApiModelProperty(value = "进度状态 字典dictCode=bu_progress_status")
    @Dict(dicCode = "bu_progress_status")
    private Integer progressStatus;

    @ApiModelProperty(value = "故障数量")
    private Integer faultCount;

    @ApiModelProperty(value = "当前工单")
    private Integer currentOrderCount;

    @ApiModelProperty(value = "工单总数")
    private Integer orderCount;

    @ApiModelProperty(value = "工单ids,用于查询工单列表")
    private String orderIds;

    @ApiModelProperty(value = "是否有子结点")
    private Boolean hasChildren; 

    @ApiModelProperty(value = "子节点")
    private List<BuRepairPlanTrainStructVO> children;


    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private Set<String> childIdSet;

    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private Set<String> orderIdSet;

    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private Set<String> faultIdSet;

}
