package org.jeecg.modules.dispatch.workorder.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 工单作业记录检查vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/12/9
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class BuWorkOrderRecordCheckVO extends CheckCommonVO {

    @ApiModelProperty(value = "检查人用户名", required = true)
    @NotBlank
    private String checkUserName;

    @ApiModelProperty(value = "检查人密码", required = true)
    @NotBlank
    private String checkUserPwd;

    @ApiModelProperty(value = "作业记录表类型 0=不是作业记录表 1=老版作业记录表 2=新版excel作业记录表", required = true)
    @NotNull
    private Integer workRecordType;

    @ApiModelProperty(value = "实例id", required = true)
    @NotBlank
    private String workRecordId;

    @ApiModelProperty(value = "第几列")
    private Integer colIndex;

}
