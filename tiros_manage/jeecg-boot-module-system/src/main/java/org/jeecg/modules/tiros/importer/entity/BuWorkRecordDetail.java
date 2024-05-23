package org.jeecg.modules.tiros.importer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 作业记录表明细, 一条规程项关联一条或多条作业记录明细，如果是多条表示对这条规程项的拆分
 * </p>
 *
 * @author youGen
 * @since 2020-08-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BuWorkRecordDetail {
    private static final long serialVersionUID = 1L;

    private String id;
    private String categoryId;
    private String itemNo;
    private String workContent;
    private Integer checkLevel;
    private String techRequire;
    private String workInfo;
    private Integer result;
    private String selfCheck;
    private Date selfCheckTime;
    private String guarderCheck;
    private Date guarderCheckTime;
    private String monitorCheck;
    private Date monitorCheckTime;
    private Date createTime;
    private String createBy;
    private Date updateTime;
    private String updateBy;

}
