package org.jeecg.modules.dispatch.workorder.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * <p>
 * 检查记录表（实例）检查vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-18
 */
@Data
@Accessors(chain = true)
public class FormCheckRecordCheckVO {

    @ApiModelProperty(value = "检查人用户名",required = true)
    @NotBlank
    private String checkUserName;

    @ApiModelProperty(value = "检查人密码",required = true)
    @NotBlank
    private String checkUserPwd;

    @ApiModelProperty(value = "检查项id，多个逗号分割", required = true)
    @NotBlank
    private String itemIds;

    @ApiModelProperty(value = "检查情况 “检查情况”一栏详细记录检查情况")
    private String checkDesc;

    @ApiModelProperty(value = "结果 “结果”一栏对检查合格的画“√”不合格的画“×”；1合格 0不合格")
    @Dict(dicCode = "bu_work_check_result")
    private Integer checkResult;

    @ApiModelProperty(value = "检查方式 “检查方式”一栏写明检查手段，如：目视、测量、操作等")
    @Dict(dicCode = "bu_regu_method")
    private Integer checkMethod;

    @ApiModelProperty(value = "检查时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkTime;

}
