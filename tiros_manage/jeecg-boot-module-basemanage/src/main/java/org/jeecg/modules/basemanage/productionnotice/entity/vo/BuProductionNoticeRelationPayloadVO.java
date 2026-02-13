package org.jeecg.modules.basemanage.productionnotice.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel(value = "生产通知单关联数据", description = "生产通知单关联的作业记录表和附件")
public class BuProductionNoticeRelationPayloadVO implements Serializable {

    @ApiModelProperty(value = "关联作业记录表")
    private List<RelatedForm> relatedForms = new ArrayList<>();

    @ApiModelProperty(value = "关联附件")
    private List<RelatedFile> relatedFiles = new ArrayList<>();

    @Data
    @ApiModel(value = "生产通知单关联作业记录表")
    public static class RelatedForm implements Serializable {
        @ApiModelProperty(value = "作业记录表ID")
        private String id;

        @ApiModelProperty(value = "作业记录表编码")
        private String code;

        @ApiModelProperty(value = "作业记录表名称")
        private String title;

        @ApiModelProperty(value = "作业记录表类型 0=非作业记录表 1=老版 2=新版")
        private Integer workRecordType;
    }

    @Data
    @ApiModel(value = "生产通知单关联附件")
    public static class RelatedFile implements Serializable {
        @ApiModelProperty(value = "文件ID")
        private String id;

        @ApiModelProperty(value = "文件名称")
        private String name;

        @ApiModelProperty(value = "文件类型")
        private String type;

        @ApiModelProperty(value = "文件路径")
        private String savepath;

        @ApiModelProperty(value = "文件大小(KB)")
        private Long fileSize;
    }
}

