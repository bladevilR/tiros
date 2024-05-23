package org.jeecg.modules.tiros.importer.bean.plan;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 计划任务明细
 * </p>
 *
 * @author youGen
 * @since 2020-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BuTpRepairPlanTask {

    private String id;
    private String planId;

    private String parentId;

    private Integer taskNo;
    private String taskWbs;
    private String taskName;

    private Integer important;

    private Integer outsource;

    private Integer method;

    private String systemId;

    private String assetTypeId;

    private Double workTime;

    private Double duration;

    private Date startTime;

    private Date finishTime;

    private Integer dayIndex;

    private String workGroupId;
    private String workGroupName;


    private String remark;
    private Date createTime;
    private String createBy;
    private Date updateTime;
    private String updateBy;
    private BuTpRepairPlanReguInfo repairPlanReguInfo;
    private Boolean hasChildren; 


}
