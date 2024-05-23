package org.jeecg.modules.system.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.netty.util.internal.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.FillRuleConstant;
import org.jeecg.common.util.FillRuleUtil;
import org.jeecg.common.util.YouBianCodeUtil;
import org.jeecg.modules.basemanage.line.entity.BuMtrLine;
import org.jeecg.modules.basemanage.line.mapper.BuMtrLineMapper;
import org.jeecg.modules.basemanage.workshop.entity.BuMtrCompany;
import org.jeecg.modules.basemanage.workshop.entity.BuMtrDepot;
import org.jeecg.modules.basemanage.workshop.entity.BuMtrWorkshop;
import org.jeecg.modules.basemanage.workshop.mapper.BuMtrCompanyMapper;
import org.jeecg.modules.basemanage.workshop.mapper.BuMtrDepotMapper;
import org.jeecg.modules.basemanage.workshop.mapper.BuMtrWorkshopMapper;
import org.jeecg.modules.group.information.bean.BuMtrWorkshopGroup;
import org.jeecg.modules.group.information.mapper.BuMtrWorkshopGroupMapper;
import org.jeecg.modules.system.entity.*;
import org.jeecg.modules.system.mapper.*;
import org.jeecg.modules.system.model.DepartIdModel;
import org.jeecg.modules.system.model.SysDepartTreeModel;
import org.jeecg.modules.system.service.ISysDepartService;
import org.jeecg.modules.system.util.FindsDepartsChildrenUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 部门表 服务实现类
 * <p>
 *
 * @Author Steve
 * @Since 2019-01-22
 */
@Service
public class SysDepartServiceImpl extends ServiceImpl<SysDepartMapper, SysDepart> implements ISysDepartService {

    @Resource
    private SysUserDepartMapper userDepartMapper;
    @Resource
    private SysDepartRoleMapper sysDepartRoleMapper;
    @Resource
    private SysDepartPermissionMapper departPermissionMapper;
    @Resource
    private SysDepartRolePermissionMapper departRolePermissionMapper;
    @Resource
    private SysDepartRoleUserMapper departRoleUserMapper;
    @Resource
    private BuMtrWorkshopGroupMapper buMtrWorkshopGroupMapper;
    @Resource
    private BuMtrLineMapper buMtrLineMapper;
    @Resource
    private BuMtrWorkshopMapper buMtrWorkshopMapper;
    @Resource
    private BuMtrCompanyMapper buMtrCompanyMapper;
    @Resource
    private BuMtrDepotMapper buMtrDepotMapper;


    @Override
    public List<SysDepartTreeModel> queryMyDeptTreeList(String departIds) {
        //根据部门id获取所负责部门
        LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<SysDepart>();
        String[] codeArr = this.getMyDeptParentOrgCode(departIds);
        for (int i = 0; i < codeArr.length; i++) {
            query.or().likeRight(SysDepart::getOrgCode, codeArr[i]);
        }
        query.eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
        query.orderByAsc(SysDepart::getDepartOrder);
        //将父节点ParentId设为null
        List<SysDepart> listDepts = this.list(query);
        for (int i = 0; i < codeArr.length; i++) {
            for (SysDepart dept : listDepts) {
                if (dept.getOrgCode().equals(codeArr[i])) {
                    dept.setParentId(null);
                }
            }
        }
        // 调用wrapTreeDataToTreeList方法生成树状数据
        List<SysDepartTreeModel> listResult = FindsDepartsChildrenUtil.wrapTreeDataToTreeList(listDepts);
        return listResult;
    }

    /**
     * queryTreeList 对应 queryTreeList 查询所有的部门数据,以树结构形式响应给前端
     */
//    @Cacheable(value = CacheConstant.SYS_DEPARTS_CACHE)
    @Override
    public List<SysDepartTreeModel> queryTreeList() {
        LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<SysDepart>();
        query.eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
        query.orderByAsc(SysDepart::getDepartOrder);
        List<SysDepart> list = this.list(query);
        // 调用wrapTreeDataToTreeList方法生成树状数据
        List<SysDepartTreeModel> listResult = FindsDepartsChildrenUtil.wrapTreeDataToTreeList(list);
        return listResult;
    }

