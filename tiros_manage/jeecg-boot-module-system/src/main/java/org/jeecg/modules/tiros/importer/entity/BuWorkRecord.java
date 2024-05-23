package org.jeecg.modules.tiros.importer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 作业记录表
 * </p>
 *
 * @author youGen
 * @since 2020-08-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BuWorkRecord  {
    private static final long serialVersionUID=1L;


    private String id;

    private String code;
    private String title;
    private String lineId;
    private String repairProId;
    private String trainNo;
    private Date workDate;
    private Date finishDate;
    private String workGroupId;
    private String workGroupName;
    private String remark;

    private Integer status;

    private Integer checkResult;
    private Date checkDate;
    private String checkUserId;
    private Date createTime;
    private String createBy;
    private Date updateTime;
    private String updateBy;

}
