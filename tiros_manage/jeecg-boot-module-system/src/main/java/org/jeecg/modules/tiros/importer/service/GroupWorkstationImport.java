package org.jeecg.modules.tiros.importer.service;

import cn.hutool.core.util.StrUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.jeecg.modules.tiros.importer.bean.conf.ImportConfig;
import org.jeecg.modules.tiros.importer.dao.ImporterDao;
import org.jeecg.modules.tiros.importer.entity.BuGroupWorkstation;
import org.jeecg.modules.tiros.importer.utils.ExcelUtil;
import org.jeecg.modules.tiros.importer.utils.UUIDUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yyg
 */
public class GroupWorkstationImport {

    private String path;
    private Connection con;
    private List<BuGroupWorkstation> groupWorkstations = new ArrayList<>();

    public GroupWorkstationImport(String path, Connection con) {
        this.path = path;
        this.con = con;
    }

    public void importWorkstation(ImportConfig importConfig) {
        try {

            Sheet sheet = ExcelUtil.getWorkbook(path).getSheetAt(0);
            int rowCount = sheet.getLastRowNum();
            int colCount = sheet.getRow(1).getLastCellNum();
            String groupId="";
            for (int i = 1; i <= rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                BuGroupWorkstation gw = new BuGroupWorkstation();
                gw.setId(UUIDUtils.getUUID());
                Integer lastRow=0;
                for (int j = 1; j < colCount; j++) {
                    Cell cell = row.getCell(j);
                    boolean isMerge;
                    String cellValue;
                    isMerge = ExcelUtil.isMerge(sheet, i, cell)!=0;
                    if (isMerge) {
                        cellValue = ExcelUtil.getMergedRegionValueAndInfo(sheet, row.getRowNum(), cell.getColumnIndex());
                    } else {
                        cellValue = ExcelUtil.getCellValue(cell);
                    }
                    if (StrUtil.isNotBlank(cellValue)) {
                        if (j == 1) {
                            if(cellValue.contains("&")&& i>lastRow){
                                String[] value= cellValue.split("&");
                                Map<String, String> stringStringMap = ImporterDao.selectWorkGroupNameIdMap(con, "'"+value[0]+"'");
                                groupId=stringStringMap.get(value[0]);
                                lastRow=Integer.valueOf(value[1].split(",")[3]);
                            }
                        } else if (j == 2) {
                            gw.setGroupId(groupId);
                            gw.setWorkstationId(ImporterDao.selectWorkstationIdByName(con,cellValue));
                        }
                    }

                }
                groupWorkstations.add(gw);
            }
           ImporterDao.saveGroupWorkstation(con, groupWorkstations);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