    //    @Cacheable(value = CacheConstant.SYS_DEPART_IDS_CACHE)
    @Override
    public List<DepartIdModel> queryDepartIdTreeList() {
        LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<SysDepart>();
        query.eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
        query.orderByAsc(SysDepart::getDepartOrder);
        List<SysDepart> list = this.list(query);
        // 调用wrapTreeDataToTreeList方法生成树状数据
        List<DepartIdModel> listResult = FindsDepartsChildrenUtil.wrapTreeDataToDepartIdTreeList(list);
        return listResult;
    }

    /**
     * saveDepartData 对应 add 保存用户在页面添加的新的部门对象数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDepartData(SysDepart sysDepart, String username) {
        if (sysDepart != null && username != null) {
            if (sysDepart.getParentId() == null) {
                sysDepart.setParentId("");
            }
            String s = UUID.randomUUID().toString().replace("-", "");
            sysDepart.setId(s);
            // 先判断该对象有无父级ID,有则意味着不是最高级,否则意味着是最高级
            // 获取父级ID
            String parentId = sysDepart.getParentId();
            //update-begin--Author:baihailong  Date:20191209 for：部门编码规则生成器做成公用配置
            JSONObject formData = new JSONObject();
            formData.put("parentId", parentId);
            String[] codeArray = (String[]) FillRuleUtil.executeRule(FillRuleConstant.DEPART, formData);
            //update-end--Author:baihailong  Date:20191209 for：部门编码规则生成器做成公用配置
            sysDepart.setOrgCode(codeArray[0]);
            String orgType = codeArray[1];
            sysDepart.setOrgType(String.valueOf(orgType));
            sysDepart.setCreateTime(new Date());
            sysDepart.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
            this.save(sysDepart);

            // 如果是班组(部门类型为4),同时添加一条记录在车间班组关联表中
            if ("4".equals(sysDepart.getOrgCategory())) {
                BuMtrWorkshopGroup workshopGroup = new BuMtrWorkshopGroup()
                        .setId(sysDepart.getId())
                        // 班组的上一级是车间，车间id=上级id
                        .setWorkshopId(parentId)
                        .setGroupName(sysDepart.getDepartName())
                        .setGroupDesc(sysDepart.getDescription())
                        .setDepartId(sysDepart.getId());
                buMtrWorkshopGroupMapper.insert(workshopGroup);
            }
        }

    }

    /**
     * saveDepartData 的调用方法,生成部门编码和部门类型（作废逻辑）
     *
     * @param parentId
     * @return
     * @deprecated
     */
    private String[] generateOrgCode(String parentId) {
        //update-begin--Author:Steve  Date:20190201 for：组织机构添加数据代码调整
        LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<SysDepart>();
        LambdaQueryWrapper<SysDepart> query1 = new LambdaQueryWrapper<SysDepart>();
        String[] strArray = new String[2];
        // 创建一个List集合,存储查询返回的所有SysDepart对象
        List<SysDepart> departList = new ArrayList<>();
        // 定义新编码字符串
        String newOrgCode = "";
        // 定义旧编码字符串
        String oldOrgCode = "";
        // 定义部门类型
        String orgType = "";
        // 如果是最高级,则查询出同级的org_code, 调用工具类生成编码并返回
        if (StringUtil.isNullOrEmpty(parentId)) {
            // 线判断数据库中的表是否为空,空则直接返回初始编码
            query1.eq(SysDepart::getParentId, "").or().isNull(SysDepart::getParentId);
            query1.orderByDesc(SysDepart::getOrgCode);
            departList = this.list(query1);
            if (departList == null || departList.size() == 0) {
                strArray[0] = YouBianCodeUtil.getNextYouBianCode(null);
                strArray[1] = "1";
                return strArray;
            } else {
                SysDepart depart = departList.get(0);
                oldOrgCode = depart.getOrgCode();
                orgType = depart.getOrgType();
                newOrgCode = YouBianCodeUtil.getNextYouBianCode(oldOrgCode);
            }
        } else { // 反之则查询出所有同级的部门,获取结果后有两种情况,有同级和没有同级
            // 封装查询同级的条件
            query.eq(SysDepart::getParentId, parentId);
            // 降序排序
            query.orderByDesc(SysDepart::getOrgCode);
            // 查询出同级部门的集合
            List<SysDepart> parentList = this.list(query);
            // 查询出父级部门
            SysDepart depart = this.getById(parentId);
            // 获取父级部门的Code
            String parentCode = depart.getOrgCode();
            // 根据父级部门类型算出当前部门的类型
            orgType = String.valueOf(Integer.valueOf(depart.getOrgType()) + 1);
            // 处理同级部门为null的情况
            if (parentList == null || parentList.size() == 0) {
                // 直接生成当前的部门编码并返回
                newOrgCode = YouBianCodeUtil.getSubYouBianCode(parentCode, null);
            } else { //处理有同级部门的情况
                // 获取同级部门的编码,利用工具类
                String subCode = parentList.get(0).getOrgCode();
                // 返回生成的当前部门编码
                newOrgCode = YouBianCodeUtil.getSubYouBianCode(parentCode, subCode);
            }
        }
        // 返回最终封装了部门编码和部门类型的数组
        strArray[0] = newOrgCode;
        strArray[1] = orgType;
        return strArray;
        //update-end--Author:Steve  Date:20190201 for：组织机构添加数据代码调整
    }


