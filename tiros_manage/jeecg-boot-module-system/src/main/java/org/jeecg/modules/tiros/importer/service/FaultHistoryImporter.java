package org.jeecg.modules.tiros.importer.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.tiros.cache.assettype.AssetTypeCacheService;
import org.jeecg.common.tiros.cache.assettype.BuTrainAssetTypeBO;
import org.jeecg.common.tiros.serialnumber.SerialNumberGenerate;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.dispatch.fault.bean.BuFaultInfo;
import org.jeecg.modules.dispatch.fault.mapper.BuFaultInfoMapper;
import org.jeecg.modules.dispatch.workorder.bean.BuMtrWorkshopGroup;
import org.jeecg.modules.dispatch.workorder.mapper.BuMtrWorkshopGroupDispatchMapper;
import org.jeecg.modules.tiros.importer.dao.ImporterSql;
import org.jeecg.modules.tiros.importer.utils.ExcelUtil;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yyg
 */
@Repository
public class FaultHistoryImporter {

    @Resource
    private AssetTypeCacheService assetTypeCacheService;
    @Resource
    private ImporterSql importerSql;
    @Resource
    private BuFaultInfoMapper buFaultInfoMapper;
    @Resource
    private BuMtrWorkshopGroupDispatchMapper buMtrWorkshopGroupDispatchMapper;
    @Resource
    private SerialNumberGenerate serialNumberGenerate;


