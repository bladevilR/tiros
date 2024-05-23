package org.jeecg.modules.basemanage.doc.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 目录信息
 * 1 顶级目录(默认创建)：个人文件、共享文件、班组文件、工艺文件，通过id查找区别，分别对应1、2
 * </p>
 *
 * @author youGen
 * @since 2020-08-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuDocDirectory对象", description = "目录信息 1 顶级目录(默认创建)：个人文件、共享文件、班组文件、工艺文件，通过id查找区别，分别对应1、2")
@TableName("bu_doc_directory")
public class BuDocDirectory extends Model<BuDocDirectory> {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "默认目录id:  1 个人文件、2 共享文件、3 班组文件")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "目录名称",required = true)
    @NotBlank
    private String name;

    @ApiModelProperty(value = "上级目录，顶层目录为null，不设置任何值")
    private String parentId;

    @ApiModelProperty(value = "是否隐藏，为“是”时，表示隐藏不在文档浏览中显示")
    private Integer hidden;

    @ApiModelProperty(value = "创建类型：1 系统初始化（任何人不能删除)  2  用户创建 (只有创建人员才能删除，除管理员外则同时需要授予了权限)")
    @Dict(dicCode = "bu_create_type")
    private Integer createType;

    @ApiModelProperty(value = "备注")
    private String remark;

    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "是否删除")
    private Integer status;

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;

    @ApiModelProperty(value = "线路id")
    private String lineId;


    @ApiModelProperty(value = "子节点")
    @TableField(exist = false)
    private List<BuDocDirectory> children;

    @ApiModelProperty(value = "所属对象id")
    @TableField(exist = false)
    private String belonger;

    @ApiModelProperty(value = "是否有子节点")
    @TableField(exist = false)
    private boolean hasChildren;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