    /**
     * removeDepartDataById 对应 delete方法 根据ID删除相关部门数据
     *
     */
    /*
     * @Override
     *
     * @Transactional public boolean removeDepartDataById(String id) {
     * System.out.println("要删除的ID 为=============================>>>>>"+id); boolean
     * flag = this.removeById(id); return flag; }
     */

    /**
     * updateDepartDataById 对应 edit 根据部门主键来更新对应的部门数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateDepartDataById(SysDepart sysDepart, String username) {
        if (sysDepart != null && username != null) {
            sysDepart.setUpdateTime(new Date());
            sysDepart.setUpdateBy(username);
            this.updateById(sysDepart);

            // 如果是班组(部门类型为4),同时修改车间班组关联表中的记录
            if ("4".equals(sysDepart.getOrgCategory())) {
                BuMtrWorkshopGroup workshopGroup = new BuMtrWorkshopGroup()
                        .setId(sysDepart.getId())
                        .setGroupName(sysDepart.getDepartName())
                        .setGroupDesc(sysDepart.getDescription());
                buMtrWorkshopGroupMapper.updateById(workshopGroup);
            }

            return true;
        } else {
            return false;
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatchWithChildren(List<String> ids) {
        // 如果是班组(部门类型为4),同时删除车间班组关联表中的记录
        for (String id : ids) {
            SysDepart sysDepart = this.getById(id);
            if ("4".equals(sysDepart.getOrgCategory())) {
                buMtrWorkshopGroupMapper.deleteById(id);
            }
        }

        List<String> idList = new ArrayList<String>();
        for (String id : ids) {
            idList.add(id);
            this.checkChildrenExists(id, idList);
        }
        this.removeByIds(idList);
        //根据部门id获取部门角色id
        List<String> roleIdList = new ArrayList<>();
        LambdaQueryWrapper<SysDepartRole> query = new LambdaQueryWrapper<>();
        query.select(SysDepartRole::getId).in(SysDepartRole::getDepartId, idList);
        List<SysDepartRole> depRoleList = sysDepartRoleMapper.selectList(query);
        for (SysDepartRole deptRole : depRoleList) {
            roleIdList.add(deptRole.getId());
        }
        //根据部门id删除用户与部门关系
        userDepartMapper.delete(new LambdaQueryWrapper<SysUserDepart>().in(SysUserDepart::getDepId, idList));
        //根据部门id删除部门授权
        departPermissionMapper.delete(new LambdaQueryWrapper<SysDepartPermission>().in(SysDepartPermission::getDepartId, idList));
        //根据部门id删除部门角色
        sysDepartRoleMapper.delete(new LambdaQueryWrapper<SysDepartRole>().in(SysDepartRole::getDepartId, idList));
        if (roleIdList != null && roleIdList.size() > 0) {
            //根据角色id删除部门角色授权
            departRolePermissionMapper.delete(new LambdaQueryWrapper<SysDepartRolePermission>().in(SysDepartRolePermission::getRoleId, roleIdList));
            //根据角色id删除部门角色用户信息
            departRoleUserMapper.delete(new LambdaQueryWrapper<SysDepartRoleUser>().in(SysDepartRoleUser::getDroleId, roleIdList));
        }
    }

    @Override
    public List<String> getSubDepIdsByDepId(String departId) {
        return this.baseMapper.getSubDepIdsByDepId(departId);
    }

    @Override
    public List<String> getMySubDepIdsByDepId(String departIds) {
        //根据部门id获取所负责部门
        String[] codeArr = this.getMyDeptParentOrgCode(departIds);
        return this.baseMapper.getSubDepIdsByOrgCodes(codeArr);
    }

//    /**
//     * @see ISysDepartService#getWorkshopGroupByGroupId(String)
//     */
//    @Transactional(readOnly = true, rollbackFor = Exception.class)
//    @Override
//    public BuMtrWorkshopGroup getWorkshopGroupByGroupId(String groupId) {
//        BuMtrWorkshopGroup workshopGroup = buMtrWorkshopGroupMapper.selectWorkshopGroupById(groupId);
//        if (null != workshopGroup) {
//            List<String> lineIdList = buMtrWorkshopGroupMapper.selectLineIdListByDepotId(workshopGroup.getDepotId());
//            if (CollectionUtils.isNotEmpty(lineIdList)) {
//                workshopGroup.setLineId(StringUtils.join(lineIdList, ","));
//            }
//        }
//
//        return workshopGroup;
//    }
//
//    /**
//     * @see ISysDepartService#getWorkshopByWorkshopId(String)
//     */
//    @Transactional(readOnly = true, rollbackFor = Exception.class)
//    @Override
//    public BuMtrWorkshop getWorkshopByWorkshopId(String workshopId) {
//        BuMtrWorkshop workshop = buMtrWorkshopMapper.selectWorkshopWithoutGraphAddressById(workshopId);
//        if (null != workshop) {
//            List<String> lineIdList = buMtrWorkshopGroupMapper.selectLineIdListByDepotId(workshop.getDepotId());
//            if (CollectionUtils.isNotEmpty(lineIdList)) {
//                workshop.setLineId(StringUtils.join(lineIdList, ","));
//            }
//        }
//
//        return workshop;
//    }

