package org.jeecg.modules.basemanage.org.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.modules.basemanage.org.bean.bo.BuDepartBO;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 人员信息
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-19
 */
@Data
@Accessors(chain = true)
@TableName("sys_user")
public class BuUser {

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "登陆账号")
    private String username;

    @ApiModelProperty(value = "真实姓名")
    private String realname;

    @ApiModelProperty(value = "工号")
    private String workNo;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "当前部门编码")
    private String orgCode;

    @Excel(name = "性别 字典dictCode=sex")
    @Dict(dicCode = "sex")
    private Integer sex;

    @ApiModelProperty(value = "电话")
    private String phone;
    @ApiModelProperty(value = "固定电话")
    private String telephone;

    @ApiModelProperty(value = "邮箱")
    private String email;


    @ApiModelProperty(value = "班组id")
    @TableField(exist = false)
    private String groupId;

    @ApiModelProperty(value = "班组名称")
    @TableField(exist = false)
    private String groupName;

    @ApiModelProperty(value = "岗位id")
    @TableField(exist = false)
    private String positionId;

    @ApiModelProperty(value = "岗位名称")
    @TableField(exist = false)
    private String positionName;

    @ApiModelProperty(value = "岗位工资")
    @TableField(exist = false)
    private BigDecimal positionWages;

    @ApiModelProperty(value = "地址")
    @TableField(exist = false)
    private String address;

    @ApiModelProperty(value = "状态  字典dictCode=bu_valid_status")
    @Dict(dicCode = "bu_valid_status")
    private Integer status;

    @ApiModelProperty(value = "技能标签 逗号拼接字符串")
    @TableField(exist = false)
    private String tags;

    @ApiModelProperty(value = "证书情况 逗号拼接字符串")
    @TableField(exist = false)
    private String certs;

    @ApiModelProperty(value = "角色名称 逗号拼接字符串")
    @TableField(exist = false)
    private String roleNames;

    @ApiModelProperty(value = "技能标签名称列表")
    @TableField(exist = false)
    private List<String> tagTitleList;

    @ApiModelProperty(value = "证书列表")
    @TableField(exist = false)
    private List<BuUserCert> certList;

    @ApiModelProperty(value = "培训记录列表")
    @TableField(exist = false)
    private List<BuTraining> trainingList;

    @ApiModelProperty(value = "技能分布列表")
    @TableField(exist = false)
    private List<BuUserSkill> skillList;

    @ApiModelProperty(value = "岗位列表")
    @TableField(exist = false)
    private List<BuPostionInfo> postionInfoList;

    @ApiModelProperty(value = "班长 ")
    @TableField(exist = false)
    private String monitor;

    @ApiModelProperty(value = "副班长 ")
    @TableField(exist = false)
    private String monitor2;

    @ApiModelProperty(value = "类型 ")
    @TableField(exist = false)
    private String type;

    @ApiModelProperty(value = "是否同步")
    private Integer isSync;



    /**
     * 用于后端根据当前选择的部门orgCode获取班组信息
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private List<BuDepartBO> departBOList;

}
