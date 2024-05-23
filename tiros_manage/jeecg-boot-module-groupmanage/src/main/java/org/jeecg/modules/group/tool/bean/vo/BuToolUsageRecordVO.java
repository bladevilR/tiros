package org.jeecg.modules.group.tool.bean.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * <p>
 * 工器具使用记录 vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-23
 */
@Data
@Accessors(chain = true)
public class BuToolUsageRecordVO {

    @ApiModelProperty(value = "id")
    private String toolId;

    @ApiModelProperty(value = "类型 字典dictCode=bu_tools_type")
    @Dict(dicCode = "bu_tools_type")
    private Integer toolType;

    @ApiModelProperty(value = "编码")
    private String code;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "工单id")
    private String orderId;

    @ApiModelProperty(value = "工单编码")
    private String orderCode;

    @ApiModelProperty(value = "工单名称")
    private String orderName;

    @ApiModelProperty(value = "工单任务id")
    private String orderTaskId;

    @ApiModelProperty(value = "工单任务编码")
    private String orderTaskNo;

    @ApiModelProperty(value = "工单任务名称")
    private String orderTaskName;

    @ApiModelProperty(value = "线路id")
    private String lineId;

    @ApiModelProperty(value = "线路名称")
    private String lineName;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "设备类型id")
    private String sysId;

    @ApiModelProperty(value = "设备类型名称")
    private String sysName;

    @ApiModelProperty(value = "使用时间 取工单任务开始时间")
    private Date userDate;

    @ApiModelProperty(value = "工班id")
    private String groupId;

    @ApiModelProperty(value = "工班名称")
    private String groupName;

}
