package org.jeecg.modules.tiros.importer.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlan;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlanTask;
import org.jeecg.modules.basemanage.tpplan.service.BuTpRepairPlanService;
import org.jeecg.modules.tiros.importer.bean.plan.BuTpRepairPlanWorkstation;
import org.jeecg.modules.tiros.importer.dao.ImporterSql;
import org.jeecg.modules.tiros.importer.utils.EmptyUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author yyg
 */
@Repository
public class RepairPlanRelationImport {
    @Resource
    private ImporterSql importerSql;
    @Resource
    private BuTpRepairPlanService buTpRepairPlanService;

    private final String DATE = "date";
    private final String NO_GROUP = "送外维修厂家";
    private int sumCountDay = 0;
    /**
     * 第三层任务
     */
    private List<BuTpRepairPlanTask> planTasks = new ArrayList<>();
    /**
     * 第一层任务
     */
    private List<BuTpRepairPlanTask> parentPlanTasks = new ArrayList<>();
    /**
     * 第二层任务
     */
    private List<BuTpRepairPlanTask> groupPlanTasks = new ArrayList<>();
    private BuTpRepairPlanTask planTask = null;
    //    private List<BuTpRepairPlanReguInfo> planReguInfos = new ArrayList<>();
    private List<BuTpRepairPlanWorkstation> planWorkstations = new ArrayList<>();
    private String planId = "";

    @Transactional(rollbackFor = Exception.class)
    public void ImportPlanTask(String fileName, Map<String, Map<String, List<String>>> content, BuTpRepairPlan plan) throws Exception {
        // 新增计划模板
        plan = createRepairPlan(fileName, plan);
        BuTpRepairPlan finalPlan = plan;
        String workshopId = plan.getWorkshopId();
        String companyId = plan.getCompanyId();
        if (StringUtils.isBlank(workshopId) || StringUtils.isBlank(companyId)) {
            // 获取登录用户信息
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            if (null != sysUser) {
                workshopId = sysUser.getWorkshopId();
                companyId = sysUser.getCompanyId();
            }
        }
        if (StringUtils.isBlank(workshopId)) {
            throw new JeecgBootException("当前用户归属车间不明确，请设置后再导入！");
        } else {
            plan.setWorkshopId(workshopId);
        }
        if (StringUtils.isNotBlank(companyId)) {
            plan.setCompanyId(companyId);
        }

        List<String> groupNameList = new ArrayList<>();
        Map<String, String> groupNameIdMap;
        Map<String, Integer> groupNameOrderMap;

        if (content != null) sumCountDay = content.get(DATE).size();
        content.keySet().forEach(key -> {
            if (!key.equals(DATE) && !key.equals(NO_GROUP)) {
                groupNameList.add(key);
            }
        });
        if (CollectionUtils.isNotEmpty(groupNameList)) {
            groupNameIdMap = importerSql.selectWorkGroupNameIdMap(groupNameList, workshopId);
            if (groupNameIdMap.size() < groupNameList.size()) {
                List<String> notFoundGroupNameList = new ArrayList<>();
                for (String groupName : groupNameList) {
                    if (!groupNameIdMap.containsKey(groupName)) {
                        notFoundGroupNameList.add(groupName);
                    }
                }
                throw new JeecgBootException("数据库不存在所需工班(" + String.join(",", notFoundGroupNameList) + ")，请添加后重新导入计划模板");
            }
            groupNameOrderMap = new HashMap<>();
            List<String> groupNames = groupNameIdMap.keySet().stream()
                    .sorted(Comparator.comparing(String::toString, Comparator.nullsLast(Comparator.naturalOrder())))
                    .collect(Collectors.toList());
            AtomicInteger groupNameSortNo = new AtomicInteger(1);
            groupNames.forEach(item -> groupNameOrderMap.put(item, groupNameSortNo.getAndIncrement()));

            //创建第一层数据
            String repairProgramName = importerSql.selectRepairProgramName(plan.getRepairProgramId());
            content.get(DATE).forEach((key, v) -> {
                BuTpRepairPlanTask repairPlanTask = getBuTpRepairPlanTaskOfDay(key, "第" + key + "天" + repairProgramName, finalPlan);
                parentPlanTasks.add(repairPlanTask);
            });

            groupNameList.forEach(group -> {
                Map<String, List<String>> task = content.get(group);
                createTaskItem(groupNameIdMap, finalPlan, group, task, groupNameOrderMap);
            });
        }
        try {
            saveData();
            resetData();
        } catch (Exception e) {
            e.printStackTrace();
            throw new JeecgBootException("导入失败!");
        }
    }

