package org.jeecg.modules.group.safeassume.bean;

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
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 安全预想
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "安全预想对象", description = "安全预想")
@TableName("bu_work_safe_assume")
public class BuWorkSafeAssume extends Model<BuWorkSafeAssume> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "标题", required = true)
    @NotBlank
    private String title;

    @ApiModelProperty(value = "日期 yyyy-MM-dd", required = true)
    @NotNull
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date readDate;

    @ApiModelProperty(value = "预想内容", required = true)
    @NotBlank
    private String content;

    @ApiModelProperty(value = "工班id", required = true)
    @NotBlank
    private String groupId;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;


    @ApiModelProperty(value = "创建人员名称")
    @TableField(exist = false)
    private String createUserName;

    @ApiModelProperty(value = "已读人数")
    @TableField(exist = false)
    private Integer readUserCount;

    @ApiModelProperty(value = "已读人员列表 逗号分隔的字符串")
    @TableField(exist = false)
    private String readUserListStr;

    @ApiModelProperty(value = "未读人员列表 逗号分隔的字符串")
    @TableField(exist = false)
    private String unReadUserListStr;

    @ApiModelProperty(value = "安全预想阅读记录列表")
    @TableField(exist = false)
    @JsonIgnore
    private List<BuWorkSafeAssumeRead> readList;

    @ApiModelProperty(value = "需要阅读人员列表(只有userId和userName字段)")
    @TableField(exist = false)
    @JsonIgnore
    private List<BuWorkSafeAssumeRead> needReadUserList;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