    /**
     * <p>
     * 根据关键字搜索相关的部门数据
     * </p>
     */
    @Override
    public List<SysDepartTreeModel> searhBy(String keyWord, String myDeptSearch, String departIds) {
        LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<SysDepart>();
        List<SysDepartTreeModel> newList = new ArrayList<>();
        //myDeptSearch不为空时为我的部门搜索，只搜索所负责部门
        if (!StringUtil.isNullOrEmpty(myDeptSearch)) {
            //departIds 为空普通用户或没有管理部门
            if (StringUtil.isNullOrEmpty(departIds)) {
                return newList;
            }
            //根据部门id获取所负责部门
            String[] codeArr = this.getMyDeptParentOrgCode(departIds);
            for (int i = 0; i < codeArr.length; i++) {
                query.or().likeRight(SysDepart::getOrgCode, codeArr[i]);
            }
            query.eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
        }
        query.like(SysDepart::getDepartName, keyWord);
        //update-begin--Author:huangzhilin  Date:20140417 for：[bugfree号]组织机构搜索回显优化--------------------
        SysDepartTreeModel model = new SysDepartTreeModel();
        List<SysDepart> departList = this.list(query);
        if (departList.size() > 0) {
            for (SysDepart depart : departList) {
                model = new SysDepartTreeModel(depart);
                model.setChildren(null);
                //update-end--Author:huangzhilin  Date:20140417 for：[bugfree号]组织机构搜索功回显优化----------------------
                newList.add(model);
            }
            return newList;
        }
        return null;
    }

