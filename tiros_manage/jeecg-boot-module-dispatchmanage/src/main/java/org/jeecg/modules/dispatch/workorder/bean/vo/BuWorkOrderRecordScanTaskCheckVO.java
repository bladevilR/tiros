package org.jeecg.modules.dispatch.workorder.bean.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 工单作业记录扫任务检查vo
 * </p>
 *
 * @author lidafeng
 * @since 2021/03/04
 */
@Data
@Accessors(chain = true)
public class BuWorkOrderRecordScanTaskCheckVO{

    @ApiModelProperty(value = "二维码表示的对象类型 TASK 作业记录表明细任务，PSON 个人二维码")
    private String targetType;

    @ApiModelProperty(value = "请求显示二维码的所属平台，PC 表示在浏览器WEB端显示， APP 表示来自手机APP展示")
    private String fromBy;

    @ApiModelProperty(value = "任务二维码唯一标识")
    private String uuid;

    @ApiModelProperty(value = "是否忽略填写 0否1是 字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer isIgnore;

    @ApiModelProperty(value = "忽略原因")
    private String ignoreDesc;

    @ApiModelProperty(value = "检查结果  字典dicCode=bu_work_order_record_check_result")
    @Dict(dicCode = "bu_work_order_record_check_result")
    private Integer result;

    @ApiModelProperty(value = "结果描述")
    private String resultDesc;

    @ApiModelProperty(value = "检查人二维码唯一标识")
    private String checkUserUuid;

    @ApiModelProperty(value = "检查人用户名")
    private String checkUserName;

    @ApiModelProperty(value = "检查人密码")
    private String checkUserPwd;

    @ApiModelProperty(value = "作业记录表ID")
    private String formInstId;

    @ApiModelProperty(value = "记录明细id，多个id逗号分隔")
    private String formDetails;

    @ApiModelProperty(value = "检查类型  字典dictCode=bu_work_order_record_check_type")
    @Dict(dicCode = "bu_work_order_record_check_type")
    private Integer checkType;

    @ApiModelProperty(value = "作业记录表类型 0=不是作业记录表 1=老版作业记录表 2=新版excel作业记录表")
    private Integer workRecordType;

    @ApiModelProperty(value = "第几列")
    private Integer colIndex;

}
