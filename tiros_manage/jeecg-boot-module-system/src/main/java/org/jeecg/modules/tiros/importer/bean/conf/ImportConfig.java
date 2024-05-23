package org.jeecg.modules.tiros.importer.bean.conf;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 导入文件路径配置
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-28
 */
@Data
@Accessors(chain = true)
public class ImportConfig {
    private String reguFilePath;
    private String workRecordDirectoryPath;
    private String tpPlanFilePath;
    private String workstationFilePath;
    private String trainStructFilePath;
    private String groupWorkstationFilePath;
    private String userFilePath;
    private String warehouseFilePath;
    private String materialTypeFilePath;

    private String lineId;
    private String repairProId;
    private String trainTypeId;
    private String workshopId;
    private String topWarehouseId;

    private String reguId;

}
