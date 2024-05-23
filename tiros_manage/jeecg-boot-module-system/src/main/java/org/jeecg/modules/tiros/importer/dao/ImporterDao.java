package org.jeecg.modules.tiros.importer.dao;


import cn.hutool.db.handler.BeanHandler;
import cn.hutool.db.handler.BeanListHandler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.tiros.importer.bean.BuMaterialCategory;
import org.jeecg.modules.tiros.importer.bean.BuMaterialType;
import org.jeecg.modules.tiros.importer.bean.BuRepairReguInfo;
import org.jeecg.modules.tiros.importer.bean.plan.BuTpRepairPlan;
import org.jeecg.modules.tiros.importer.bean.plan.BuTpRepairPlanReguInfo;
import org.jeecg.modules.tiros.importer.bean.plan.BuTpRepairPlanTask;
import org.jeecg.modules.tiros.importer.bean.plan.BuTpRepairPlanWorkstation;
import org.jeecg.modules.tiros.importer.entity.*;
import org.jeecg.modules.tiros.importer.utils.UUIDUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yyg
 */
public class ImporterDao {
    private static PreparedStatement statement = null;


    public static BuMaterialType selectByName(Connection con, String name) {
        BuMaterialType materialType = null;

        String sql = "select * from bu_material_type where name = " + "'" + name + "'";
        try {
            Statement statement = con.createStatement();
            statement.executeQuery(sql);
            ResultSet resultSet = statement.getResultSet();
            BeanHandler<BuMaterialType> bh = new BeanHandler<>(BuMaterialType.class);
            materialType = bh.handle(resultSet);
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return materialType;
    }

    public static Map<String, String> selectMaterialTypeNameIdMap(Connection con) {
        Map<String, String> materialTypeNameIdMap = new HashMap<>(16);
        String sql = "select * from bu_material_type";
        try {
            Statement statement = con.createStatement();
            statement.executeQuery(sql);
            ResultSet resultSet = statement.getResultSet();
            BeanListHandler<BuMaterialType> materialTypeBeanListHandler = new BeanListHandler<>(BuMaterialType.class);
            List<BuMaterialType> materialTypeList = materialTypeBeanListHandler.handle(resultSet);
            if (CollectionUtils.isNotEmpty(materialTypeList)) {
                for (BuMaterialType materialType : materialTypeList) {
                    materialTypeNameIdMap.put(materialType.getName(), materialType.getId());
                }
            }

            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return materialTypeNameIdMap;
    }

//    public static int saveMaterialType(Connection con, BuMaterialType materialType) {
//        int row = 0;
//        String sql = "insert  into bu_material_type (id,code,name,kind,unit,category1) values (?,?,?,?,?,?)";
//        try {
//            statement = con.prepareStatement(sql);
//            con.setAutoCommit(false);
//            statement.setString(1, materialType.getId());
//            statement.setString(2, materialType.getCode());
//            statement.setString(3, materialType.getName());
//            statement.setInt(4, materialType.getKind());
//            statement.setString(5, materialType.getUnit());
//            statement.setString(6, "1");
//
//            row = statement.executeUpdate();
//            con.commit();
//            statement.close();
//        } catch (Exception e) {
//            try {
//                con.rollback();
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//            }
//            e.printStackTrace();
//        }
//        return row;
//    }

    public static int saveMaterialTools(Connection con, List<BuRepairReguTools> tools) {
        int row = 0;
        String sql = "insert ignore into bu_repair_regu_tools (id,regu_detail_id,tool_type_id,amount) values (?,?,?,?)";
        try {
            statement = con.prepareStatement(sql);
            con.setAutoCommit(false);
            tools.forEach(t -> {
                try {
                    statement.setString(1, t.getId());
                    statement.setString(2, t.getReguDetailId());
                    statement.setString(3, t.getToolTypeId());
                    statement.setDouble(4, t.getAmount());
                    statement.addBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            });
            statement.executeBatch();
            con.commit();
            statement.close();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
        return row;
    }

    public static void saveMaterial(Connection con, List<BuRepairReguMaterial> material) {

        String sql = "insert ignore  into bu_repair_regu_material (id,regu_detail_id,material_type_id) values (?,?,?)";
        try {
            PreparedStatement statement = con.prepareStatement(sql);
            con.setAutoCommit(false);
            material.forEach(m -> {
                try {
                    statement.setString(1, m.getId());
                    statement.setString(2, m.getReguDetailId());
                    statement.setString(3, m.getMaterialTypeId());
                    statement.addBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            statement.executeBatch();
            con.commit();
            statement.close();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public static void saveReguDetail(Connection con, List<BuRepairReguDetail> reguDetail) {
        String sql = "insert into bu_repair_regu_detail (id,regu_id,no,title,type,safe_notice,parent_id,method,quality_level,requirement) values" +
                " (?,?,?,?,?,?,?,?,?,?)";

        try {
            PreparedStatement statement = con.prepareStatement(sql);
            con.setAutoCommit(false);
            reguDetail.forEach(r -> {
                try {
                    statement.setString(1, r.getId());
                    statement.setString(2, r.getReguId());
                    statement.setString(3, r.getNo());
                    statement.setString(4, r.getTitle());
                    statement.setInt(5, r.getType());
                    statement.setString(6, r.getSafeNotice());
                    statement.setString(7, r.getParentId());
                    statement.setString(8, r.getMethod());
                    statement.setString(9, r.getQualityLevel());
                    statement.setString(10, r.getRequirement());
                    statement.addBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            statement.executeBatch();
            con.commit();
            statement.close();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }


    public static void saveWorkRecord(Connection con, List<BuWorkRecord> workRecords) {

        String sql = "insert into bu_work_record (id,code,title,line_id,repair_pro_id) values (?,?,?,?,?)";
        try {
            PreparedStatement statement = con.prepareStatement(sql);
            con.setAutoCommit(false);
            workRecords.forEach(w -> {
                try {
                    statement.setString(1, w.getId());
                    statement.setString(2, w.getCode());
                    statement.setString(3, w.getTitle());
                    statement.setString(4, w.getLineId());
                    statement.setString(5, w.getRepairProId());
                    statement.addBatch();
                } catch (SQLException e) {

                    e.printStackTrace();
                }
            });
            statement.executeBatch();
            con.commit();
            statement.close();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public static Map<String, String> selectWorkGroupNameIdMap(Connection con, String groupNamesString) {
        Map<String, String> nameIdMap = new HashMap<>(16);
        try {
            String sql = "select id,group_name from bu_mtr_workshop_group where group_name in (" + groupNamesString + ")";
            Statement statement = con.createStatement();
            statement.executeQuery(sql);
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String groupName = resultSet.getString("group_name");
                nameIdMap.put(groupName, id);
            }
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nameIdMap;
    }

    public static String getGroupName(Connection con, String id) {
        String groupName = "";
        try {

            String sql = "select group_name from bu_mtr_workshop_group where id = ?";
            PreparedStatement statement = con.prepareStatement(sql);
            con.setAutoCommit(false);
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                groupName = resultSet.getString("group_name");

            }

            con.commit();
            statement.close();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
        return groupName;
    }


    public static void saveRepairPlanTask(Connection con, List<BuTpRepairPlanTask> planTasks) {

        String sql = "insert into bu_tp_repair_plan_task (id,plan_id,task_no,task_wbs,task_name,work_time,duration,day_index,work_group_id,parent_id,gen_order,start_time,finish_time) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement statement = con.prepareStatement(sql);
            con.setAutoCommit(false);
            planTasks.forEach(r -> {
                try {
                    statement.setString(1, r.getId());
                    statement.setString(2, r.getPlanId());
                    statement.setInt(3, r.getTaskNo());
                    statement.setString(4, r.getTaskWbs());
                    statement.setString(5, r.getTaskName());
                    statement.setDouble(6, r.getWorkTime() == null ? 0.0 : r.getWorkTime());
                    statement.setDouble(7, r.getDuration());
                    statement.setInt(8, r.getDayIndex());
                    statement.setString(9, r.getWorkGroupId());
                    statement.setString(10, r.getParentId());
                    statement.setInt(11, 1);
                    statement.setDate(12, new Date(r.getStartTime().getTime()));
                    statement.setDate(13, new Date(r.getFinishTime().getTime()));


                    statement.addBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            statement.executeBatch();
            con.commit();
            statement.close();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public static void saveRepairPlanReguInfo(Connection con, List<BuTpRepairPlanReguInfo> reguInfos) {

        String sql = "insert into bu_tp_repair_plan_regu_info (id,task_id,regu_id,regu_detail_id) values (?,?,?,?)";
        try {
            PreparedStatement statement = con.prepareStatement(sql);
            con.setAutoCommit(false);
            reguInfos.forEach(r -> {
                try {
                    statement.setString(1, r.getId());
                    statement.setString(2, r.getTaskId());
                    statement.setString(3, r.getReguId());
                    statement.setString(4, r.getReguDetailId());
                    statement.addBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            statement.executeBatch();
            con.commit();
            statement.close();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public static void saveRepairPlanWorkstation(Connection con, List<BuTpRepairPlanWorkstation> workstations) {

        String sql = "insert into bu_tp_repair_plan_workstation (id,task_id,workstation_id) values (?,?,?)";
        try {
            PreparedStatement statement = con.prepareStatement(sql);
            con.setAutoCommit(false);
            workstations.forEach(r -> {
                try {
                    statement.setString(1, r.getId());
                    statement.setString(2, r.getTaskId());
                    statement.setString(3, r.getWorkstationId());
                    statement.addBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            statement.executeBatch();
            con.commit();
            statement.close();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public static List<String> getWorkstationIdListByWorkshopId(Connection con, String workshopId) {
        List<String> workstationIdList = new ArrayList<>();
        try {

            String sql = "select id from bu_mtr_workstation where workshop_id = ?";
            PreparedStatement statement = con.prepareStatement(sql);
            con.setAutoCommit(false);
            statement.setString(1, workshopId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                if (StringUtils.isNotBlank(id)) {
                    workstationIdList.add(id);
                }
            }

            con.commit();
            statement.close();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
        return workstationIdList;
    }

    public static void deleteWorkstationForm(Connection con, String workstationIdsString) {
        try {
            String sql = "delete from bu_base_ref_station_form where workstation_id in (" + workstationIdsString + ")";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.executeUpdate();

            con.commit();
            statement.close();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public static void deleteWorkstation(Connection con, String workstationIdsString) {
        try {
            String sql = "delete from bu_mtr_workstation where id in (" + workstationIdsString + ")";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.executeUpdate();

            con.commit();
            statement.close();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public static void saveWorkstation(Connection con, List<BuBaseWorkstation> workstations) {

        String sql = "insert into bu_mtr_workstation (id,workshop_id,name,location,content,status,station_no,remark) values (?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement statement = con.prepareStatement(sql);
            con.setAutoCommit(false);
            workstations.forEach(w -> {
                try {
                    statement.setString(1, w.getId());
                    statement.setString(2, w.getWorkshopId());
                    statement.setString(3, w.getName());
                    statement.setString(4, w.getLocation());
                    statement.setString(5, w.getContent());
                    statement.setInt(6, w.getStatus());
                    statement.setString(7, w.getStationNo());
                    statement.setString(8, w.getRemark());
                    statement.addBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            statement.executeBatch();
            con.commit();
            statement.close();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public static String selectWorkstationId(Connection con, String no) {
        String id = null;

        String sql = "select id from bu_mtr_workstation where station_no = " + "'" + no + "'";
        try {
            Statement statement = con.createStatement();
            statement.executeQuery(sql);
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                id = resultSet.getString("id");
            }
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public static String selectWorkstationIdByName(Connection con, String name) {
        String id = null;

        String sql = "select id from bu_mtr_workstation where name = " + "'" + name + "'";
        try {
            Statement statement = con.createStatement();
            statement.executeQuery(sql);
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                id = resultSet.getString("id");
            }
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public static String selectPositionIdByName(Connection con, String name) {
        String id = null;
        String sql;
        if (name.contains("主任")) {
            if (name.contains("副")) {
                name = "车间副主任";
            } else {
                name = "车间主任";
            }
            sql = "select id from bu_postion_info where position_name = " + "'" + name + "'";
        } else if (name.contains("工长")) {
            if (name.contains("副")) {
                name = "副工长";
            } else {
                name = "工长";
            }
            sql = "select id from bu_postion_info where position_name = " + "'" + name + "'";
        } else {
            sql = "select id from bu_postion_info where position_name like concat('%', " + "'" + name + "'" + ")";
        }
        try {
            Statement statement = con.createStatement();
            statement.executeQuery(sql);
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                id = resultSet.getString("id");
            }
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    //保存用户岗位关联
    public static void saveUserExtend(Connection con, List<SysUser> sysUsers) {
        int row = 0;
        String sql = "insert  into bu_user_extend (user_id,postion_id) values (?,?)";
        try {
            PreparedStatement statement = con.prepareStatement(sql);
            con.setAutoCommit(false);
            sysUsers.forEach(u -> {
                try {
                    statement.setString(1, u.getId());
                    statement.setString(2, u.getPositionId());
                    statement.addBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            statement.executeBatch();
            con.commit();
            statement.close();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    //保存用户工班关联
    public static void saveUserDepart(Connection con, List<SysUser> sysUsers) {
        int row = 0;
        String sql = "insert  into sys_user_depart (id,user_id,dep_id) values (?,?,?)";
        try {
            PreparedStatement statement = con.prepareStatement(sql);
            con.setAutoCommit(false);
            sysUsers.forEach(u -> {
                try {
                    statement.setString(1, UUIDUtils.getUUID());
                    statement.setString(2, u.getId());
                    statement.setString(3, u.getDeptId());
                    statement.addBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            statement.executeBatch();
            con.commit();
            statement.close();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    //保存用户角色关联
    public static void saveUserRole(Connection con, List<SysUser> sysUsers) {
        int row = 0;
        String sql = "insert  into sys_user_role (id,user_id,role_id) values (?,?,?)";
        try {
            PreparedStatement statement = con.prepareStatement(sql);
            con.setAutoCommit(false);
            sysUsers.forEach(u -> {
                try {
                    statement.setString(1, UUIDUtils.getUUID());
                    statement.setString(2, u.getId());
                    statement.setString(3, u.getRoleId());
                    statement.addBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            statement.executeBatch();
            con.commit();
            statement.close();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    //保存用户
    public static void saveUser(Connection con, List<SysUser> sysUsers) {
        int row = 0;
        String sql = "insert  into sys_user (id,realname,sex,status,del_flag,work_no,username,password,salt) values (?,?,?,1,0,?,?,?,?)";
        try {
            PreparedStatement statement = con.prepareStatement(sql);
            con.setAutoCommit(false);
            sysUsers.forEach(u -> {
                try {
                    statement.setString(1, u.getId());
                    statement.setString(2, u.getRealname());
                    statement.setInt(3, u.getSex());
                    statement.setString(4, u.getWorkNo());
                    statement.setString(5, u.getUsername());
                    statement.setString(6, u.getPassword());
                    statement.setString(7, u.getSalt());
                    statement.addBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            statement.executeBatch();
            con.commit();
            statement.close();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public static Integer selectReguInfoVersion(Connection con, String lineId) {
        Integer version = null;

        String sql = "select max(version) as version from bu_repair_regu_info where line_id = " + "'" + lineId + "'";
        try {
            Statement statement = con.createStatement();
            statement.executeQuery(sql);
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                version = resultSet.getInt("version");
            }
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    public static Map<String, BuRepairReguDetail> selectWbsReguDetailMap(Connection con, String reguId) {
        List<BuRepairReguDetail> reguDetailList = new ArrayList<>();
        Map<String, BuRepairReguDetail> wbsReguDetailMap = new HashMap<>();

        String sql = "select * from bu_repair_regu_detail where regu_id = " + "'" + reguId + "'";
        try {
            Statement statement = con.createStatement();
            statement.executeQuery(sql);
            ResultSet resultSet = statement.getResultSet();
            BeanListHandler<BuRepairReguDetail> bh = new BeanListHandler<>(BuRepairReguDetail.class);
            reguDetailList = bh.handle(resultSet);
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setWbsReguDetailMap(reguDetailList, wbsReguDetailMap);

        return wbsReguDetailMap;
    }

    private static void setWbsReguDetailMap(List<BuRepairReguDetail> reguDetailList,
                                            Map<String, BuRepairReguDetail> wbsReguDetailMap) {
        List<BuRepairReguDetail> topList = extractChildren(reguDetailList);
        for (BuRepairReguDetail top : topList) {
            top.setWbs(top.getNo());
            List<BuRepairReguDetail> children = top.getChildren();
            setReguDetailWbs(children, top.getWbs());
        }

        for (BuRepairReguDetail top : topList) {
            recurseAddMap(wbsReguDetailMap, top);
        }
    }

    private static void recurseAddMap(Map<String, BuRepairReguDetail> wbsReguDetailMap,
                                      BuRepairReguDetail reguDetail) {
        List<BuRepairReguDetail> children = reguDetail.getChildren();
        if (CollectionUtils.isNotEmpty(children)) {
            for (BuRepairReguDetail child : children) {
                wbsReguDetailMap.put(child.getWbs(), child);
                recurseAddMap(wbsReguDetailMap, child);
            }
        }

        reguDetail.setChildren(null);
        wbsReguDetailMap.put(reguDetail.getWbs(), reguDetail);
    }

    private static void setReguDetailWbs(List<BuRepairReguDetail> reguDetailList, String parentWbs) {
        if (CollectionUtils.isEmpty(reguDetailList)) {
            return;
        }

        for (BuRepairReguDetail reguDetail : reguDetailList) {
            String wbs = parentWbs + "." + reguDetail.getNo();
            reguDetail.setWbs(wbs);
            List<BuRepairReguDetail> children = reguDetail.getChildren();
            setReguDetailWbs(children, reguDetail.getWbs());
        }

    }

    private static List<BuRepairReguDetail> extractChildren(List<BuRepairReguDetail> reguDetailList) {
        List<BuRepairReguDetail> topList = reguDetailList.stream()
                .filter(detail -> null == detail.getParentId())
                .collect(Collectors.toList());
        for (BuRepairReguDetail top : topList) {
            recurseGetChildren(top, reguDetailList);
        }
        return topList;
    }

    private static void recurseGetChildren(BuRepairReguDetail parent, List<BuRepairReguDetail> reguDetailList) {
        String parentId = parent.getId();
        List<BuRepairReguDetail> children = reguDetailList.stream()
                .filter(detail -> parentId.equals(detail.getParentId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(children)) {
            for (BuRepairReguDetail child : children) {
                recurseGetChildren(child, reguDetailList);
            }
        }
        parent.setChildren(children);
    }

    public static void saveWorkRecordDetail(Connection con, List<BuWorkRecordDetail> recordDetails) {

        String sql = "insert into bu_work_record_detail (id,category_id,work_content,check_level,tech_require,work_info,self_check,guarder_check,monitor_check,item_no) values (?,?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement statement = con.prepareStatement(sql);
            con.setAutoCommit(false);
            recordDetails.forEach(w -> {
                try {
                    statement.setString(1, w.getId());
                    statement.setString(2, w.getCategoryId());
                    statement.setString(3, w.getWorkContent());
                    statement.setInt(4, w.getCheckLevel());
                    statement.setString(5, w.getTechRequire());
                    statement.setString(6, w.getWorkInfo());
                    statement.setString(7, w.getSelfCheck());
                    statement.setString(8, w.getGuarderCheck());
                    statement.setString(9, w.getMonitorCheck());
                    statement.setString(10, w.getItemNo());
                    statement.addBatch();
                } catch (SQLException e) {
                    e.printStackTrace();

                }
            });
            statement.executeBatch();
            con.commit();
            statement.close();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public static void saveWorkRecordCategory(Connection con, List<BuWorkRecordCategory> recordCategories) {

        String sql = "insert into bu_work_record_category (id,work_rec_id,rec_index,regu_title) values (?,?,?,?)";
        try {
            PreparedStatement statement = con.prepareStatement(sql);
            con.setAutoCommit(false);
            recordCategories.forEach(w -> {
                try {
                    statement.setString(1, w.getId());
                    statement.setString(2, w.getWorkRecId());
                    statement.setInt(3, w.getReguIndex());
                    statement.setString(4, w.getReguTitle());
                    statement.addBatch();
                } catch (SQLException e) {
                    e.printStackTrace();

                }
            });
            statement.executeBatch();
            con.commit();
            statement.close();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    /**
     * 保存规程信息
     *
     * @param con
     * @param reguInfo
     * @return
     */
    public static int saveReguInfo(Connection con, BuRepairReguInfo reguInfo) {
        int row = 0;
        String sql = "insert into bu_repair_regu_info (id,code,name,repair_pro_id,line_id,train_type_id,workshop_id,version) values (?,?,?,?,?,?,?,?)";
        try {
            statement = con.prepareStatement(sql);
            con.setAutoCommit(false);
            statement.setString(1, reguInfo.getId());
            statement.setString(2, reguInfo.getCode());
            statement.setString(3, reguInfo.getName());
            statement.setString(4, reguInfo.getRepairProId());
            statement.setString(5, reguInfo.getLineId());
            statement.setString(6, reguInfo.getTrainTypeId());
            statement.setString(7, reguInfo.getWorkshopId());
            statement.setString(8, reguInfo.getVersion());

            row = statement.executeUpdate();
            con.commit();
            statement.close();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();

        }
        return row;
    }

    public static List<BuRepairReguInfo> listReguInfo(Connection con) {
        List<BuRepairReguInfo> reguInfoList = new ArrayList<>();
        try {
            String sql = "select * from bu_repair_regu_info";
            PreparedStatement statement = con.prepareStatement(sql);
            con.setAutoCommit(false);

            ResultSet resultSet = statement.executeQuery();
            BeanListHandler<BuRepairReguInfo> listHandler = new BeanListHandler<>(BuRepairReguInfo.class);
            reguInfoList = listHandler.handle(resultSet);

            con.commit();
            statement.close();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();

        }
        return reguInfoList;
    }

    /**
     * 保存计划模板
     *
     * @param con
     * @param plan
     * @return
     */
    public static int saveRepairPlan(Connection con, BuTpRepairPlan plan) {
        int row = 0;
        String sql = "insert  into bu_tp_repair_plan (id,code,tp_name,line_id,train_type_id,repair_program_id,duration,base_date,regu_id) values (?,?,?,?,?,?,?,?,?)";
        try {
            statement = con.prepareStatement(sql);
            con.setAutoCommit(false);
            statement.setString(1, plan.getId());
            statement.setString(2, plan.getCode());
            statement.setString(3, plan.getTpName());
            statement.setString(4, plan.getLineId());
            statement.setString(5, plan.getTrainTypeId());
            statement.setString(6, plan.getRepairProgramId());
            statement.setInt(7, plan.getDuration());
            statement.setDate(8, new Date(plan.getBaseDate().getTime()));
            statement.setString(9, plan.getReguId());

            row = statement.executeUpdate();
            con.commit();
            statement.close();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();

        }
        return row;
    }

    public static void saveGroupWorkstation(Connection con, List<BuGroupWorkstation> groupWorkstations) {
        String sql = "insert into bu_group_workstation (id,group_id,workstation_id) values (?,?,?)";
        try {
            PreparedStatement statement = con.prepareStatement(sql);
            con.setAutoCommit(false);
            groupWorkstations.forEach(w -> {
                try {
                    statement.setString(1, w.getId());
                    statement.setString(2, w.getGroupId());
                    statement.setString(3, w.getWorkstationId());
                    statement.addBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            statement.executeBatch();
            con.commit();
            statement.close();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public static List<BuMaterialCategory> selectAllMaterialTypeCategory(Connection con) {
        List<BuMaterialCategory> categoryList = new ArrayList<>();
        try {
            String sql = "select * from bu_material_category";
            PreparedStatement statement = con.prepareStatement(sql);
            con.setAutoCommit(false);

            ResultSet resultSet = statement.executeQuery();
            BeanListHandler<BuMaterialCategory> categoryBeanListHandler = new BeanListHandler<>(BuMaterialCategory.class);
            categoryList = categoryBeanListHandler.handle(resultSet);

            con.commit();
            statement.close();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();

        }
        return categoryList;
    }

    public static int insertMaterialCategoryList(Connection con, List<BuMaterialCategory> categoryList) {
        int row = 0;
        String sql = "insert into bu_material_category (id, code, name, status, parent_id, remark) values" +
                " (?,?,?,?,?,?)";
        try {
            statement = con.prepareStatement(sql);
            con.setAutoCommit(false);

            for (BuMaterialCategory category : categoryList) {
                statement.setString(1, category.getId());
                statement.setString(2, category.getCode());
                statement.setString(3, category.getName());
                statement.setInt(4, category.getStatus());
                statement.setString(5, category.getParentId());
                statement.setString(6, category.getRemark());
                statement.addBatch();
            }
//            Integer maxNameSize = 0;
//            int index = 0;
//            for (int i = 0; i < categoryList.size(); i++) {
//                BuMaterialCategory  category = categoryList.get(i);
//                if (category.getName().length() > maxNameSize) {
//                    maxNameSize = category.getName().length();
//                    index = i;
//                }
//                statement.setString(1, category.getId());
//                statement.setString(2, category.getCode());
//                statement.setString(3, category.getName());
//                statement.setInt(4, category.getStatus());
//                statement.setString(5, category.getParentId());
//                statement.setString(6, category.getRemark());
//                statement.addBatch();
//            }
//            System.out.println("maxNameSize = " + maxNameSize);
//            System.out.println("maxNameSize 出现的数据 " + categoryList.get(index).toString());

            int[] batchResults = statement.executeBatch();
            for (int batchResult : batchResults) {
                if (-2 == batchResult) {
                    row++;
                }
            }

            con.commit();
            statement.close();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();

        }
        return row;
    }


    public static int insertMaterialCategoryList1(Connection con, List<org.jeecg.modules.material.material.bean.BuMaterialCategory> categoryList) {
        int row = 0;
        String sql = "insert into bu_material_category (id, code, name, status, parent_id, remark) values" +
                " (?,?,?,?,?,?)";
        try {
            statement = con.prepareStatement(sql);
            con.setAutoCommit(false);

            for (org.jeecg.modules.material.material.bean.BuMaterialCategory category : categoryList) {
                statement.setString(1, category.getId());
                statement.setString(2, category.getCode());
                statement.setString(3, category.getName());
                statement.setInt(4, category.getStatus());
                statement.setString(5, category.getParentId());
                statement.setString(6, category.getRemark());
                statement.addBatch();
            }
//            Integer maxNameSize = 0;
//            int index = 0;
//            for (int i = 0; i < categoryList.size(); i++) {
//                BuMaterialCategory  category = categoryList.get(i);
//                if (category.getName().length() > maxNameSize) {
//                    maxNameSize = category.getName().length();
//                    index = i;
//                }
//                statement.setString(1, category.getId());
//                statement.setString(2, category.getCode());
//                statement.setString(3, category.getName());
//                statement.setInt(4, category.getStatus());
//                statement.setString(5, category.getParentId());
//                statement.setString(6, category.getRemark());
//                statement.addBatch();
//            }
//            System.out.println("maxNameSize = " + maxNameSize);
//            System.out.println("maxNameSize 出现的数据 " + categoryList.get(index).toString());

            int[] batchResults = statement.executeBatch();
            for (int batchResult : batchResults) {
                if (-2 == batchResult) {
                    row++;
                }
            }

            con.commit();
            statement.close();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();

        }
        return row;
    }

    public static int insertMaterialTypeList(Connection con, List<BuMaterialType> materialTypeList) {
        int row = 0;
        String sql = "insert into bu_material_type (id, code, name, spec, unit, price, status, kind, category, category1," +
                "category2, category3, subject, is_asset, from_head, is_consume, remark) values" +
                " (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            statement = con.prepareStatement(sql);
            con.setAutoCommit(false);

            for (BuMaterialType materialType : materialTypeList) {
                statement.setString(1, materialType.getId());
                statement.setString(2, materialType.getCode());
                statement.setString(3, materialType.getName());
                statement.setString(4, materialType.getSpec());
                statement.setString(5, materialType.getUnit());
                statement.setBigDecimal(6, materialType.getPrice());
                statement.setInt(7, materialType.getStatus());
                statement.setInt(8, materialType.getKind());
                statement.setString(9, materialType.getCategory());
                statement.setInt(10, materialType.getCategory1());
                statement.setString(11, materialType.getCategory2());
                statement.setString(12, materialType.getCategory3());
                statement.setString(13, materialType.getSubject());
                statement.setInt(14, materialType.getIsAsset());
                statement.setInt(15, materialType.getFromHead());
                statement.setInt(16, materialType.getIsConsume());
                statement.setString(17, materialType.getRemark());
                statement.addBatch();
            }

            int[] batchResults = statement.executeBatch();
            for (int batchResult : batchResults) {
                if (-2 == batchResult) {
                    row++;
                }
            }

            con.commit();
            statement.close();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();

        }
        return row;
    }


    private static void close(Statement statement, Connection con) {
        try {
            statement.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