    public String execute(String fileName, InputStream inputStream, Integer type) throws Exception {
        try {
            Sheet sheet = ExcelUtil.getWorkbookByStream(fileName, inputStream).getSheetAt(0);
            int firstRowNum = sheet.getFirstRowNum();
            int lastRowNum = sheet.getLastRowNum();
            // 系统
            Map<String, BuTrainAssetTypeBO> idSysBOMap = assetTypeCacheService.mapSys(null);
            Map<String, String> systemNameIdMap = new HashMap<>();
            Map<String, String> systemShortNameIdMap = new HashMap<>();
            for (Map.Entry<String, BuTrainAssetTypeBO> idSysBOEntry : idSysBOMap.entrySet()) {
                BuTrainAssetTypeBO sysBO = idSysBOEntry.getValue();
                systemNameIdMap.put(sysBO.getName(), sysBO.getId());
                systemShortNameIdMap.put(sysBO.getShortName(), sysBO.getId());
            }
            // 获取系统id
            Map<String, String> workstationIdMap = importerSql.selectWorkstationIdMap();
            Map<String, String> groupIdMap = importerSql.selectWorkGroupNameMap();
            Map<String, String> userIdMap = importerSql.selectUserNameMap();
            String lineId = fileName.substring(0, fileName.indexOf("号"));
            // 班组
            List<BuMtrWorkshopGroup> groupList = buMtrWorkshopGroupDispatchMapper.selectAllGroupInfo();
            Map<String, BuMtrWorkshopGroup> idGroupMap = groupList.stream()
                    .collect(Collectors.toMap(BuMtrWorkshopGroup::getId, Function.identity()));
            // 故障
            List<BuFaultInfo> faultList = buFaultInfoMapper.selectList(Wrappers.emptyWrapper());

            List<BuFaultInfo> needAddFaultList = new ArrayList<>();
            int rowCount = 0;
            int existFaultCount = 0;
            for (int rowIndex = firstRowNum + 1; rowIndex < lastRowNum; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                BuFaultInfo fault = new BuFaultInfo()
                        .setId(UUIDGenerator.generate())
                        .setFaultSn(null)
                        .setPlanId("-1")
                        .setLineId(lineId)
                        .setHasDuty(0)
                        .setFaultOnline(0)
                        .setOutsource(0)
                        .setStatus(2)
                        .setSubmitStatus(1);
                for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
                    String cellValue = ExcelUtil.getCellValue(row.getCell(cellIndex)).trim();
                    //架修故障
                    if (type == 1) {
                        if (cellIndex == 1) {
                            fault.setPhase(parsePhase(cellValue));
                        }
                        if (cellIndex == 2) {
                            fault.setHappenTime(parseDate(cellValue, rowIndex, cellIndex));
                            fault.setReportTime(fault.getHappenTime());
                        }
                        if (cellIndex == 3) {
                            fault.setTrainNo(cellValue);
                        }
                        if (cellIndex == 4) {
                            // 系统id根据系统名称获取
                            String sysId = systemNameIdMap.get(cellValue);
                            if (org.apache.commons.lang3.StringUtils.isBlank(sysId)) {
                                sysId = systemShortNameIdMap.get(cellValue);
                            }
                            fault.setSysId(org.apache.commons.lang3.StringUtils.isBlank(sysId) ? "-" : sysId);
                        }
                        //所属工位
                        if (cellIndex == 5) {
                            if (StringUtils.isNotBlank(cellValue)) {
                                fault.setWorkStationId(workstationIdMap.get(cellValue));
                            }
                        }
                        if (cellIndex == 7) {
                            checkFaultDescLength(rowIndex, cellIndex, cellValue);
                            fault.setFaultDesc(cellValue);
                        }
                        if (cellIndex == 9) {
                            //故障等级
                            fault.setFaultLevel(parseFaultLevel(cellValue));
                        }
                        if (cellIndex == 12) {
                            //班组
                            String groupId = groupIdMap.get(cellValue);
                            BuMtrWorkshopGroup group = idGroupMap.get(groupId);
                            if (StringUtils.isBlank(groupId) || null == group) {
                                fault = null;
                                break;
                            }
                            fault.setReportGroupId(groupId)
                                    .setWorkshopId(group.getWorkshopId())
                                    .setCompanyId(group.getCompanyId());
                        }
                        //关闭时间
                        if (cellIndex == 13) {
                            Date closeDate = parseDate(cellValue, rowIndex, cellIndex);
                            fault.setCloseTime(closeDate);
                        }
                        if (cellIndex == 14) {
                            String userId = userIdMap.get(cellValue);
                            if (StringUtils.isBlank(userId)) {
                                userId = "-1";
                            }
                            fault.setReportUserId(userId);
                        }
                        if (cellIndex == 15) {
                        }
                        if (cellIndex == 16) {
                            if (StringUtils.isNotBlank(cellValue)) {
                                fault.setHasDuty("无".equals(cellValue) ? 0 : 1);
                            }
                        }
                        if (cellIndex == 17) {
                            if (StringUtils.isNotBlank(cellValue)) {
                                fault.setOutsource("是".equals(cellValue) ? 1 : 0);
                            }
                        }
                    } else {
                        //大修故障
                        if (cellIndex == 1) {
                            fault.setHappenTime(parseDate(cellValue, rowIndex, cellIndex));
                            fault.setReportTime(fault.getHappenTime());
                        }

                        if (cellIndex == 3) {
                            fault.setPhase(parsePhase(cellValue));
                        }

                        if (cellIndex == 4) {
                            //班组
                            String groupId = groupIdMap.get(cellValue);
                            BuMtrWorkshopGroup group = idGroupMap.get(groupId);
                            if (StringUtils.isBlank(groupId) || null == group) {
                                fault = null;
                                break;
                            }
                            fault.setReportGroupId(groupId)
                                    .setWorkshopId(group.getWorkshopId())
                                    .setCompanyId(group.getCompanyId());
                        }
                        if (cellIndex == 5) {
                            if (StringUtils.isNotBlank(cellValue)) {
                                fault.setWorkStationId(workstationIdMap.get(cellValue));
                            }
                        }
                        if (cellIndex == 6) {
                            String userId = userIdMap.get(cellValue);
                            if (StringUtils.isBlank(userId)) {
                                userId = "-1";
                            }
                            fault.setReportUserId(userId);
                        }
                        if (cellIndex == 8) {
                            fault.setTrainNo(cellValue);
                        }
                        if (cellIndex == 10) {
                            // 系统id根据系统名称获取
                            String sysId = systemNameIdMap.get(cellValue);
                            if (org.apache.commons.lang3.StringUtils.isBlank(sysId)) {
                                sysId = systemShortNameIdMap.get(cellValue);
                            }
                            fault.setSysId(org.apache.commons.lang3.StringUtils.isBlank(sysId) ? "-" : sysId);
                        }
                        if (cellIndex == 13) {
                            checkFaultDescLength(rowIndex, cellIndex, cellValue);
                            fault.setFaultDesc(cellValue);
                        }
                        if (cellIndex == 16) {
                            //故障等级
                            fault.setFaultLevel(parseFaultLevel(cellValue));
                        }
                        if (cellIndex == 22) {
                            if (StringUtils.isNotBlank(cellValue)) {
                                fault.setHasDuty("无".equals(cellValue) ? 0 : 1);
                            }
                        }
                        if (cellIndex == 23) {
                            Date closeDate = parseDate(cellValue, rowIndex, cellIndex);
                            fault.setCloseTime(closeDate);
                        }
                    }
                }
                if (fault != null) {
                    boolean exist = checkFaultExist(fault, faultList);
                    if (exist) {
                        existFaultCount++;
                    } else {
                        needAddFaultList.add(fault);
                        faultList.add(fault);
                    }
                    rowCount++;
                }
            }

            if (CollectionUtils.isNotEmpty(needAddFaultList)) {
                needAddFaultList.sort(Comparator.comparing(BuFaultInfo::getReportTime, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(BuFaultInfo::getHappenTime, Comparator.nullsLast(Comparator.naturalOrder())));
                // 生成故障编号
                for (BuFaultInfo fault : needAddFaultList) {
                    String faultSn = serialNumberGenerate.generateSerialNumberByCode("JDXGZ");
                    fault.setFaultSn(faultSn + "LS");
                    // 设置故障来源
                    fault.setFromType(2);
                }

                // 删除旧的
                // 通过故障编码删除，导入时的故障编码要求唯一且不能重复
                int deleteCount = 0;
                List<String> faultSnList = needAddFaultList.stream()
                        .map(BuFaultInfo::getFaultSn)
                        .collect(Collectors.toList());
                List<List<String>> faultSnBatchSubList = DatabaseBatchSubUtil.batchSubList(faultSnList);
                for (List<String> faultSnBatchSub : faultSnBatchSubList) {
                    LambdaQueryWrapper<BuFaultInfo> deleteFaultWrapper = new LambdaQueryWrapper<BuFaultInfo>()
                            .in(BuFaultInfo::getFaultSn, faultSnBatchSub);
                    int delete = buFaultInfoMapper.delete(deleteFaultWrapper);
                    deleteCount = deleteCount + delete;
                }
                // 插入新的
                List<List<BuFaultInfo>> faultBatchSubList = DatabaseBatchSubUtil.batchSubList(needAddFaultList);
                for (List<BuFaultInfo> faultBatchSub : faultBatchSubList) {
                    buFaultInfoMapper.insertList(faultBatchSub);
                }

                return String.format("表单中有效故障信息有%s条（其中有%s条已存在的），生成了%s条故障记录并导入（%s条旧的被删除后重新导入）",
                        rowCount, existFaultCount, needAddFaultList.size(), deleteCount);
            } else {
                return "表单中没有需要导入的故障数据";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new JeecgBootException("文件格式不正确：" + ex.getMessage());
        }
    }


    private int parseFaultLevel(String cellValue) {
        int level = 0;
        if (StringUtils.isNotBlank(cellValue)) {
            switch (cellValue) {
                case "A":
                    level = 1;
                    break;
                case "B":
                    level = 2;
                    break;
                case "C":
                    level = 3;
                    break;
                case "D":
                    level = 4;
                    break;
                default:
                    break;
            }
        }
        return level;
    }

    private Date parseDate(String cellValue, int rowIndex, int columnIndex) {
        if (StringUtils.isNotBlank(cellValue)) {
            try {
                try {
                    Calendar calendar = new GregorianCalendar(1900, Calendar.JANUARY, -1);
                    Date date = calendar.getTime();
                    return DateUtils.addDays(date, Double.valueOf(cellValue).intValue());
                } catch (NumberFormatException ex) {
                    try {
                        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                        return dateFormat1.parse(cellValue);
                    } catch (ParseException e) {
                        try {
                            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd");
                            return dateFormat2.parse(cellValue);
                        } catch (ParseException parseException) {
                            SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyyMMdd");
                            return dateFormat3.parse(cellValue);
                        }
                    }
                }
            } catch (ParseException ex) {
                throw new JeecgBootException(String.format("【第%s行%s列】日期格式不正确，请使用推荐格式（yyyy-MM-dd或yyyy/MM/dd或yyyyMMdd），当前输入：%s",
                        rowIndex + 1, columnIndex + 1, cellValue));
            }
        }
        return new Date();
    }

    private int parsePhase(String cellValue) {
        int phase = 1;
        if (StringUtils.isNotBlank(cellValue)) {
            switch (cellValue) {
                case "架修期":
                    phase = 1;
                    break;
                case "大修期":
                    phase = 4;
                    break;
                case "质保期":
                    phase = 2;
                    break;
                case "出保期":
                    phase = 3;
                    break;
                default:
                    break;
            }

        }
        return phase;
    }

    private boolean checkFaultExist(BuFaultInfo fault, List<BuFaultInfo> faultList) {
        if (null == fault) {
            return true;
        }
        if (CollectionUtils.isEmpty(faultList)) {
            return false;
        }

        // 判断存在的规则：车号、发生日期、描述一样
        String trainNo = fault.getTrainNo();
        Date happenTime = fault.getHappenTime();
        String faultDesc = fault.getFaultDesc();
        for (BuFaultInfo faultInfo : faultList) {
            boolean sameTrainNo = trainNo.equals(faultInfo.getTrainNo());
            boolean sameHappenDay = org.jeecg.common.util.DateUtils.isSameDay(happenTime, faultInfo.getHappenTime());
            boolean sameFaultDesc = faultDesc.trim().equals(faultInfo.getFaultDesc().trim());
            if (sameTrainNo && sameHappenDay && sameFaultDesc) {
                return true;
            }
        }

        return false;
    }

    private void checkFaultDescLength(int rowIndex, int cellIndex, String cellValue) {
        if (cellValue.length() > 332) {
            throw new JeecgBootException(String.format("【第%s行%s列】过长【当前长度%s】，请修改为332以内（包含汉字和标点符号等<=332），当前输入：%s",
                    rowIndex + 1, cellIndex + 1, cellValue.length(), cellValue));
        }
    }

}
