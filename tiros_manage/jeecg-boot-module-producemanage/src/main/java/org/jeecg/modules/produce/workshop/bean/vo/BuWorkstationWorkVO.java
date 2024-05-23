package org.jeecg.modules.produce.workshop.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 工位作业信息vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/30
 */
@Data
@Accessors(chain = true)
public class BuWorkstationWorkVO {

    @ApiModelProperty(value = "工位id")
    private String id;

    @ApiModelProperty(value = "工位位置")
    private String location;

    @ApiModelProperty(value = "工位号")
    private String stationNo;

    @ApiModelProperty(value = "工位名称")
    private String name;

    @ApiModelProperty(value = "作业班组")
    private String groupName;

    @ApiModelProperty(value = "作业内容")
    private String content;

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @ApiModelProperty(value = "作业状态 1作业正常 2发现故障 3无作业")
    private Integer workStatus;

    @ApiModelProperty(value = "班长")
    private String monitor;
//
//    @ApiModelProperty(value = "质检人员")
//    private String checkUserName;

}
