package org.jeecg.modules.third.workshopdb.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 车间故障 来自sqlserver数据库
 * </p>
 *
 * @author zhaiyantao
 * @since 2023-03-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "故障总表对象", description = "故障总表")
@TableName("故障总表")
public class WSFault {

    /**
     *
     */
    private Integer 序号;
    /**
     *
     */
    private String 故障序号;
    /**
     *
     */
    private String 类别;
    /**
     *
     */
    private String 日期;
    /**
     *
     */
    private String 接报时间;
    /**
     *
     */
    private String 发生时间;
    /**
     *
     */
    private String 响应日期;
    /**
     *
     */
    private String 响应时间;
    /**
     *
     */
    private String 报告人;
    /**
     *
     */
    private String 接报人;
    /**
     *
     */
    private String 填报人;
    /**
     *
     */
    private String 故障单号;
    /**
     *
     */
    private String 列车号;
    /**
     *
     */
    private String 车辆号;
    /**
     *
     */
    private String 故障描述;
    /**
     *
     */
    private String 对应的故障描述条款;
    /**
     *
     */
    private String 系统;
    /**
     *
     */
    private String 设备类别;
    /**
     *
     */
    private String 故障等级;
    /**
     *
     */
    private String 故障影响;
    /**
     *
     */
    private String 处理结果;
    /**
     *
     */
    private String 故障处理详细情况;
    /**
     *
     */
    private String 故障处理方式;
    /**
     *
     */
    private String 属性;
    /**
     *
     */
    private String 修复日期;
    /**
     *
     */
    private String 修复时间;
    /**
     *
     */
    private String 是否完成维修服务承诺;
    /**
     *
     */
    private String 未完成维修服务承诺原因;
    /**
     *
     */
    private String 处理人;
    /**
     *
     */
    private String 跟踪情况;
    /**
     *
     */
    private String 备注;
    /**
     *
     */
    private String 故障单状态;
    /**
     *
     */
    private String 线别;
    /**
     *
     */
    private String 段场;
    /**
     *
     */
    private String 里程数;
    /**
     *
     */
    private String 是否需求故障分析报告;
    /**
     *
     */
    private String 故障分析报告文件名;
    /**
     *
     */
    private String 故障分析报告上传状态;
    /**
     *
     */
    private String 是否开具故障单;
    /**
     *
     */
    private String 故障单文件名称;
    /**
     *
     */
    private String 故障单上传状态;
    /**
     *
     */
    private String 是否为重要故障;
    /**
     *
     */
    private String 重要故障内容;
    /**
     *
     */
    private String 重要故障更新日期;
    /**
     *
     */
    private String 重要故障更新人;
    /**
     *
     */
    private String 删除人;
    /**
     *
     */
    private String 删除日期;
    /**
     *
     */
    private String 响应间隔;
    /**
     *
     */
    private String 修复间隔;
    /**
     *
     */
    private String 部件;
    /**
     *
     */
    private String 旧故障序号;
    /**
     *
     */
    private String 转移状态;

}
