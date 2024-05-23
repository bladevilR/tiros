package org.jeecg.modules.basemanage.doc.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 文件信息
 * </p>
 *
 * @author youGen
 * @since 2020-08-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuDocFile对象", description = "文件信息")
@TableName("bu_doc_file")
public class BuDocFile extends Model<BuDocFile> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "文件名称")
    private String name;

    @ApiModelProperty(value = "文件类型：存放小写的文件扩展名称,如：.pdf")
    private String type;

    @ApiModelProperty(value = "文件存放的路径，这里存放的是相对路径")
    private String savepath;

    @ApiModelProperty(value = "单位为KB")
    private Long fileSize;

    @ApiModelProperty(value = "所属目录id")
    private String directoryId;

    @ApiModelProperty(value = "目录类型：1  个人目录   2 共享目录  3  班组目录")
    @Dict(dicCode = "bu_directory_type")
    private Integer directoryType;

    @ApiModelProperty(value = "文件所属对象id，根据目录类型不同，对象id不同，如存放在个人目录中，则所属对象id为相应人员的id")
    private String belonger;

    @ApiModelProperty(value = "标签,多个用逗号分隔")
    private String fileTags;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "是否删除")
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;

    @ApiModelProperty(value = "线路id")
    private String lineId;


    @ApiModelProperty(value = "共享到目录id")
    @TableField(exist = false)
    private String sharedId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
