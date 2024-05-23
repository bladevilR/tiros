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

/**
 * <p>
 * 班组创建的目录，班组文档管理员角色可进行授权
 *
 * </p>
 *
 * @author youGen
 * @since 2020-08-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuDocDirectoryGroup对象", description = "班组创建的目录，班组文档管理员角色可进行授权 ")
@TableName("bu_doc_directory_group")
public class BuDocDirectoryGroup extends Model<BuDocDirectoryGroup> {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "默认目录id:  1 个人文件、2 共享文件、3 班组文件")
    @Dict(dicCode = "bu_directory_type")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "目录名称")
    private String name;

    @ApiModelProperty(value = "上级目录，顶层目录的上次目录为 1 ，即目录信息表中的 个人目录id")
    private String parentId;

    @ApiModelProperty(value = "归属班组id")
    private String belonger;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "备注")
    private String remark;

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
    private List<BuDocDirectoryGroup> children;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
