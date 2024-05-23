package org.jeecg.modules.basemanage.doc.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

/**
 * <p>
 * 可以对目录授权，也可以对文件授权，权限以最底层节点的权限为准，底层无授权信息则继承上层的设置，如果没有任何授权则表示没有
 * </p>
 *
 * @author youGen
 * @since 2020-08-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuDocPrivilege对象", description = "可以对目录授权，也可以对文件授权，权限以最底层节点的权限为准，底层无授权信息则继承上层的设置，如果没有任何授权则表示没有")
@TableName("bu_doc_privilege")
public class BuDocPrivilege extends Model<BuDocPrivilege> {
    @JsonIgnore
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;
    @ApiModelProperty(value = "权限对象类型：1 目录  2 文件")
    @Dict(dicCode = "bu_obj_type")
    private Integer objType;
    @ApiModelProperty(value = "权限对象id")
    private String objId;
    @ApiModelProperty(value = "权限对象名称")
    @TableField(exist = false)
    private String objName;
    @ApiModelProperty(value = "是否设置子项")
    @TableField(exist = false)
    private Boolean isSub;
    @ApiModelProperty(value = "表示被授予对象的维度，1 人员  2 角色  3  机构")
    @Dict(dicCode = "bu_dimension")
    private Integer targetType;

    @ApiModelProperty(value = "根据授予对象id")
    private String targetId;

    @ApiModelProperty(value = "拥有权限:5位字符，0或1的字符组成，0表示无权限，1表示有权限，如：00010，每位对应权限项：1 上传文件  2查看文件  3 下载文件  4  删除文件  5  目录管理")
    private String docPrivileges;
    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