    private void resetData() {
        planTasks.clear();
        parentPlanTasks.clear();
        groupPlanTasks.clear();
        planTask = null;
        planId = "";
    }

    private Map<String, Integer> setGroupOrder(String workshopId) {
        if ("CJ1".equals(workshopId)) {
            Map<String, Integer> groupOrder = new HashMap<>();
            groupOrder.put("转向架工班", 1);
            groupOrder.put("轮对工班", 2);
            groupOrder.put("车钩工班", 3);
            groupOrder.put("车体车门工班", 4);
            groupOrder.put("总装工班", 5);
            groupOrder.put("电子电气工班", 6);
            groupOrder.put("调试工班", 7);
            groupOrder.put("制动工班", 8);
            groupOrder.put("委外劳务班", 9);
            return groupOrder;
        } else if ("CJ2".equals(workshopId)) {
            Map<String, Integer> groupOrder = new HashMap<>();
            groupOrder.put("调试工班", 1);
            groupOrder.put("车体工班", 2);
            groupOrder.put("车门工班", 3);
            groupOrder.put("总装工班", 4);
            groupOrder.put("电气工班", 5);
            return groupOrder;
        }
        throw new JeecgBootException("未明确车间，不能setGroupOrder");
    }

    private void createTaskItem(Map<String, String> groupNameIdMap, BuTpRepairPlan tpRepairPlan, String group, Map<String, List<String>> task, Map<String, Integer> groupNameOrderMap) {
        task.forEach((day, data) -> {
            for (int i = 0; i < data.size(); i++) {
                int taskNo = i + 1;
                String taskName = data.get(i);
                String taskWbs = "";
                Integer dayIndex = Integer.valueOf(day);
                if (StringUtils.isBlank(taskName)) return;
                BuTpRepairPlanTask parentTask = selectParentTask(dayIndex).get(0);
                taskWbs = parentTask.getTaskWbs() + "." + taskNo;
                BuTpRepairPlanTask planTask = new BuTpRepairPlanTask()
                        .setId(UUIDGenerator.generate())
                        .setDayIndex(dayIndex)
                        .setPlanId(tpRepairPlan.getId())
                        .setTaskNo(taskNo)
                        .setTaskName(taskName)
                        .setWorkTime(8.00)
                        .setDuration(1.00)
                        .setWorkGroupId(groupNameIdMap.get(group))
                        .setParentId(parentTask.getId())
                        .setImportant(0)
                        .setOutsource(0)
                        .setMethod(1)
                        .setGenOrder(1);

                Map<String, Date> time = getStartFinishTime(dayIndex, tpRepairPlan.getBaseDate());
                planTask.setStartTime(time.get("start"));
                planTask.setFinishTime(time.get("end"));


                List<BuTpRepairPlanTask> buTpRepairPlanTasks = groupPlanTasks.stream().filter(d -> d.getDayIndex() == dayIndex && d.getTaskName().equals(group)).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(buTpRepairPlanTasks)) {
                    planTask.setParentId(buTpRepairPlanTasks.get(0).getId());
                    planTask.setTaskWbs(buTpRepairPlanTasks.get(0).getTaskWbs() + "." + taskNo);
                } else {
                    createGroupTask(group, day, groupNameOrderMap.getOrDefault(group, 0), taskNo, taskWbs, parentTask, planTask, groupNameIdMap);
                }
                planTasks.add(planTask);

            }
          /*  data.forEach(item -> {
                String taskNo = "1";
                String taskName = "";
                String taskWbs = "";
                if (item.contains(".")) {
                    if (item.contains("：")) {
                        System.out.println(item);
                        item = item.substring(item.lastIndexOf("：") + 1).trim();
                        System.out.println(">>>>" + item);
                    }
                    if (item.contains(":")) {
                        System.out.println(item);
                        item = item.substring(item.lastIndexOf(":") + 1).trim();
                        System.out.println("<<<<<" + item);
                    }
                    System.out.println("|||||||||" + item);
                    taskNo = item.substring(0, item.lastIndexOf("."));
                    taskName = item.substring(item.lastIndexOf(".") + 1);
                } else {
                    if (StringUtils.isNotBlank(item)) {
                        taskName = item;
                    }else {
                        return;
                    }
                }

            });*/
        });
    }

    private void createGroupTask(String group, String day, int index, int taskNo, String taskWbs, BuTpRepairPlanTask parentTask, BuTpRepairPlanTask planTask, Map<String, String> groupNameIdMap) {
        String id = UUIDGenerator.generate();
        BuTpRepairPlanTask groupTask = new BuTpRepairPlanTask()
                .setId(id)
                .setTaskNo(index)
                .setTaskName(group)
                .setTaskWbs(parentTask.getTaskNo() + "." + index)
                .setParentId(parentTask.getId())
                .setDayIndex(Integer.valueOf(day))
                .setDuration(parentTask.getDuration())
                .setPlanId(parentTask.getPlanId())
                .setFinishTime(parentTask.getFinishTime())
                .setStartTime(parentTask.getStartTime())
                .setWorkGroupId(groupNameIdMap.get(group))
                .setImportant(0)
                .setOutsource(0)
                .setMethod(1)
                .setGenOrder(1);

        planTask.setParentId(id);
        planTask.setTaskWbs(taskWbs + "." + taskNo);
        groupPlanTasks.add(groupTask);
    }


   /* public void ImportRepairPlanRelation(String path, ImportConfig importConfig) {
        try {
            Sheet sheet;
            Workbook workbook = ExcelUtil.getWorkbook(path);
            BuTpRepairPlan tpRepairPlan = createRepairPlan(path, importConfig);
            int numberOfSheets = workbook.getNumberOfSheets();

            // 查询工班信息
            List<String> groupNameList = new ArrayList<>();
            Map<String, String> groupNameIdMap = new HashMap<>();
            for (int k = 1; k < numberOfSheets; k++) {
                sheet = workbook.getSheetAt(k);
                String groupName = sheet.getSheetName();
                groupNameList.add(groupName);
            }
            if (CollectionUtils.isNotEmpty(groupNameList)) {
                String groupNamesString = "'" + String.join("','", groupNameList) + "'";
                groupNameIdMap = ImporterDao.selectWorkGroupNameIdMap(con, groupNamesString);
                if (groupNameIdMap.size() < groupNameList.size()) {
                    List<String> notFoundGroupNameList = new ArrayList<>();
                    for (String groupName : groupNameList) {
                        if (!groupNameIdMap.containsKey(groupName)) {
                            notFoundGroupNameList.add(groupName);
                        }
                    }
                    System.out.println("数据库不存在所需工班(" + String.join(",", notFoundGroupNameList) + ")，请添加后重新导入计划模板");
                    return;
                }
            }

            for (int k = 1; k < numberOfSheets; k++) {
                sheet = workbook.getSheetAt(k);
                String sheetName = sheet.getSheetName();
                Map<String, List<String>> task = content.get(sheetName);
                if (parentPlanTasks.size() < 21) {
                    task.forEach((key, v) -> {
                        BuTpRepairPlanTask repairPlanTask = getBuTpRepairPlanTask(key, "第" + key + "天架修", tpRepairPlan);
                        parentPlanTasks.add(repairPlanTask);
                    });
                }
                int rowCount = sheet.getLastRowNum();
                int colCount = sheet.getRow(0).getLastCellNum();
                AtomicReference<String> taskId = new AtomicReference<>("");
                for (int i = 1; i <= rowCount; i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) {
                        continue;
                    }
                    List<String> taskItems = new ArrayList<>(task.get(String.valueOf(i)));
                    int index = i;
                    for (int j = 0; j < colCount; j++) {
                        Cell cell = row.getCell(j);
                        // 合并类型：0没有合并 1列合并(同行多列合并) 2行合并(多行同列合并) 3行列都合并
                        int mergeType;
                        String cellValue;
                        mergeType = ExcelUtil.isMerge(sheet, i, cell);
                        if (mergeType != 0) {
                            cellValue = ExcelUtil.getMergedRegionValueAndInfo(sheet, row.getRowNum(), cell.getColumnIndex());
                        } else {
                            cellValue = ExcelUtil.getCellValue(cell);
                        }
                        if (StrUtil.isNotEmpty(cellValue.trim())) {
                            if (cellValue.contains("\n")) {
                                cellValue = cellValue.replace("\n", "");
                            }
                            if (j == 0) {
                                index = Double.valueOf(cellValue).intValue();
                            }
                            if (j == 1) {
                                createTask(sheetName, taskId, taskItems, index, cellValue, tpRepairPlan, importConfig.getReguId(), groupNameIdMap);
                            }
                            if (j == 6) {
                                createPlanWorkstation(index, cellValue, sheetName, groupNameIdMap);
                            }
                        }
                    }
                }
            }
            saveData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private BuTpRepairPlan createRepairPlan(String fileName, BuTpRepairPlan plan) {
        if (StringUtils.isBlank(plan.getTrainTypeId())) {
            throw new JeecgBootException("选择的线路没有绑定车型!");
        }

        planId = plan.getId();
        if (StringUtils.isBlank(planId)) {
            planId = UUIDGenerator.generate();
            plan.setId(planId);
        }

        buTpRepairPlanService.saveOrUpdate(plan);
        return buTpRepairPlanService.getById(planId);
    }

    private BuTpRepairPlanTask getBuTpRepairPlanTaskOfDay(String key, String taskName, BuTpRepairPlan tpRepairPlan) {
        Integer dateIndex = Integer.valueOf(key);
        BuTpRepairPlanTask repairPlanTask = new BuTpRepairPlanTask()
                .setId(UUIDGenerator.generate())
                .setDayIndex(dateIndex)
                .setPlanId(tpRepairPlan.getId())
                .setTaskNo(dateIndex)
                .setTaskWbs(key)
                .setTaskName(taskName)
                .setDuration(1.00)
                .setImportant(0)
                .setOutsource(0)
                .setMethod(1)
                .setGenOrder(1);
        Map<String, Date> time = getStartFinishTime(dateIndex, tpRepairPlan.getBaseDate());
        repairPlanTask.setStartTime(time.get("start"));
        repairPlanTask.setFinishTime(time.get("end"));
        return repairPlanTask;
    }

    private Map<String, Date> getStartFinishTime(int dayIndex, Date baseDate) {
        Map<String, Date> map = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(baseDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DATE, dayIndex);

        Date start = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.SECOND, -1);
        Date end = calendar.getTime();
        map.put("start", start);
        map.put("end", end);

      /*  Calendar calendar = Calendar.getInstance();
        calendar.setTime(baseDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, dayIndex);*/
        return map;
    }

   /* private void createPlanWorkstation(int index, String cellValue,
                                       String sheetName, Map<String, String> groupNameIdMap) {
        String[] workStationList = cellValue.trim().split(";");
        for (String workstation : workStationList) {
            String[] workStations = workstation.split("#");
            String no = workStations[0];
            String workStationNo = workStations[1];

            String[] nos = no.split(",");
            for (String n : nos) {
                List<BuTpRepairPlanTask> planTaskList = getExistPlanTaskList(sheetName, groupNameIdMap, n, index);
                if (CollectionUtils.isEmpty(planTaskList)) {
                    continue;
                }
                for (BuTpRepairPlanTask planTask : planTaskList) {
                    String[] workStationNos = workStationNo.split(",");
                    for (String stationNo : workStationNos) {
                        BuTpRepairPlanWorkstation planWorkstation = new BuTpRepairPlanWorkstation();
                        planWorkstation.setId(UUIDUtils.getUUID());
                        planWorkstation.setTaskId(planTask.getId());
                        String workstationId = ImporterDao.selectWorkstationId(con, stationNo);
                        if (StrUtil.isNotEmpty(workstationId)) {
                            planWorkstation.setWorkstationId(workstationId);
                            planWorkstations.add(planWorkstation);
                        }
                    }
                }
            }
        }
    }*/

    /*private void createTask(String sheetName, AtomicReference<String> taskId,
                            List<String> taskItems, int index,
                            String cellValue, BuTpRepairPlan tpRepairPlan,
                            String reguId, Map<String, String> groupNameIdMap) {
//        Map<String, BuRepairReguDetail> wbsReguDetailMap = ImporterDao.selectWbsReguDetailMap(con, reguId);

        String[] reguList = cellValue.trim().split(";");
        for (String s : reguList) {
            String[] reguItems = s.split("#");
            String no = reguItems[0];
//            String reguNo = reguItems[1];
//            List<String> reguDetailWbsList = getReguDetailWbsList(reguNo);
            for (String t : taskItems) {
                String taskNo = t.substring(0, t.lastIndexOf("."));
                if (no.equals(taskNo)) {
                    String taskWbs = index + "." + no;
                    if (!EmptyUtils.listNotEmpty(getExistPlanTaskList(sheetName, groupNameIdMap, no, index))) {
                        taskId.set(UUIDUtils.getUUID());
                        planTask = new BuTpRepairPlanTask();
                        planTask.setId(taskId.toString());
                        planTask.setDayIndex(index);
                        planTask.setPlanId(tpRepairPlan.getId());
                        planTask.setTaskNo(Integer.valueOf(taskNo));
                        planTask.setTaskWbs(taskWbs);
                        planTask.setTaskName(t.substring(t.lastIndexOf(".") + 1));
                        planTask.setWorkTime(8.00);
                        planTask.setDuration(1.00);
                        planTask.setWorkGroupId(groupNameIdMap.get(sheetName));
                        planTask.setParentId(selectParentTask(index).get(0).getId());
                        Date time = getStartFinishTime(index, tpRepairPlan.getBaseDate());
                        planTask.setStartTime(time);
                        planTask.setFinishTime(time);
                        planTasks.add(planTask);
                    }

//                    for (String reguDetailWbs : reguDetailWbsList) {
//                        BuRepairReguDetail reguDetail = wbsReguDetailMap.get(reguDetailWbs);
//                        if (null != reguDetail) {
//                            BuTpRepairPlanReguInfo planReguInfo = new BuTpRepairPlanReguInfo();
//                            planReguInfo.setId(UUIDUtils.getUUID());
//                            planReguInfo.setTaskId(taskId.toString());
//                            planReguInfo.setReguId(reguDetail.getReguId());
//                            planReguInfo.setReguDetailId(reguDetail.getId());
//                            planReguInfos.add(planReguInfo);
//                        }
//                    }
                }
            }
        }
    }*/


