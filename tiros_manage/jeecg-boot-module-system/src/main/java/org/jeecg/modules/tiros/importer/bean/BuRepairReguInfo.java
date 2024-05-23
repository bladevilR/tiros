package org.jeecg.modules.tiros.importer.bean;

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

    private String trainTypeId;

    private String workshopId;

    private String version;

    private Integer status;

    private String remark;

    private Date createTime;
    private String createBy;
    private Date updateTime;
    private String updateBy;


    private String repairProName;

    private String lineName;

    private String workshopName;

}
