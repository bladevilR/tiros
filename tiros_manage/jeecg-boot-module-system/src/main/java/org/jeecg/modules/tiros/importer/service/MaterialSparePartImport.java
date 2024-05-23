package org.jeecg.modules.tiros.importer.service;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.material.material.bean.BuMaterialType;
import org.jeecg.modules.material.material.mapper.BuMaterialTypeMapper;
import org.jeecg.modules.material.sparepart.bean.BuMaterialSparePart;
import org.jeecg.modules.tiros.importer.dao.ImporterSql;
import org.jeecg.modules.tiros.importer.utils.EmptyUtils;
import org.jeecg.modules.tiros.importer.utils.ExcelUtil;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author yfy
 * @title: MaterialSparePartImport
 * @projectName tiros_manage
 * @description: TODO
 * @date 2021/5/7 16:35
 */
@Repository
public class MaterialSparePartImport {


    @Resource
    private ImporterSql importerSql;

    @Resource
    private BuMaterialTypeMapper materialTypeMapper;



    public void importMaterialSparePart(String fileName, InputStream inputStream) throws Exception{

        Sheet sheet = ExcelUtil.getWorkbookByStream(fileName, inputStream).getSheetAt(0);
        int firstRowNum = sheet.getFirstRowNum();
        int lastRowNum = sheet.getLastRowNum();
        int cellIndexMax = 16;

        List<BuMaterialSparePart> sparePartList = new ArrayList<>();

        Boolean isExistBuMaterialSparePart = false;

        // 获取登录用户信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userName = sysUser.getUsername();
        String userId = sysUser.getId();

        try {
            //读取每一行数据
            for (int rowIndex = 1; rowIndex < lastRowNum + 1; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                BuMaterialSparePart materialSparePart = new BuMaterialSparePart();


                for (int cellIndex = 0; cellIndex < cellIndexMax; cellIndex++) {
                    String value = ExcelUtil.getCellValue(row.getCell(cellIndex)).trim();
                    if (StrUtil.isNotEmpty(value)) {
                        if (cellIndex == 0) {
                            materialSparePart.setMaterialCode(value);
                            BuMaterialType materialType = findBuMaterialTypeByCode(value);
                            if (materialType != null){
                                materialSparePart.setMaterialTypeId(materialType.getId());
                            } else {
                                materialSparePart.setMaterialTypeId("-1");
                            }
                        } else if (cellIndex == 1){
                            String[] split = value.split("-\\[");
                            if (split.length >= 2) {
                                String name = split[0];
                                String spec = split[1].trim();
                                spec = spec.substring(0, spec.length() - 1);
                                spec = spec.replaceFirst("规格：", "").replaceFirst("规格:", "").trim();
                                materialSparePart.setName(name);
                                materialSparePart.setModel(spec);
                            }
                        }
                    }

                }
                materialSparePart.setPrice(new BigDecimal(0.0).stripTrailingZeros());
                materialSparePart.setLineId("3");
                materialSparePart.setStatus(1);
                materialSparePart.setCreateBy(userName);
                materialSparePart.setCreateTime(new Date());
                sparePartList.add(materialSparePart);

            }

        } catch (Exception e) {
            throw new JeecgBootException("导入失败!");
        }
        try {
            save(sparePartList);
        } catch (Exception e) {
            throw new JeecgBootException("导入失败!");
        }

    }




    private BuMaterialType findBuMaterialTypeByCode(String code){
        LambdaQueryWrapper<BuMaterialType> typeLambdaQueryWrapper = new LambdaQueryWrapper<BuMaterialType>()
                .eq(BuMaterialType :: getCode,code);

        BuMaterialType buMaterialType = materialTypeMapper.selectOne(typeLambdaQueryWrapper);

        return buMaterialType;
    }

    @Transactional(rollbackFor = Exception.class)
    public void save(List<BuMaterialSparePart> sparePartList) throws Exception {
        if (EmptyUtils.listNotEmpty(sparePartList)) {
            importerSql.saveMaterialSparePart(sparePartList);
        }

    }
}