//    private List<String> getReguDetailWbsList(String reguNo) {
//        List<String> reguDetailWbsList = new ArrayList<>();
//
//        if (!reguNo.contains("-")) {
//            reguDetailWbsList.add(reguNo);
//        } else {
//            String[] split = reguNo.split("\\.");
//            String firstNos = split[0];
//            String secondNos = split[1];
//            String threeNos = split[2];
//
//            List<String> firstNoList = getNoList(firstNos);
//            List<String> secondNoList = getNoList(secondNos);
//            List<String> threeNoList = getNoList(threeNos);
//
//            for (String firstNo : firstNoList) {
//                for (String secondNo : secondNoList) {
//                    for (String threeNo : threeNoList) {
//                        reguDetailWbsList.add(firstNo + "." + secondNo + "." + threeNo);
//                    }
//                }
//            }
//        }
//
//        return reguDetailWbsList;
//    }

//    private List<String> getNoList(String firstNos) {
//        List<String> firstNoList = new ArrayList<>();
//        String[] allNoArray = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
//                "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
//                "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"};
//        if (firstNos.equals("*")) {
//            firstNoList = Arrays.asList(allNoArray);
//        } else if (firstNos.contains("-")) {
//            String[] firstNosSplit = firstNos.split("-");
//            int start = Integer.parseInt(firstNosSplit[0]);
//            int end = Integer.parseInt(firstNosSplit[1]);
//            for (int i = start; i <= end; i++) {
//                firstNoList.add(i + "");
//            }
//        } else {
//            firstNoList.add(firstNos);
//        }
//
//        return firstNoList;
//    }

    private void saveData() throws Exception {
        if (StringUtils.isNotBlank(planId)) {
            buTpRepairPlanService.deleteTaskByIds(planId);
        }
        if (EmptyUtils.listNotEmpty(parentPlanTasks)) {
            importerSql.saveRepairPlanTask(parentPlanTasks);
            if (EmptyUtils.listNotEmpty(planTasks)) {
              /* if (EmptyUtils.listNotEmpty(planReguInfos)) {
                   ImporterDao.saveRepairPlanReguInfo(con, planReguInfos);
               }*/
                importerSql.saveRepairPlanTask(groupPlanTasks);
                importerSql.saveRepairPlanTask(planTasks);

               /* if (EmptyUtils.listNotEmpty(planWorkstations)) {
                    ImporterDao.saveRepairPlanWorkstation(con, planWorkstations);
                }*/
            }
        }
    }

    private List<BuTpRepairPlanTask> getExistPlanTaskList(String sheetName, Map<String, String> groupNameIdMap,
                                                          String no, int index) {
        if (EmptyUtils.listNotEmpty(planTasks)) {
            String workGroupId = groupNameIdMap.get(sheetName);
            return planTasks.stream()
                    .filter(planTask -> (planTask.getTaskNo().equals(Integer.valueOf(no)) && planTask.getWorkGroupId().equals(workGroupId) && index == planTask.getDayIndex()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    private List<BuTpRepairPlanTask> selectParentTask(int dayIndex) {
        return parentPlanTasks.stream()
                .filter(p -> p.getDayIndex() == dayIndex)
                .collect(Collectors.toList());
    }


}