    /**
     * 根据部门id删除并且删除其可能存在的子级任何部门
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(String id) {
        SysDepart sysDepart = this.getById(id);
        List<String> idList = new ArrayList<>();
        idList.add(id);
        this.checkChildrenExists(id, idList);
        //清空部门树内存
        //FindsDepartsChildrenUtil.clearDepartIdModel();
        boolean ok = this.removeByIds(idList);
        //根据部门id获取部门角色id
        List<String> roleIdList = new ArrayList<>();
        LambdaQueryWrapper<SysDepartRole> query = new LambdaQueryWrapper<>();
        query.select(SysDepartRole::getId).in(SysDepartRole::getDepartId, idList);
        List<SysDepartRole> depRoleList = sysDepartRoleMapper.selectList(query);
        for (SysDepartRole deptRole : depRoleList) {
            roleIdList.add(deptRole.getId());
        }
        //根据部门id删除用户与部门关系
        userDepartMapper.delete(new LambdaQueryWrapper<SysUserDepart>().in(SysUserDepart::getDepId, idList));
        //根据部门id删除部门授权
        departPermissionMapper.delete(new LambdaQueryWrapper<SysDepartPermission>().in(SysDepartPermission::getDepartId, idList));
        //根据部门id删除部门角色
        sysDepartRoleMapper.delete(new LambdaQueryWrapper<SysDepartRole>().in(SysDepartRole::getDepartId, idList));
        if (roleIdList != null && roleIdList.size() > 0) {
            //根据角色id删除部门角色授权
            departRolePermissionMapper.delete(new LambdaQueryWrapper<SysDepartRolePermission>().in(SysDepartRolePermission::getRoleId, roleIdList));
            //根据角色id删除部门角色用户信息
            departRoleUserMapper.delete(new LambdaQueryWrapper<SysDepartRoleUser>().in(SysDepartRoleUser::getDroleId, roleIdList));
        }

        // 如果是班组(部门类型为4),同时删除车间班组关联表中的记录
        if ("4".equals(sysDepart.getOrgCategory())) {
            buMtrWorkshopGroupMapper.deleteById(id);
        }

        return ok;
    }

    /**
     * delete 方法调用
     *
     * @param id
     * @param idList
     */
    private void checkChildrenExists(String id, List<String> idList) {
        LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<SysDepart>();
        query.eq(SysDepart::getParentId, id);
        List<SysDepart> departList = this.list(query);
        if (departList != null && departList.size() > 0) {
            for (SysDepart depart : departList) {
                idList.add(depart.getId());
                this.checkChildrenExists(depart.getId(), idList);
            }
        }
    }

    @Override
    public List<SysDepart> queryUserDeparts(String userId) {
        List<SysDepart> departList = baseMapper.queryUserDeparts(userId);

        if (CollectionUtils.isNotEmpty(departList)) {
            List<SysDepart> allDepartList = baseMapper.selectList(Wrappers.emptyWrapper());
            Map<String, SysDepart> idDepartMap = new HashMap<>();
            allDepartList.forEach(item -> idDepartMap.put(item.getId(), item));

            // 查询所有公司、车间、车辆段、线路
            List<BuMtrCompany> companyList = buMtrCompanyMapper.selectAllList();
            Map<String, BuMtrCompany> idCompanyMap = new HashMap<>();
            companyList.forEach(item -> idCompanyMap.put(item.getId(), item));
            List<BuMtrWorkshop> workshopList = buMtrWorkshopMapper.selectAllListWithoutGraphAddress();
            Map<String, BuMtrWorkshop> idWorkshopMap = new HashMap<>();
            workshopList.forEach(item -> idWorkshopMap.put(item.getId(), item));
            List<BuMtrLine> lineList = buMtrLineMapper.selectAllList();
//            Map<String, BuMtrLine> idLineMap = new HashMap<>();
//            lineList.forEach(item -> idLineMap.put(item.getLineId(), item));
            List<BuMtrDepot> depotList = buMtrDepotMapper.selectAllList();
            Map<String, BuMtrDepot> idDepotMap = new HashMap<>();
            depotList.forEach(item -> idDepotMap.put(item.getId(), item));
            List<BuMtrWorkshopGroup> groupList = buMtrWorkshopGroupMapper.selectList(Wrappers.emptyWrapper());
            Map<String, BuMtrWorkshopGroup> idGroupMap = new HashMap<>();
            groupList.forEach(item -> idGroupMap.put(item.getId(), item));

            for (SysDepart depart : departList) {
                // 前提：部门id=班组id、车间id、公司id
                String departId = depart.getId();
                if (idGroupMap.containsKey(departId)) {
                    // 班组
                    BuMtrWorkshopGroup group = idGroupMap.get(departId);
                    String workshopId = group.getWorkshopId();
                    BuMtrWorkshop workshop = idWorkshopMap.get(workshopId);
                    String depotId = workshop.getDepotId();
                    BuMtrDepot depot = idDepotMap.get(depotId);
                    String companyId = depot.getCompanyId();
                    String lineIds = lineList.stream()
                            .filter(item -> workshopId.equals(item.getWorkshopId()))
                            .map(BuMtrLine::getLineId)
                            .distinct()
                            .collect(Collectors.joining(","));
//                    List<String> lineIdList = depot.getLineIdList();
//                    String lineId = null;
//                    if (CollectionUtils.isNotEmpty(lineIdList)) {
//                        lineId = String.join(",", lineIdList);
//                    }

                    depart.setCompanyId(companyId)
                            .setWorkshopId(workshopId)
                            .setDepotId(depotId)
                            .setLineId(lineIds)
                            .setGroupId(departId)
                            .setDepartIsGroup(true);
                }
                if (idWorkshopMap.containsKey(departId)) {
                    // 车间
                    BuMtrWorkshop workshop = idWorkshopMap.get(departId);
                    String depotId = workshop.getDepotId();
                    BuMtrDepot depot = idDepotMap.get(depotId);
                    String companyId = depot.getCompanyId();
                    String lineIds = lineList.stream()
                            .filter(item -> departId.equals(item.getWorkshopId()))
                            .map(BuMtrLine::getLineId)
                            .distinct()
                            .collect(Collectors.joining(","));
//                    List<String> lineIdList = depot.getLineIdList();
//                    String lineId = null;
//                    if (CollectionUtils.isNotEmpty(lineIdList)) {
//                        lineId = String.join(",", lineIdList);
//                    }

                    depart.setCompanyId(companyId)
                            .setWorkshopId(departId)
                            .setDepotId(depotId)
                            .setLineId(lineIds)
                            .setGroupId(null)
                            .setDepartIsGroup(false);
                }
                if (idCompanyMap.containsKey(departId)) {
                    // 公司
                    depart.setCompanyId(departId)
                            .setWorkshopId(null)
                            .setDepotId(null)
                            .setLineId(null)
                            .setGroupId(null)
                            .setDepartIsGroup(false);
                }
                if ("CLZX".equals(departId) || departId.contains("CLZX")) {
                    // 车辆中心
                    String parentId = idDepartMap.get(departId).getParentId();
                    if (idCompanyMap.containsKey(parentId)) {
                        depart.setCompanyId(parentId)
                                .setWorkshopId(null)
                                .setDepotId(null)
                                .setLineId(null)
                                .setGroupId(null)
                                .setDepartIsGroup(false);
                    }
                }
            }
        }

        return departList;
    }

