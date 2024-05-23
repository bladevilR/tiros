package org.jeecg.modules.tiros.importer.service;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.jeecg.modules.tiros.importer.bean.conf.ImportConfig;
import org.jeecg.modules.tiros.importer.dao.ImporterDao;
import org.jeecg.modules.tiros.importer.entity.SysUser;
import org.jeecg.modules.tiros.importer.utils.ConvertUtil;
import org.jeecg.modules.tiros.importer.utils.ExcelUtil;
import org.jeecg.modules.tiros.importer.utils.PasswordUtil;
import org.jeecg.modules.tiros.importer.utils.UUIDUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yyg
 */
public class SysUserImport {

    private String path;
    private Connection con;
    private List<SysUser> sysUsers = new ArrayList<>();

    public SysUserImport(String path, Connection con) {
        this.path = path;
        this.con = con;
    }

    public void importSysUser(ImportConfig importConfig) {
        try {
            String workshopId = "CJ1";
            if (StringUtils.isNotBlank(importConfig.getWorkshopId())) {
                workshopId = importConfig.getWorkshopId();
            }
            Sheet sheet = ExcelUtil.getWorkbook(path).getSheet("10月需更新");
            int rowCount = sheet.getLastRowNum();
            int colCount = sheet.getRow(2).getLastCellNum();
            for (int i = 2; i <=234; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                SysUser user = new SysUser();
                user.setId(UUIDUtils.getUUID());
                String workNo="";
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
                        if (j == 3) {
                            Map<String, String> stringStringMap = ImporterDao.selectWorkGroupNameIdMap(con, "'" + cellValue + "'");
                            String deptId=stringStringMap.get(cellValue);
                             user.setDeptId(StrUtil.isNotBlank(deptId)?deptId:"jdx");
                        } else if (j == 7) {
                            workNo=cellValue.contains(".")?cellValue.substring(0,cellValue.lastIndexOf(".")):cellValue;
                            user.setWorkNo(workNo);
                        }else if(j==8){
                            user.setRealname(cellValue);
                        }else if(j==9){
                            user.setSex(cellValue.equals("男")?1:2);
                        }else if(j==10){
                            user.setPositionId(ImporterDao.selectPositionIdByName(con,cellValue));
                        }

                    }

                }
                user.setUsername(workNo);
                String salt = ConvertUtil.randomGen(8);
                user.setSalt(salt);
                String passwordEncode = PasswordUtil.encrypt(user.getUsername(), user.getUsername() ,salt);
                user.setPassword(passwordEncode);
                user.setStatus(1);
                user.setRoleId("1324276437565313026");
                sysUsers.add(user);
            }
           ImporterDao.saveUser(con, sysUsers);
           ImporterDao.saveUserDepart(con,sysUsers);
           ImporterDao.saveUserExtend(con,sysUsers);
           ImporterDao.saveUserRole(con,sysUsers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
