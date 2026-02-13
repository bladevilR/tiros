package org.jeecg.modules.basemanage.jobguidebook.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BuJobGuideBookQueryVO {
    @ApiModelProperty(value = "线路")
    private String lineId;
    @ApiModelProperty(value = "车型")
    private String trainTypeId;
    @ApiModelProperty(value = "修程")
    private String repairProId;
    @ApiModelProperty(value = "项目")
    private String project;
    @ApiModelProperty(value = "名称或编码")
    private String formName;
    @ApiModelProperty(value = "编制人")
    private String creatorName;
    @ApiModelProperty(value = "是否模板 0-否 1-模板")
    private Integer templateFlag;
    @ApiModelProperty(value = "业务状态 DRAFT/REVIEWING/REVIEW_PASS/APPROVING/APPROVED/PUBLISHED/OBSOLETE")
    private String bizStatus;
}
