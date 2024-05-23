package org.jeecg.modules.tiros.importer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 工位信息
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BuBaseWorkstation implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String workshopId;
    private String stationNo;

    private String name;
    private String location;
    private String content;
    private Integer status;

    private String remark;
    private Date createTime;
    private String createBy;
    private Date updateTime;
    private String updateBy;
    private String companyName;
    private String depotName;
    private String workshopName;


}
