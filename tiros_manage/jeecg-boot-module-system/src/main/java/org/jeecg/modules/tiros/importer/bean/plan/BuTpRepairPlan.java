package org.jeecg.modules.tiros.importer.bean.plan;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 维修计划模版
 * </p>
 *
 * @author youGen
 * @since 2020-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BuTpRepairPlan {
    private static final long serialVersionUID = 1L;

    private String id;
    private String code;
    private String tpName;
    private String lineId;
    private String lineName;
    private String trainTypeId;
    private String trainTypeName;
    private String repairProgramId;
    private String repairProgramName;
    private Integer groupQuantity;
    private Integer duration;
    private Date baseDate;
    private Integer status;
    private Integer defaultTp;
    private String remark;
    private Date createTime;
    private String createBy;
    private Date updateTime;
    private String updateBy;
    private String reguId;
}
