package org.jeecg.modules.message.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import java.util.Date;

/**
 * @author yyg
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bu_mail_send_message")
public class BuMailSendMessage extends Model<BuMailSendMessage> {

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "主题")
    private String title;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "接收邮箱")
    private String receiveMail;

    @ApiModelProperty(value = "消息来源id")
    private String fromId;

    @ApiModelProperty(value = "附件地址")
    private String filePath;

    @ApiModelProperty(value = "是否生成通知 0否 1是")
    @Dict(dicCode = "bu_state")
    private Integer sendStatus;

    @JsonIgnore
    private Date createTime;

    @JsonIgnore
    private Date updateTime;


}
