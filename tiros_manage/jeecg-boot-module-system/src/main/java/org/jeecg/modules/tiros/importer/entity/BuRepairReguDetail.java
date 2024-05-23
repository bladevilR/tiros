package org.jeecg.modules.tiros.importer.entity;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 规程内容, 注意：类型不同界面表单输入的不同
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-28
 */
@Data
public class BuRepairReguDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String reguId;
    private String no;
    private String title;
    private Integer type;
    private String safeNotice;
    private String remark;
    private String parentId;
    private Integer outsource;
    private Integer important;
    private String method;
    private String qualityLevel;
    private String assetTypeId;
    private Float workTime;
    private String requirement;
    private Date createTime;
    private String createBy;
    private Date updateTime;
    private String updateBy;
    private Boolean hasChildren; 
    private String parentName;

    private String wbs;
    private List<BuRepairReguDetail> children;

    List<BuRepairReguTechFile> reguTechFiles;
    List<BuRepairReguMaterial> reguMaterials;
    List<BuRepairReguForm> reguForms;
    List<BuRepairReguPerson> reguPersons;
    List<BuRepairReguTools> reguTools;

}
