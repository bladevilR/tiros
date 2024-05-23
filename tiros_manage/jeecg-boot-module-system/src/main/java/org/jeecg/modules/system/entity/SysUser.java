package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_user")
public class SysUser implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户id 主键")
    @TableId(type = IdType.ID_WORKER_STR)
    private String id;

    @ApiModelProperty(value = "登录账号")
    @Excel(name = "登录账号", width = 15)
    private String username;

    @ApiModelProperty(value = "真实姓名")
    @Excel(name = "真实姓名", width = 15)
    private String realname;

    @ApiModelProperty(value = "密码")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @ApiModelProperty(value = "md5密码盐")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String salt;

    @ApiModelProperty(value = "头像")
    @Excel(name = "头像", width = 15, type = 2)
    private String avatar;

    @ApiModelProperty(value = "生日")
    @Excel(name = "生日", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    @ApiModelProperty(value = "性别（1：男 2：女）")
    @Excel(name = "性别", width = 15, dicCode = "sex")
    @Dict(dicCode = "sex")
    private Integer sex;

    @ApiModelProperty(value = "电子邮件")
    @Excel(name = "电子邮件", width = 15)
    private String email;

    @ApiModelProperty(value = "电话")
    @Excel(name = "电话", width = 15)
    private String phone;

    @ApiModelProperty(value = "部门code(当前选择登录部门)")
    private String orgCode;

    @ApiModelProperty(value = "状态(1：正常  2：冻结 ）")
    @Excel(name = "状态", width = 15, dicCode = "user_status")
    @Dict(dicCode = "user_status")
    private Integer status;

    @ApiModelProperty(value = "删除状态（0，正常，1已删除）")
    @Excel(name = "删除状态", width = 15, dicCode = "del_flag")
    private Integer delFlag;

    @ApiModelProperty(value = "工号，唯一键")
    @Excel(name = "工号", width = 15)
    private String workNo;

    @ApiModelProperty(value = "职务，关联职务表")
    @Excel(name = "职务", width = 15)
    private String post;

    @ApiModelProperty(value = "座机号")
    @Excel(name = "座机号", width = 15)
    private String telephone;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "同步工作流引擎 1同步0不同步")
    private Integer activitiSync;

    @ApiModelProperty(value = "身份（0 普通成员 1 上级）")
    @Excel(name = "（1普通成员 2上级）", width = 15)
    private Integer userIdentity;

    @ApiModelProperty(value = "负责部门")
    @Excel(name = "负责部门", width = 15, dictTable = "sys_depart", dicText = "depart_name", dicCode = "id")
    @Dict(dictTable = "sys_depart", dicText = "depart_name", dicCode = "id")
    private String departIds;

    @ApiModelProperty(value = "是否来自uuv同步")
    @Dict(dicCode = "bu_state")
    private Integer isSync;


    @ApiModelProperty(value = "部门id(班组成员=班组id，车间成员=车间id) 数据表不存在字段，不可做条件查询使用")
    @TableField(exist = false)
    private String departId;

    @ApiModelProperty(value = "当前部门是否是班组 true表示是，空或false表示不是")
    @TableField(exist = false)
    private Boolean departIsGroup;

    @ApiModelProperty(value = "车辆段id 数据表不存在字段，不可做条件查询使用")
    @TableField(exist = false)
    private String depotId;

    @ApiModelProperty(value = "用户角色id列表 数据表不存在字段，不可做条件查询使用")
    @TableField(exist = false)
    private List<String> roles;

    @ApiModelProperty(value = "用户角色名称，多个逗号分割 数据表不存在字段，不可做条件查询使用")
    @TableField(exist = false)
    private String roleNames;

    @ApiModelProperty(value = "用户权限列表 数据表不存在字段，不可做条件查询使用")
    @TableField(exist = false)
    private Set<String> permissions;

    @ApiModelProperty(value = "第三方登录的唯一标识 数据表不存在字段，不可做条件查询使用")
    @TableField(exist = false)
    private String thirdId;

    @ApiModelProperty(value = "第三方类型 数据表不存在字段，不可做条件查询使用")
    @TableField(exist = false)
    private String thirdType;

    @ApiModelProperty(value = "公司id 数据表不存在字段，不可做条件查询使用")
    @TableField(exist = false)
    private String companyId;

    @ApiModelProperty(value = "车间id 数据表不存在字段，不可做条件查询使用")
    @TableField(exist = false)
    private String workshopId;

    @ApiModelProperty(value = "线路id(多个逗号分割) 数据表不存在字段，不可做条件查询使用")
    @TableField(exist = false)
    private String lineId;

}