    @Override
    public List<SysDepart> queryDepartsByUsername(String username) {
        return baseMapper.queryDepartsByUsername(username);
    }

    /**
     * 根据用户所负责部门ids获取父级部门编码
     *
     * @param departIds
     * @return
     */
    private String[] getMyDeptParentOrgCode(String departIds) {
        //根据部门id查询所负责部门
        LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<SysDepart>();
        query.eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
        query.in(SysDepart::getId, Arrays.asList(departIds.split(",")));
        query.orderByAsc(SysDepart::getOrgCode);
        List<SysDepart> list = this.list(query);
        //查找根部门
        if (list == null || list.size() == 0) {
            return null;
        }
        String orgCode = this.getMyDeptParentNode(list);
        String[] codeArr = orgCode.split(",");
        return codeArr;
    }

    /**
     * 获取负责部门父节点
     *
     * @param list
     * @return
     */
    private String getMyDeptParentNode(List<SysDepart> list) {
        Map<String, String> map = new HashMap<>();
        //1.先将同一公司归类
        for (SysDepart dept : list) {
            String code = dept.getOrgCode().substring(0, 3);
            if (map.containsKey(code)) {
                String mapCode = map.get(code) + "," + dept.getOrgCode();
                map.put(code, mapCode);
            } else {
                map.put(code, dept.getOrgCode());
            }
        }
        StringBuffer parentOrgCode = new StringBuffer();
        //2.获取同一公司的根节点
        for (String str : map.values()) {
            String[] arrStr = str.split(",");
            parentOrgCode.append(",").append(this.getMinLengthNode(arrStr));
        }
        return parentOrgCode.substring(1);
    }

    /**
     * 获取同一公司中部门编码长度最小的部门
     *
     * @param str
     * @return
     */
    private String getMinLengthNode(String[] str) {
        int min = str[0].length();
        String orgCode = str[0];
        for (int i = 1; i < str.length; i++) {
            if (str[i].length() <= min) {
                min = str[i].length();
                orgCode = orgCode + "," + str[i];
            }
        }
        return orgCode;
    }
}
