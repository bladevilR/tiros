package org.jeecg.modules.tiros.importer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 规程信息
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BuRepairReguInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String code;
    private String name;
    private String repairProId;
    private String lineId;

    /**
     * 根据选择线路后台设置，界面不需要展示
     */
    private String trainTypeId;
    private String workshopId;

    /**
     * 如果同一线路存在多个规程，则默认取版本号最大的
     */
    private String version;

    /**
     * 0 无效  1 有效
     */
    private Integer status;
    private String remark;
    private Date createTime;
    private String createBy;
    private Date updateTime;
    private String updateBy;
}
