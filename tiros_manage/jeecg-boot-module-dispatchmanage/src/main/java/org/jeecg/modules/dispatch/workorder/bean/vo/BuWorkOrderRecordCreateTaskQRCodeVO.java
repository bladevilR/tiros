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
 * 工单作业记录生成任务二维码参数
 * </p>
 *
 * @author lidafeng
 * @since 2021/03/07
 */
@Data
@Accessors(chain = true)
public class BuWorkOrderRecordCreateTaskQRCodeVO {


    @ApiModelProperty(value = "二维码表示的对象类型 TASK 作业记录表明细任务，PSON 个人二维码", required = true)
    @NotBlank
    private String targetType;
    @ApiModelProperty(value = "请求显示二维码的所属平台，PC 表示在浏览器WEB端显示， APP 表示来自手机APP展示", required = true)
    @NotBlank
    private String fromBy;

    @ApiModelProperty(value = "工单ID", required = true)
    @NotBlank
    private String orderId;

    @ApiModelProperty(value = "检查类型  字典dictCode=bu_work_order_record_check_type", required = true)
    @Dict(dicCode = "bu_work_order_record_check_type")
    @NotNull
    private Integer checkType;

    @ApiModelProperty(value = "表单类型 1 在线表单（数据表单） 2 文件表单 3 作业记录表", required = true)
    @Dict(dicCode = "bu_form_type")
    @NotNull
    private Integer formType;


    @ApiModelProperty(value = "工单任务ID", required = true)
    @NotBlank
    private String taskId;

    @ApiModelProperty(value = "作业记录表ID", required = true)
    @NotBlank
    private String formInstId;

    @ApiModelProperty(value = "记录明细id，多个id逗号分隔", required = true)
    @NotBlank
    private String formDetails;

    @ApiModelProperty(value = "UUID")
    private String UUID;

    @ApiModelProperty(value = "是否被扫描")
    private int isScan;

    @ApiModelProperty(value = "作业记录表类型 0=不是作业记录表 1=老版作业记录表 2=新版excel作业记录表")
    private Integer workRecordType;

    @ApiModelProperty(value = "所处列数")
    private Integer colIndex;
    @ApiModelProperty(value = "来个User",hidden = true)
    private String formUserId;

    @Override
    public String toString() {
        return "orderId='" + orderId + '\'' +
                ", checkType=" + checkType +
                ", taskId='" + taskId + '\'' +
                ", formInstId='" + formInstId + '\'' +
                ", formDetails='" + formDetails + '\'';
    }
}
