package org.jeecg.modules.dispatch.fault.bean.bo;

import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.ExcelImport;

import java.util.Date;

/**
 * <p>
 * 导入历史故障bo
 * </p>
 *
 * @author zhaiyantao
 * @since 2024-03-31
 */
@Data
@Accessors(chain = true)
public class FaultImportBO {

//    @ExcelImport(name = "序号")

    @ExcelImport(name = "故障分类")
    private String phase;

    @ExcelImport(name = "发生日期")
    private String happenTime;

    @ExcelImport(name = "车号")
    private String trainNo;

    @ExcelImport(name = "所属系统")
    private String sysName;

    @ExcelImport(name = "所属工位")
    private String workStationName;

//    @ExcelImport(name = "故障位置")

    @ExcelImport(name = "车辆故障详细描述")
    private String faultDesc;

    @ExcelImport(name = "车辆故障详细处理措施")
    private String solutionDesc;

    @ExcelImport(name = "故障等级")
    private String faultLevel;

//    @ExcelImport(name = "故障影响")
//    @ExcelImport(name = "完成情况")

    @ExcelImport(name = "故障类别")
    private String reportGroupName;

    @ExcelImport(name = "关闭日期")
    private String closeTime;

    @ExcelImport(name = "处理人")
    private String reportUserName;

//    @ExcelImport(name = "更换部件名称")

    @ExcelImport(name = "是否有责")
    private String hasDuty;

    @ExcelImport(name = "是否委外件故障")
    private String outsource;

//    @ExcelImport(name = "规程内要求情况")

}
