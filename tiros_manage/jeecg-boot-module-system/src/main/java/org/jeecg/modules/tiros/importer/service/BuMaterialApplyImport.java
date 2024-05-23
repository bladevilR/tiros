package org.jeecg.modules.tiros.importer.service;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrder;
import org.jeecg.modules.dispatch.workorder.mapper.BuWorkOrderMapper;
import org.jeecg.modules.material.apply.bean.BuMaterialApply;
import org.jeecg.modules.material.apply.bean.BuMaterialApplyDetail;
import org.jeecg.modules.material.apply.mapper.BuMaterialApplyDetailMapper;
import org.jeecg.modules.material.apply.mapper.BuMaterialApplyMapper;
import org.jeecg.modules.material.material.bean.BuMaterialType;
import org.jeecg.modules.material.material.mapper.BuMaterialTypeMapper;
import org.jeecg.modules.tiros.importer.entity.BuMaterialApplyBO;
import org.jeecg.modules.tiros.importer.utils.ExcelUtil;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yyg
 * @title: importMaterialTools
 * @projectName tiros_manage
 * @description: TODO
 * @date 2021/7/26
 */
@Repository
public class BuMaterialApplyImport {

    @Resource
    private BuMaterialTypeMapper buMaterialTypeMapper;
    @Resource
    private BuWorkOrderMapper workOrderMapper;
    @Resource
    private BuMaterialApplyMapper materialApplyMapper;
    @Resource
    private BuMaterialApplyDetailMapper applyDetailMapper;

    @Transactional(rollbackFor = Exception.class)
    public void execute(String fileName, InputStream inputStream) throws Exception {

        Sheet sheet = ExcelUtil.getWorkbookByStream(fileName, inputStream).getSheetAt(0);
        int lastRowNum = sheet.getLastRowNum();
        int lastCellNum = sheet.getRow(0).getLastCellNum();
        List<BuMaterialApplyBO> materialApplyBOList = new ArrayList<>();
        try {
            for (int i = 4; i < lastRowNum; i++) {
                Row row = sheet.getRow(i);
                BuMaterialApplyBO materialApplyBO = null;
                for (int j = 2; j < lastCellNum; j++) {
                    if (j == 2 || j == 5 || j == 8) {
                        Cell cell = row.getCell(j);
                        String cellValue = ExcelUtil.getCellValue(cell).trim();
                        if (j == 2) {
                            if (StringUtils.isNotBlank(cellValue)) {
                                materialApplyBO = new BuMaterialApplyBO()
                                        .setMaterialTypeCode(cellValue.replace("'",""));
                            } else {
                                break;
                            }
                        }
                        if (j == 5) {
                            if (materialApplyBO != null) {
                                if (StringUtils.isNotBlank(cellValue)) {
                                    materialApplyBO.setPrice(Double.parseDouble(cellValue));
                                } else {
                                    materialApplyBO.setPrice(0D);
                                }
                            }
                        }
                        if (j == 8) {
                            if (materialApplyBO != null) {
                                materialApplyBO.setWorkOrderCode(cellValue);
                            }
                        }
                    } else {
                        continue;
                    }
                    if (materialApplyBO != null) {
                        String workOrderCode = materialApplyBO.getWorkOrderCode();
                        if (StringUtils.isNotBlank(workOrderCode) && workOrderCode.contains(":")) {
                            String[] workOrderCodeArray = workOrderCode.split(":");
                            if (workOrderCodeArray[1].startsWith("J")) {
                                materialApplyBO.setWorkOrderCode(workOrderCodeArray[1]);
                                materialApplyBOList.add(materialApplyBO);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new JeecgBootException("文件内容格式不正确!");
        }
        try {
            if (CollectionUtils.isNotEmpty(materialApplyBOList)) {
                Set<String> materialCodeList = materialApplyBOList.stream().map(BuMaterialApplyBO::getMaterialTypeCode).filter(item -> StringUtils.isNotBlank(item)).collect(Collectors.toSet());
                Set<String> workOrderCodeList = materialApplyBOList.stream().map(BuMaterialApplyBO::getWorkOrderCode).filter(item -> StringUtils.isNotBlank(item)).collect(Collectors.toSet());
                //物资类型
                List<BuMaterialType> materialTypeList = buMaterialTypeMapper.selectListByCode(materialCodeList);
                Map<String, String> materialTypeMap = new HashMap<>(16);
                materialTypeList.stream().forEach(item -> materialTypeMap.put(item.getCode(), item.getId()));

                //工单
                List<BuWorkOrder> workOrderList = workOrderMapper.selectListByCode(workOrderCodeList);
                Map<String, String> workOrderMap = new HashMap<>();
                workOrderList.stream().forEach(item -> workOrderMap.put(item.getOrderCode(), item.getId()));

                //物料领用
                List<String> workOrderIdList = workOrderList.stream().map(BuWorkOrder::getId).collect(Collectors.toList());
                List<BuMaterialApply> materialApplyList = new ArrayList<>();
                Map<String, String> materialApplyMap = new HashMap<>();
                List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(workOrderIdList);
                batchSubList.stream().forEach(item -> {
                    List<BuMaterialApply> materialApply = materialApplyMapper.selectList(Wrappers.<BuMaterialApply>lambdaQuery()
                            .select(BuMaterialApply::getId,BuMaterialApply::getWorkOrderId).in(BuMaterialApply::getWorkOrderId, item));
                    materialApplyList.addAll(materialApply);
                });
              //  materialApplyList.stream().forEach(item->materialApplyMap.put(item.getWorkOrderId(),item.getId()));
                //物料领用明细
                List<String> materialApplyIdList = materialApplyList.stream().map(BuMaterialApply::getId).collect(Collectors.toList());
                List<BuMaterialApplyDetail> materialApplyDetailList = new ArrayList<>();
                List<List<String>> materialApplyIdSubList = DatabaseBatchSubUtil.batchSubList(materialApplyIdList);
                materialApplyIdSubList.stream().forEach(item -> {
                    List<BuMaterialApplyDetail> materialApplyDetails = applyDetailMapper.selectList(Wrappers.<BuMaterialApplyDetail>lambdaQuery().in(BuMaterialApplyDetail::getApplyId, item));
                    materialApplyDetailList.addAll(materialApplyDetails);
                });
                //更新物料领用明细价格
                List<BuMaterialApplyDetail> updateMaterialApplyDetailList = new ArrayList<>();
                materialApplyBOList.stream().forEach(updateItem -> {
                    String workOrderId = workOrderMap.get(updateItem.getWorkOrderCode());
                    String materialTypeId = materialTypeMap.get(updateItem.getMaterialTypeCode());
                    Set<String> applyIdList = materialApplyList.stream().filter(item -> item.getWorkOrderId().equals(workOrderId)).map(BuMaterialApply::getId).collect(Collectors.toSet());
                    if(applyIdList.size()>0) {
                        List<BuMaterialApplyDetail> finds = materialApplyDetailList.stream().filter(detail -> applyIdList.contains(detail.getApplyId()) && detail.getMaterialTypeId().equals(materialTypeId)).collect(Collectors.toList());
                        if (finds.size() > 0) {
                            finds.forEach(d -> {
                                d.setUnitPrice(BigDecimal.valueOf(updateItem.getPrice()));
                                updateMaterialApplyDetailList.add(d);
                            });
                        } else {
                            System.out.println("工单："+updateItem.getWorkOrderCode()+" 物料："+ updateItem.getMaterialTypeCode()+ "对应的领用明细找不到");
                        }
                    } else {
                        System.out.println("工单："+updateItem.getWorkOrderCode()+" 对应的领用单找不到");
                    }

                });

                /*
                materialApplyDetailList.stream().forEach(applyDetail -> {
                    materialApplyBOList.stream().forEach(updateItem -> {
                        String workOrderId = workOrderMap.get(updateItem.getWorkOrderCode());
                        String materialTypeId = materialTypeMap.get(updateItem.getMaterialTypeCode());
                        Set<String> applyIdList = materialApplyList.stream().filter(item -> item.getWorkOrderId().equals(workOrderId)).map(BuMaterialApply::getId).collect(Collectors.toSet());
                        if (applyIdList.contains(applyDetail.getApplyId()) &&  applyDetail.getMaterialTypeId().equals(materialTypeId)) {
                            if (updateItem.getPrice() <= 0) {
                                System.out.println("工单： " + workOrderId + " 物资：" + materialTypeId + " 价格==0");
                            }
                            applyDetail.setUnitPrice(BigDecimal.valueOf(updateItem.getPrice()));
                            updateMaterialApplyDetailList.add(applyDetail);
                        } else {
                            System.out.println("工单： " + workOrderId + " 物资：" + materialTypeId + " 不存在");
                        }
                    });
                });
                */
                if (CollectionUtils.isNotEmpty(updateMaterialApplyDetailList)) {
                    applyDetailMapper.updatePriceBatch(updateMaterialApplyDetailList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new JeecgBootException("导入失败");
        }
    }
}
