package org.jeecg.modules.dispatch.planform.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.BusinessException;
import org.jeecg.common.exception.ExceptionCode;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.tiros.constant.TirosConstant;
import org.jeecg.common.tiros.rpt.service.AlertCheckService;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.tiros.util.MybatisWrapperUtil;
import org.jeecg.common.tiros.util.TirosUtil;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.PasswordUtil;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.dispatch.planform.bean.*;
import org.jeecg.modules.dispatch.planform.bean.vo.*;
import org.jeecg.modules.dispatch.planform.mapper.*;
import org.jeecg.modules.dispatch.planform.service.BuRepairPlanFormsService;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairPlan;
import org.jeecg.modules.dispatch.serialplan.bean.BuWorkMeasureItem;
import org.jeecg.modules.dispatch.serialplan.bean.bo.BuWorkRecordCategoryBO;
import org.jeecg.modules.dispatch.serialplan.mapper.BuRepairPlanMapper;
import org.jeecg.modules.dispatch.workorder.bean.BuPlWorkRecordExcelData;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrder;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderTaskFormInst;
import org.jeecg.modules.dispatch.workorder.bean.bo.SysUserBO;
import org.jeecg.modules.dispatch.workorder.bean.vo.*;
import org.jeecg.modules.dispatch.workorder.mapper.BuPlWorkRecordExcelDataMapper;
import org.jeecg.modules.dispatch.workorder.mapper.BuWorkOrderMapper;
import org.jeecg.modules.dispatch.workorder.mapper.BuWorkOrderTaskFormInstMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * <p>
 * 列计划表单，当列计划审批通过后，根据该配置表自动生成相关数据记录 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-17
 */
@Slf4j
@Service
public class BuRepairPlanFormsServiceImpl extends ServiceImpl<BuRepairPlanFormsMapper, BuRepairPlanForms> implements BuRepairPlanFormsService {

    @Resource
    private BuRepairPlanMapper buRepairPlanMapper;
    @Resource
    private BuRepairPlanFormsMapper buRepairPlanFormsMapper;
    @Resource
    private BuRepairPlanTaskFormsMapper buRepairPlanTaskFormsMapper;
    @Resource
    private BuPlanFormDataRecordMapper buPlanFormDataRecordMapper;
    @Resource
    private BuPlanFormValuesMapper buPlanFormValuesMapper;
    @Resource
    private BuPlanFormWorkRecordMapper buPlanFormWorkRecordMapper;
    @Resource
    private BuPlanFormWorkRecordDetailMapper buPlanFormWorkRecordDetailMapper;
    @Resource
    private BuPlanFormWorkRecordChecksMapper buPlanFormWorkRecordChecksMapper;
    @Resource
    private BuPlanFormCheckRecordMapper buPlanFormCheckRecordMapper;
    @Resource
    private BuPlanFormCheckRecordItemMapper buPlanFormCheckRecordItemMapper;
    @Resource
    private BuPlanFormCheckRecordJudgeMapper buPlanFormCheckRecordJudgeMapper;
    @Resource
    private BuPlanFormCheckRecordRectifyMapper buPlanFormCheckRecordRectifyMapper;
    @Resource
    private BuWorkCheckDispatchMapper buWorkCheckDispatchMapper;
    @Resource
    private BuWorkCheckItemDispatchMapper buWorkCheckItemDispatchMapper;
    @Resource
    private BuWorkCheckTechLinkDispatchMapper buWorkCheckTechLinkDispatchMapper;
    @Resource
    private BuWorkOrderMapper buWorkOrderMapper;
    @Resource
    private BuWorkMeasureItemDispatchMapper buWorkMeasureItemDispatchMapper;
    @Resource
    private AlertCheckService alertCheckService;
    @Resource
    private BuWorkOrderTaskFormInstMapper buWorkOrderTaskFormInstMapper;
    @Resource
    private BuPlWorkRecordExcelDataMapper buPlWorkRecordExcelDataMapper;
    @Resource
    public RedisTemplate<String, String> redisTemplate;
    @Resource
    public ISysBaseAPI sysBaseAPI;


    /**
     * @see BuRepairPlanFormsService#pageForms(BuRepairPlanFormsQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuRepairPlanForms> pageForms(BuRepairPlanFormsQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buRepairPlanFormsMapper.selectPageWithChoiceByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuRepairPlanFormsService#listForms(BuRepairPlanFormsQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuRepairPlanForms> listForms(BuRepairPlanFormsQueryVO queryVO) throws Exception {
        return buRepairPlanFormsMapper.selectListWithChoiceByCondition(queryVO);
    }

    /**
     * @see BuRepairPlanFormsService#getFormById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuRepairPlanForms getFormById(String id) throws Exception {
        return buRepairPlanFormsMapper.selectFormById(id);
    }

    /**
     * @see BuRepairPlanFormsService#updateFormById(BuRepairPlanForms)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateFormById(BuRepairPlanForms buRepairPlanForms) throws Exception {

        deleteOldFormsRelationData(Collections.singletonList(buRepairPlanForms.getId()));
        buRepairPlanFormsMapper.updateById(buRepairPlanForms);

        return true;
    }

    /**
     * @see BuRepairPlanFormsService#editTitleOrAsset(BuRepairPlanFormEditVO)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean editTitleOrAsset(BuRepairPlanFormEditVO planFormEditVO) {
        Integer formType = planFormEditVO.getFormType();
        String id = planFormEditVO.getId();
        String structDetailId = planFormEditVO.getTrainStructId();
        String title = planFormEditVO.getTitle();
        // 数据记录表
        if (formType == 1) {
            buPlanFormDataRecordMapper.updateStructAndTitleById(id, structDetailId, title);
            // 作业记录表
        } else if (formType == 3) {
            buPlanFormWorkRecordMapper.updateStructAndTitleById(id, structDetailId, title);
            // 作业检查表
        } else if (formType == 4) {
            buPlanFormCheckRecordMapper.updateStructAndTitleById(id, structDetailId, title);
        }

        return true;
    }

    /**
     * @see BuRepairPlanFormsService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));

        deleteOldFormsRelationData(idList);
        buRepairPlanFormsMapper.deleteBatchIds(idList);

        // 删除列计划表单时，删除列计划任务的表单关联
        LambdaQueryWrapper<BuRepairPlanTaskForms> taskFormsWrapper = new LambdaQueryWrapper<BuRepairPlanTaskForms>()
                .in(BuRepairPlanTaskForms::getPlanFormId, idList);
        buRepairPlanTaskFormsMapper.delete(taskFormsWrapper);

        return true;
    }

    /**
     * @see BuRepairPlanFormsService#generateFormsRelationDataByPlanId(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean generateFormsRelationDataByPlanId(String planId) throws Exception {
        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = null == sysUser ? "admin" : sysUser.getId();

        Date now = new Date();

        // 查询列计划信息
        BuRepairPlan plan = buRepairPlanMapper.selectById(planId);
        if (null == plan) {
            throw new JeecgBootException("列计划不存在");
        }
        String trainNo = plan.getTrainNo();

        // 查询列计划表单
        LambdaQueryWrapper<BuRepairPlanForms> wrapper = new LambdaQueryWrapper<BuRepairPlanForms>()
                .eq(BuRepairPlanForms::getPlanId, planId)
                .eq(BuRepairPlanForms::getFromBy, 1);
        List<BuRepairPlanForms> planFormsList = buRepairPlanFormsMapper.selectList(wrapper);

        if (CollectionUtils.isNotEmpty(planFormsList)) {
            // 删除旧的表单实列
            List<String> planFormsIdList = planFormsList.stream()
                    .map(BuRepairPlanForms::getId)
                    .collect(Collectors.toList());
            deleteOldFormsRelationData(planFormsIdList);

            // 现在使用资产结构，所以要在生产表单实例时，把表单实例的对应的设备类型id，车辆结构id，车辆设备id 置空
//            // 根据结构明细id获取车辆设备
//            List<String> structDetailIdList = planFormsList.stream()
//                    .map(BuRepairPlanForms::getTrainStructId)
//                    .filter(StringUtils::isNotBlank)
//                    .distinct()
//                    .collect(Collectors.toList());
//            List<BuTrainAsset> allAssetList = buTrainAssetDispatchMapper.selectAssetListByTrainNoAndStructDetailIdList(trainNo, structDetailIdList);
//            Map<String, List<BuTrainAsset>> structDetailIdAssetListMap = allAssetList.stream()
//                    .collect(Collectors.groupingBy(BuTrainAsset::getStructDetailId));

            // 生成表单实列
            for (BuRepairPlanForms planForm : planFormsList) {
                // 现在使用资产结构，所以要在生产表单实例时，把表单实例的对应的设备类型id，车辆结构id，车辆设备id 置空
//                List<BuTrainAsset> assetList = structDetailIdAssetListMap.getOrDefault(planForm.getTrainStructId(), new ArrayList<>());
                savePlanFormInstAndRelation(planForm, trainNo, now, userId, null);
            }
        }

        return true;
    }

    /**
     * @see BuRepairPlanFormsService#listOrderWorkRecord(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuWorkOrderTaskFormInst> listOrderWorkRecord(String orderId) throws Exception {
        return buPlanFormWorkRecordMapper.selectListByOrderId(orderId);
    }

    /**
     * @see BuRepairPlanFormsService#getFormDataRecordById(BuPlanOnlineFormQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuPlanFormDataRecord getFormDataRecordById(BuPlanOnlineFormQueryVO queryVO) throws Exception {
        BuPlanFormDataRecord planFormDataRecord;

        // 是否需要数据值记录
        Boolean needValues = queryVO.getNeedValues();
        if (needValues != null && needValues) {
            planFormDataRecord = buPlanFormDataRecordMapper.selectDataRecordWithValuesById(queryVO.getFormInstId());
        } else {
            planFormDataRecord = buPlanFormDataRecordMapper.selectDataRecordById(queryVO.getFormInstId());
            if (planFormDataRecord != null) {
                planFormDataRecord.setValuesList(new ArrayList<>());
            }
        }

        return planFormDataRecord;
    }

    /**
     * @see BuRepairPlanFormsService#saveFormDataRecordResult(BuPlanFormDataRecord)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveFormDataRecordResult(BuPlanFormDataRecord buPlanFormDataRecord) throws Exception {
        Date now = new Date();

        buPlanFormDataRecord.setStatus(1);
        buPlanFormDataRecordMapper.updateById(buPlanFormDataRecord);
        // excel表单和自定义控件表单都是json，
        // 后端程序开启另一个线程从json抽取数据，保存到测量记录表，如果再次保存，则清除原所有记录（包括内存表中预警记录），重新生成
        deleteFormValues(buPlanFormDataRecord);
        saveFormValues(buPlanFormDataRecord, now);

        // 更新生成预警信息-5测量异常预警
        alertCheckService.generateAlertInfo(5);

        return true;
    }

    /**
     * @see BuRepairPlanFormsService#listAlertRecordByDataRecordFormId(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuPlanFormValues> listAlertRecordByDataRecordFormId(String dataRecordId) throws Exception {
        LambdaQueryWrapper<BuPlanFormValues> valuesWrapper = new LambdaQueryWrapper<BuPlanFormValues>()
                .eq(BuPlanFormValues::getFormDataRecordId, dataRecordId);
        List<BuPlanFormValues> valuesList = buPlanFormValuesMapper.selectList(valuesWrapper);

        // 过滤发生过预警的记录
        return valuesList.stream()
                .filter(values -> 1 == values.getAlertHappen())
                .collect(Collectors.toList());
    }

    /**
     * @see BuRepairPlanFormsService#getFormWorkRecordById(BuPlanWorkRecordFormQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuPlanFormWorkRecord getFormWorkRecordById(BuPlanWorkRecordFormQueryVO queryVO) throws Exception {
        BuPlanFormWorkRecord planFormWorkRecord;

        // 是否需要查询检查信息
        Boolean needChecks = queryVO.getNeedChecks();
        if (needChecks != null && needChecks) {
            if (StringUtils.isNotBlank(queryVO.getTask2InstId())) {
                planFormWorkRecord = buPlanFormWorkRecordMapper.selectWorkRecordWithDetailCheckByTask2InstId(queryVO.getTask2InstId(), queryVO.getAllItems());
            } else {
                List<BuPlanFormWorkRecord> recordList = buPlanFormWorkRecordMapper.selectWorkRecordWithDetailCheckByInstId(queryVO.getFormInstId(), queryVO.getAllItems());
                planFormWorkRecord = recordList.get(0);
            }
        } else {
            if (StringUtils.isNotBlank(queryVO.getTask2InstId())) {
                planFormWorkRecord = buPlanFormWorkRecordMapper.selectWorkRecordWithDetailByTask2InstId(queryVO.getTask2InstId(), queryVO.getAllItems());
            } else {
                List<BuPlanFormWorkRecord> recordList = buPlanFormWorkRecordMapper.selectWorkRecordWithDetailByInstId(queryVO.getFormInstId(), queryVO.getAllItems());
                planFormWorkRecord = recordList.get(0);
            }
        }
        setDetailCheck(planFormWorkRecord);

        // 是否需要明细分类 true:作业明细分到categoryList下，false:作业明细在detailList
        setDetailCategory(planFormWorkRecord, queryVO.getNeedCategory());

        return planFormWorkRecord;
    }

    /**
     * @see BuRepairPlanFormsService#checkFormWorkRecordDetail(BuWorkOrderRecordCheckVO)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<Map<String, String>> checkFormWorkRecordDetail(BuWorkOrderRecordCheckVO checkVO) {
        SysUserBO sysUserBO = buPlanFormWorkRecordChecksMapper.selectUserBOByUserName(checkVO.getCheckUserName());

        // 验证用户名密码
        if (!"~**&&##@@$$%%^^~".equals(checkVO.getCheckUserPwd())) {
            if (null == sysUserBO) {
                throw new BusinessException(ExceptionCode.USERNAME_OR_PASSWORD_ERROR);
            }
            String userpassword = PasswordUtil.encrypt(checkVO.getCheckUserName(), checkVO.getCheckUserPwd(), sysUserBO.getSalt());
            if (!userpassword.equals(sysUserBO.getPassword())) {
                throw new BusinessException(ExceptionCode.USERNAME_OR_PASSWORD_ERROR);
            }
        }
        // 新的作业记录表
        Integer colIndex = -1;
        if (checkVO.getWorkRecordType() == 2) {
            //先生成明细
            String details = getWorkRecordDetailIds(checkVO.getWorkRecordId(), checkVO.getDetailIds());
            checkVO.setDetailIds(details);
            colIndex = checkVO.getColIndex();
        }
        submitWorkRecordChecks(checkVO, sysUserBO.getId(), colIndex);

        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("id", sysUserBO.getId());
        userInfo.put("username", sysUserBO.getUsername());
        userInfo.put("realname", sysUserBO.getRealname());
        if (StringUtils.isNotBlank(checkVO.getTogetherCheckUserNames())) {
            String togetherCheckUserNames = checkVO.getTogetherCheckUserNames();
            if (togetherCheckUserNames.contains(sysUserBO.getRealname())) {
                List<String> togetherCheckUserNameList = new ArrayList<>(Arrays.asList(togetherCheckUserNames.split(",")));
                togetherCheckUserNameList.removeAll(Collections.singletonList(sysUserBO.getRealname()));
                userInfo.put("togetherCheckUserNames", String.join(",", togetherCheckUserNameList));
            } else {
                userInfo.put("togetherCheckUserNames", togetherCheckUserNames);
            }
        } else {
            userInfo.put("togetherCheckUserNames", null);
        }
        return new Result<Map<String, String>>().successResult(userInfo);
    }

    private String getWorkRecordDetailIds(String workRecordId, String detailIds) {
        List<String> detailIdList = Arrays.asList(detailIds.split(","));
        List<BuPlanFormWorkRecordDetail> formWorkRecordDetailList = new ArrayList<>();
        List<String> localDetailIdList = new ArrayList<>();
        detailIdList.forEach(rowCol -> {
            List<BuPlanFormWorkRecordDetail> workRecordDetails = buPlanFormWorkRecordDetailMapper.selectList(Wrappers.<BuPlanFormWorkRecordDetail>lambdaQuery()
                    .select(BuPlanFormWorkRecordDetail::getId)
                    .eq(BuPlanFormWorkRecordDetail::getWorkRecordId, workRecordId)
                    .eq(BuPlanFormWorkRecordDetail::getDetailLineNum, Integer.valueOf(rowCol)));
            if (CollectionUtils.isNotEmpty(workRecordDetails)) {
                localDetailIdList.addAll(workRecordDetails.stream().map(BuPlanFormWorkRecordDetail::getId).collect(Collectors.toList()));
            } else {
                BuPlanFormWorkRecordDetail planFormWorkRecordDetail = new BuPlanFormWorkRecordDetail()
                        .setId(UUIDGenerator.generate())
                        .setWorkRecordId(workRecordId)
                        .setWorkRecDetailId("-1")
                        .setResult(0)
                        .setIsIgnore(0)
                        .setCreateTime(new Date())
                        .setUpdateTime(new Date())
                        .setDetailLineNum(Integer.valueOf(rowCol));
                formWorkRecordDetailList.add(planFormWorkRecordDetail);
                localDetailIdList.add(planFormWorkRecordDetail.getId());
            }
        });

        if (CollectionUtils.isNotEmpty(formWorkRecordDetailList)) {
            buPlanFormWorkRecordDetailMapper.insertList(formWorkRecordDetailList);
        }

        return String.join(",", localDetailIdList);
    }

    /**
     * @see BuRepairPlanFormsService#saveFormWorkRecordManufNo(String, String, String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveFormWorkRecordManufNo(String id, String manufNo, String manufNoUp) throws Exception {
        BuPlanFormWorkRecord workRecord = buPlanFormWorkRecordMapper.selectById(id);
        if (null == workRecord) {
            throw new JeecgBootException("作业记录表实例不存在");
        }

        workRecord.setManufNo(manufNo)
                .setManufNoUp(manufNoUp);
        UpdateWrapper<BuPlanFormWorkRecord> updateWrapper = MybatisWrapperUtil.getSetFieldNullByIdUpdateWrapper(workRecord);
        buPlanFormWorkRecordMapper.update(workRecord, updateWrapper);

        return true;
    }

    /**
     * @see BuRepairPlanFormsService#scanCheckFormWorkRecordDetail(CheckCommonVO, String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean scanCheckFormWorkRecordDetail(CheckCommonVO checkVO, String checkUserId) {
        submitWorkRecordChecks(checkVO, checkUserId, -1);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean scanCheckFormWorkRecordDetail(BuWorkOrderRecordScanTaskCheckVO checkVO) throws Exception {
        String checkUserId = "";
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //新的作业记录表
        Integer colIndex = -1;
        if (checkVO.getWorkRecordType() != null && checkVO.getWorkRecordType() == 2) {
            //先生成明细
            String details = getWorkRecordDetailIds(checkVO.getFormInstId(), checkVO.getFormDetails());
            checkVO.setFormDetails(details);
            colIndex = checkVO.getColIndex();
        }
        //没有扫任务码
        if (StringUtils.isBlank(checkVO.getUuid())) {
            //自检
            if (checkVO.getCheckType() == 1) {
                checkUserId = sysUser.getId();
                submitWorkRecordChecks(getCheckCommonVO(checkVO), sysUser.getId(), colIndex);
            } else {
                //没扫人员二维码
                if (StringUtils.isBlank(checkVO.getCheckUserUuid())) {
                    SysUserBO sysUserBO = buPlanFormWorkRecordChecksMapper.selectUserBOByUserName(checkVO.getCheckUserName());

                    // 验证用户名密码
                    if (!"~**&&##@@$$%%^^~".equals(checkVO.getCheckUserPwd())) {
                        if (null == sysUserBO) {
                            throw new BusinessException(ExceptionCode.USERNAME_OR_PASSWORD_ERROR);
                        }
                        String userpassword = PasswordUtil.encrypt(checkVO.getCheckUserName(), checkVO.getCheckUserPwd(), sysUserBO.getSalt());
                        if (!userpassword.equals(sysUserBO.getPassword())) {
                            throw new BusinessException(ExceptionCode.USERNAME_OR_PASSWORD_ERROR);
                        }
                    }
                    checkUserId = sysUserBO.getId();
                    submitWorkRecordChecks(getCheckCommonVO(checkVO), sysUserBO.getId(), colIndex);
                } else {
                    //扫了人员二维码
                    String checkUserUuid = redisTemplate.opsForValue().get(CommonConstant.PREFIX_MY_QRCODE + checkVO.getCheckUserUuid());
                    submitWorkRecordChecks(getCheckCommonVO(checkVO), checkUserUuid, colIndex);
                    checkUserId = checkUserUuid;
                }
            }
        } else {
            //扫任务二维码
            String key = redisTemplate.opsForValue().get(CommonConstant.PREFIX_TASK_QRCODE + checkVO.getUuid());
            if (key != null) { //如果redis已过期不处理
                String voJsonStr = redisTemplate.opsForValue().get(CommonConstant.PREFIX_TASK_QRCODE + key);
                if (voJsonStr != null) { //redis中有数据直接读取
                    BuWorkOrderRecordCreateTaskQRCodeVO vo = JSON.parseObject(voJsonStr, BuWorkOrderRecordCreateTaskQRCodeVO.class);
                    String checkUserUuid = checkVO.getCheckUserUuid();
                    checkVO.setWorkRecordType(vo.getWorkRecordType())
                            .setFormDetails(vo.getFormDetails())
                            .setFormInstId(vo.getFormInstId())
                            .setCheckType(vo.getCheckType())
                            .setColIndex(vo.getColIndex());
                    if (checkVO.getWorkRecordType() != null && checkVO.getWorkRecordType() == 2) {
                        //先生成明细
                        String details = getWorkRecordDetailIds(checkVO.getFormInstId(), checkVO.getFormDetails());
                        checkVO.setFormDetails(details);
                        colIndex = checkVO.getColIndex();
                    }

                    String toUser = "";
                    CheckCommonVO checkCommonVO = getCheckCommonVO(checkVO);
                    //没扫人员二维码
                    if (StringUtils.isBlank(checkUserUuid)) {
                        checkUserUuid = sysUser.getId();
                    } else {
                        // 扫了人员二维码 读取redis数据
                        checkUserUuid = redisTemplate.opsForValue().get(CommonConstant.PREFIX_MY_QRCODE + checkVO.getCheckUserUuid());
                    }
                    toUser = sysBaseAPI.getUserById(vo.getFormUserId()).getUsername();
                    submitWorkRecordChecks(checkCommonVO, checkUserUuid, colIndex);
                    checkUserId = checkUserUuid;
                    //删除缓存
                    redisTemplate.delete(CommonConstant.PREFIX_TASK_QRCODE + checkVO.getUuid());
                    redisTemplate.delete(CommonConstant.PREFIX_TASK_QRCODE + key);

                    //发送消息
                    ExecutorService singleExecutor = ThreadUtil.newSingleExecutor();
                    String finalToUser = toUser;
                    singleExecutor.execute(() -> {
                        String title = "扫码检查通知";
                        String checkTypeName = sysBaseAPI.getDictItemTextByCodeAndValue("bu_work_order_record_check_type", null == vo.getCheckType() ? null : vo.getCheckType().toString());
                        String content = checkTypeName + "成功!";
                        sysBaseAPI.sendScanCodeMsg(finalToUser, title, content);
                    });
                }
            } else {
                throw new JeecgBootException("二维码失效!");
            }
        }
        if ("APP".equals(checkVO.getFromBy()) && checkVO.getWorkRecordType() != null && checkVO.getWorkRecordType() == 2) {
            saveExcelData(checkVO.setCheckUserUuid(checkUserId));
        }
        return true;
    }


    private void saveExcelData(BuWorkOrderRecordScanTaskCheckVO checkVO) {
        List<String> rowIndexList = Arrays.asList(checkVO.getFormDetails().split(","));
        rowIndexList.forEach(row -> {
            String resultData = "";
            BuPlWorkRecordExcelData excelData = buPlWorkRecordExcelDataMapper.selectById(checkVO.getFormInstId());
            BuPlanFormWorkRecord workRecord = buPlanFormWorkRecordMapper.selectById(checkVO.getFormInstId());
            if (workRecord != null) {
                String dataJson = buPlanFormWorkRecordMapper.selectFormTemplateDataJsonByFormTemplateId(workRecord.getFormObjId());
                Map<String, Integer> resultColMap = parseDataJsonGetResultCol(dataJson);
                if (excelData != null) {
                    String userName = sysBaseAPI.getUserById(checkVO.getCheckUserUuid()).getRealname();
                    try {
                        resultData = TirosUtil.writeExcelDataByRowAndCol(excelData.getResult(), Integer.parseInt(row), resultColMap.get("jgcol"), checkVO.getResult() == 1 ? "√" : "×");
                        resultData = TirosUtil.writeExcelDataByRowAndCol(resultData, Integer.parseInt(row), resultColMap.get("zyqlCol"), checkVO.getResultDesc());
                        resultData = TirosUtil.writeExcelDataByRowAndCol(resultData, Integer.parseInt(row), checkVO.getColIndex(), userName);
                        buPlWorkRecordExcelDataMapper.updateById(new BuPlWorkRecordExcelData().setId(checkVO.getFormInstId()).setResult(resultData));
                    } catch (Exception e) {
                        log.error("APP保存excelData数据异常：" + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private Map<String, Integer> parseDataJsonGetResultCol(String dataJson) {
        Map<String, Integer> map = new HashMap<>();
        JSONObject jsonObj = JSON.parseObject(dataJson);
        JSONArray jsonArray = jsonObj.getJSONObject("checkDetail").getJSONArray("rows");
        JSONObject object = JSON.parseObject(jsonArray.get(0) + "");
        Integer zyqkCol = object.getJSONObject("cells").getJSONObject("zyqk").getInteger("colIndex");
        Integer jgCol = object.getJSONObject("cells").getJSONObject("jg").getInteger("colIndex");
        map.put("zyqlCol", zyqkCol);
        map.put("jgcol", jgCol);
        return map;

    }

    private CheckCommonVO getCheckCommonVO(BuWorkOrderRecordScanTaskCheckVO checkVO) {
        return new CheckCommonVO()
                .setResultDesc(checkVO.getResultDesc())
                .setDetailIds(checkVO.getFormDetails())
                .setResult(checkVO.getResult())
                .setType(checkVO.getCheckType())
                .setIsIgnore(checkVO.getIsIgnore())
                .setIgnoreDesc(checkVO.getIgnoreDesc());
    }


    /**
     * @see BuRepairPlanFormsService#getFormCheckRecordById(BuPlanCheckRecordFormQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuPlanFormCheckRecord getFormCheckRecordById(BuPlanCheckRecordFormQueryVO queryVO) throws Exception {
        BuPlanFormCheckRecord checkRecord;

        // 是否需要查询整改关联信息，默认是
        Boolean needRectify = queryVO.getNeedRectify();
        if (null == needRectify || needRectify) {
            if (StringUtils.isNotBlank(queryVO.getTask2InstId())) {
                checkRecord = buPlanFormCheckRecordMapper.selectCheckRecordWithItemRectifyByTask2InstId(queryVO.getTask2InstId(), queryVO.getAllItems());
            } else {
                List<BuPlanFormCheckRecord> recordList = buPlanFormCheckRecordMapper.selectCheckRecordWithItemRectifyByInstId(queryVO.getFormInstId(), queryVO.getAllItems());
                checkRecord = recordList.get(0);
            }
        } else {
            if (StringUtils.isNotBlank(queryVO.getTask2InstId())) {
                checkRecord = buPlanFormCheckRecordMapper.selectCheckRecordWithItemByTask2InstId(queryVO.getTask2InstId(), queryVO.getAllItems());
            } else {
                List<BuPlanFormCheckRecord> recordList = buPlanFormCheckRecordMapper.selectCheckRecordWithItemByInstId(queryVO.getFormInstId(), queryVO.getAllItems());
                checkRecord = recordList.get(0);
            }
        }

        if (null == checkRecord) {
            throw new JeecgBootException("检查表单实例不存在");
        }

        // 是否需要质量评定信息，默认是
        Boolean needJudge = queryVO.getNeedJudge();
        if (null == needJudge || needJudge) {
            List<BuPlanFormCheckRecordJudge> judgeList = buPlanFormCheckRecordJudgeMapper.selectListByCheckInstId(queryVO.getFormInstId());
            checkRecord.setJudgeList(judgeList);

            StringBuilder judgeDescBuilder = new StringBuilder();
            if (CollectionUtils.isNotEmpty(judgeList)) {
                for (BuPlanFormCheckRecordJudge judge : judgeList) {
                    String jdTimeStr = null == judge.getJdTime() ? "" : DateUtils.datetimeFormat.get().format(judge.getJdTime());

                    judgeDescBuilder.append("评定人：").append(judge.getJdUserName()).append(" ")
                            .append("评定时间：").append(jdTimeStr).append(" ")
                            .append("评定内容：").append(judge.getJdContent()).append(";")
                            .append(System.lineSeparator());
                }
                String judgeDesc = null;
                if (judgeDescBuilder.length() > 0) {
                    judgeDesc = judgeDescBuilder.deleteCharAt(judgeDescBuilder.length() - 1).toString();
                }
                checkRecord.setJudge(judgeDesc);
            }
        }

        // 关联工艺文件
        List<BuWorkCheckTechLink> techLinkList = buWorkCheckTechLinkDispatchMapper.selectListByWorkCheckId(checkRecord.getFormObjId());
        checkRecord.setTechLinkList(techLinkList);

        // 提取整改
        extractRectify(checkRecord);

        return checkRecord;
    }

    /**
     * @see BuRepairPlanFormsService#listCheckRecordItemByCheckRecordId(BuPlanCheckRecordFormQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuPlanFormCheckRecordItem> listCheckRecordItemByCheckRecordId(BuPlanCheckRecordFormQueryVO queryVO) throws Exception {
        if (StringUtils.isBlank(queryVO.getFormInstId())) {
            return new ArrayList<>();
        }

        List<BuPlanFormCheckRecordItem> itemList;
        // 是否需要查询整改关联信息
        Boolean needRectify = queryVO.getNeedRectify();
        // 是否需要所有作业表单信息
        Boolean allItems = queryVO.getAllItems();
        String recordIds = null;
        if (null == allItems || !allItems) {
            recordIds = buPlanFormCheckRecordMapper.selectRecordIds(queryVO.getTask2InstId());
        }
        if (null != needRectify && needRectify) {
            itemList = buPlanFormCheckRecordItemMapper.selectListWithRectifyByCheckRecordId(queryVO.getFormInstId(), recordIds);
        } else {
            itemList = buPlanFormCheckRecordItemMapper.selectListByCheckRecordId(queryVO.getFormInstId(), recordIds);
        }

        return itemList;
    }

    /**
     * @see BuRepairPlanFormsService#setCheckRecordItemWorkTime(String, Date)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean setCheckRecordItemWorkTime(String itemIds, Date workTime) throws Exception {
        List<String> checkItemIdList = Arrays.asList(itemIds.split(","));
        if (CollectionUtils.isEmpty(checkItemIdList)) {
            return true;
        }
        // 设置时间
        if (null == workTime) {
            workTime = new Date();
        }
        LambdaQueryWrapper<BuPlanFormCheckRecordItem> checkItemWrapper = new LambdaQueryWrapper<BuPlanFormCheckRecordItem>()
                .in(BuPlanFormCheckRecordItem::getId, checkItemIdList);
        BuPlanFormCheckRecordItem checkRecordItem = new BuPlanFormCheckRecordItem()
                .setWorkTime(workTime);
        buPlanFormCheckRecordItemMapper.update(checkRecordItem, checkItemWrapper);

        return true;
    }

    /**
     * @see BuRepairPlanFormsService#checkCheckRecordItem(FormCheckRecordCheckVO)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean checkCheckRecordItem(FormCheckRecordCheckVO checkVO) throws Exception {
        SysUserBO sysUserBO = buPlanFormWorkRecordChecksMapper.selectUserBOByUserName(checkVO.getCheckUserName());
        // 验证用户名密码
        if (!"~**&&##@@$$%%^^~".equals(checkVO.getCheckUserPwd())) {
            if (null == sysUserBO) {
                throw new BusinessException(ExceptionCode.USERNAME_OR_PASSWORD_ERROR);
            }
            String userpassword = PasswordUtil.encrypt(checkVO.getCheckUserName(), checkVO.getCheckUserPwd(), sysUserBO.getSalt());
            if (!userpassword.equals(sysUserBO.getPassword())) {
                throw new BusinessException(ExceptionCode.USERNAME_OR_PASSWORD_ERROR);
            }
        }
        // 设置时间
        if (null == checkVO.getCheckTime()) {
            checkVO.setCheckTime(new Date());
        }
        saveCheckRecordItemCheckResult(checkVO, sysUserBO);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatchCheckResult(DelWorkRecordCheckVO delWorkRecordCheckVO) throws Exception {
        List<String> idList = delWorkRecordCheckVO.getIds();
        idList.forEach(id -> {
            //删除新的作业记录表检查结果
            if (delWorkRecordCheckVO.getWorkRecordType() == 2) {
                delWorkRecordNew(delWorkRecordCheckVO, id);
            } else {
                if (delWorkRecordCheckVO.getCheckType() != 4) {
                    buPlanFormWorkRecordChecksMapper.delete(Wrappers.<BuPlanFormWorkRecordChecks>lambdaUpdate()
                            .eq(BuPlanFormWorkRecordChecks::getRecordDetailId, id)
                            .eq(BuPlanFormWorkRecordChecks::getCheckType, delWorkRecordCheckVO.getCheckType()));
                    buPlanFormWorkRecordDetailMapper.updateById(new BuPlanFormWorkRecordDetail().setId(id).setResult(0));
                } else {
                    buPlanFormWorkRecordDetailMapper.updateById(new BuPlanFormWorkRecordDetail().setId(id).setIsIgnore(0));
                }
            }
        });
        return true;
    }

    private void delWorkRecordNew(DelWorkRecordCheckVO checkVO, String id) {
        List<String> workRecordDetailIdList = getWorkRecordDetailIdList(checkVO, id);
        if (CollectionUtils.isNotEmpty(workRecordDetailIdList)) {
            if (checkVO.getCheckType() != 4) {
                buPlanFormWorkRecordChecksMapper.delete(Wrappers.<BuPlanFormWorkRecordChecks>lambdaUpdate()
                        .eq(BuPlanFormWorkRecordChecks::getCheckType, checkVO.getCheckType())
                        .in(BuPlanFormWorkRecordChecks::getRecordDetailId, workRecordDetailIdList));
                buPlanFormWorkRecordDetailMapper.updateById(new BuPlanFormWorkRecordDetail().setId(workRecordDetailIdList.get(0)).setResult(0));
            } else {
                buPlanFormWorkRecordDetailMapper.updateById(new BuPlanFormWorkRecordDetail().setId(workRecordDetailIdList.get(0)).setIsIgnore(0));
            }
        }
    }

    private List<String> getWorkRecordDetailIdList(DelWorkRecordCheckVO checkVO, String rowCol) {
        return buPlanFormWorkRecordDetailMapper.selectList(Wrappers.<BuPlanFormWorkRecordDetail>lambdaQuery()
                .select(BuPlanFormWorkRecordDetail::getId)
                .eq(BuPlanFormWorkRecordDetail::getWorkRecordId, checkVO.getFormInstId())
                .eq(BuPlanFormWorkRecordDetail::getDetailLineNum, Integer.valueOf(rowCol)))
                .stream().map(BuPlanFormWorkRecordDetail::getId).collect(Collectors.toList());
    }

    /**
     * @see BuRepairPlanFormsService#getFormWorkRecordExcelData(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public String getFormWorkRecordExcelData(String id) {
        BuPlWorkRecordExcelData plWorkRecordExcelData = buPlWorkRecordExcelDataMapper.selectById(id);
        if (null == plWorkRecordExcelData) {
            return null;
        }
        return plWorkRecordExcelData.getResult();
    }

    /**
     * @see BuRepairPlanFormsService#selectFormTemplateDataJsonByFormTemplateId(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public String selectFormTemplateDataJsonByFormTemplateId(String id) {
        String dataJson = buPlanFormWorkRecordMapper.selectFormTemplateDataJsonByFormTemplateId(id);
        if (StringUtils.isBlank(dataJson)) {
            throw new JeecgBootException("表单不存在");
        }
        return dataJson;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveFormWorkRecordExcelData(FromExcelDataVO excelDataVO) throws Exception {
        return new BuPlWorkRecordExcelData()
                .setId(excelDataVO.getFormInstId())
                .setResult(excelDataVO.getXlsJson())
                .insertOrUpdate();
    }

    /**
     * @see BuRepairPlanFormsService#listPlanFormInst(BuRepairPlanFormsInstQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<PlanFormInstance> listPlanFormInst(BuRepairPlanFormsInstQueryVO queryVO) throws Exception {
        return buRepairPlanFormsMapper.selectPlanFormInstList(queryVO);
    }

    /**
     * @see BuRepairPlanFormsService#addPlanFormInst(BuRepairPlanForms)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean addPlanFormInst(BuRepairPlanForms planForm) throws Exception {
        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = null == sysUser ? "admin" : sysUser.getId();

        Date now = new Date();

        // 表单池添加一条记录
        planForm.setId(UUIDGenerator.generate())
                .setFromBy(2);
        buRepairPlanFormsMapper.insert(planForm);

        // 根据表单类型创建表单实例
        BuRepairPlan plan = buRepairPlanMapper.selectById(planForm.getPlanId());
        savePlanFormInstAndRelation(planForm, plan.getTrainNo(), now, userId, null);

        return true;
    }

    /**
     * @see BuRepairPlanFormsService#deleteBatchPlanFormInst(List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatchPlanFormInst(List<BuRepairPlanFormInstDeleteVO> formInstDeleteVOList) throws Exception {
        for (BuRepairPlanFormInstDeleteVO planFormInstDeleteVO : formInstDeleteVOList) {
            // 验证表单实例是否已关联工单任务
            LambdaQueryWrapper<BuWorkOrderTaskFormInst> orderTaskFormInstWrapper = new LambdaQueryWrapper<BuWorkOrderTaskFormInst>()
                    .eq(BuWorkOrderTaskFormInst::getFormInstId, planFormInstDeleteVO.getFormInstId());
            List<BuWorkOrderTaskFormInst> orderTaskFormInstList = buWorkOrderTaskFormInstMapper.selectList(orderTaskFormInstWrapper);
            if (CollectionUtils.isNotEmpty(orderTaskFormInstList)) {
                String fromName = buWorkOrderTaskFormInstMapper.selectFormName(planFormInstDeleteVO.getFormInstId(), planFormInstDeleteVO.getFormType());
                throw new JeecgBootException((StringUtils.isNotBlank(fromName) ? fromName : "") + "表单实例已经被工单关联不可删除!");
            }

            Integer formType = planFormInstDeleteVO.getFormType();
            switch (formType) {
                case 1:
                    deleteDataForm(planFormInstDeleteVO);
                    break;
                case 3:
                    deleteWorkRecordForm(planFormInstDeleteVO);
                    break;
                case 4:
                    deleteCheckForm(planFormInstDeleteVO);
                    break;
                default:
                    break;
            }
        }

        return true;
    }

    /**
     * @see BuRepairPlanFormsService#regeneratePlanFormInst(String, Integer)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean regeneratePlanFormInst(String planFormId, Integer oldFormType) throws Exception {
        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = null == sysUser ? "admin" : sysUser.getId();

        Date now = new Date();

        // 查询列计划表单
        BuRepairPlanForms planForm = buRepairPlanFormsMapper.selectById(planFormId);
        if (null == oldFormType) {
            oldFormType = planForm.getFormType();
        }

        // 删除旧的实例
        String oldFormInstId = null;
        if (oldFormType == 1) {
            LambdaQueryWrapper<BuPlanFormDataRecord> dataRecordWrapper = new LambdaQueryWrapper<BuPlanFormDataRecord>()
                    .eq(BuPlanFormDataRecord::getPlanFormId, planFormId);
            List<BuPlanFormDataRecord> dataRecordList = buPlanFormDataRecordMapper.selectList(dataRecordWrapper);
            for (BuPlanFormDataRecord dataRecord : dataRecordList) {
                oldFormInstId = dataRecord.getId();

                BuRepairPlanFormInstDeleteVO planFormInstDeleteVO = new BuRepairPlanFormInstDeleteVO()
                        .setFormInstId(oldFormInstId)
                        .setFormType(oldFormType);
                deleteDataForm(planFormInstDeleteVO);
            }
        } else if (oldFormType == 3) {
            LambdaQueryWrapper<BuPlanFormWorkRecord> workRecordWrapper = new LambdaQueryWrapper<BuPlanFormWorkRecord>()
                    .eq(BuPlanFormWorkRecord::getPlanFormId, planFormId);
            List<BuPlanFormWorkRecord> workRecordList = buPlanFormWorkRecordMapper.selectList(workRecordWrapper);
            for (BuPlanFormWorkRecord workRecord : workRecordList) {
                oldFormInstId = workRecord.getId();

                BuRepairPlanFormInstDeleteVO planFormInstDeleteVO = new BuRepairPlanFormInstDeleteVO()
                        .setFormInstId(oldFormInstId)
                        .setFormType(oldFormType);
                deleteWorkRecordForm(planFormInstDeleteVO);
            }
        } else if (oldFormType == 4) {
            LambdaQueryWrapper<BuPlanFormCheckRecord> checkRecordWrapper = new LambdaQueryWrapper<BuPlanFormCheckRecord>()
                    .eq(BuPlanFormCheckRecord::getPlanFormId, planFormId);
            List<BuPlanFormCheckRecord> checkRecordList = buPlanFormCheckRecordMapper.selectList(checkRecordWrapper);
            for (BuPlanFormCheckRecord checkRecord : checkRecordList) {
                oldFormInstId = checkRecord.getId();

                BuRepairPlanFormInstDeleteVO planFormInstDeleteVO = new BuRepairPlanFormInstDeleteVO()
                        .setFormInstId(oldFormInstId)
                        .setFormType(oldFormType);
                deleteCheckForm(planFormInstDeleteVO);
            }
        }
        // 删除旧的实例时，把列计划表单也删除了，重新插入
        buRepairPlanFormsMapper.insert(planForm);
        // 生成新的实例
        BuRepairPlan plan = buRepairPlanMapper.selectById(planForm.getPlanId());
        savePlanFormInstAndRelation(planForm, plan.getTrainNo(), now, userId, oldFormInstId);

        return true;
    }


    private void setDetailCheck(BuPlanFormWorkRecord planFormWorkRecord) {
        if (planFormWorkRecord == null) {
            return;
        }
        List<BuPlanFormWorkRecordDetail> detailList = planFormWorkRecord.getDetailList();
        if (CollectionUtils.isEmpty(detailList)) {
            return;
        }

        for (BuPlanFormWorkRecordDetail detail : detailList) {
            List<BuPlanFormWorkRecordChecks> checksList = detail.getChecksList();
            if (CollectionUtils.isEmpty(checksList)) {
                detail.setChecksList(new ArrayList<>());
                continue;
            }

            Integer currentMaxCheckType = 0;
            for (BuPlanFormWorkRecordChecks check : checksList) {
                Integer checkType = check.getCheckType();
                if (checkType > currentMaxCheckType) {
                    currentMaxCheckType = checkType;
                }
                switch (checkType) {
                    case 1:
                        detail.setSelfCheck(check.getCheckUserName());
                        break;
                    case 2:
                        detail.setGuarderCheck(check.getCheckUserName());
                        break;
                    case 3:
                        detail.setMonitorCheck(check.getCheckUserName());
                        break;
                    case 4:
                        detail.setMonitorAcceptance(check.getCheckUserName());
                        break;
                    default:
                        break;
                }
            }
            detail.setCurrentMaxCheckType(currentMaxCheckType);
        }
    }

    private void setDetailCategory(BuPlanFormWorkRecord planFormWorkRecord, Boolean needCategory) {
        if (planFormWorkRecord == null) {
            return;
        }
        if (needCategory == null || !needCategory) {
            planFormWorkRecord.setCategoryList(new ArrayList<>());
            return;
        }

        List<BuPlanFormWorkRecordDetail> workRecordDetailList = planFormWorkRecord.getDetailList();
        Map<String, List<BuPlanFormWorkRecordDetail>> categoryIdDetailListMap = workRecordDetailList.stream()
                .collect(Collectors.groupingBy(BuPlanFormWorkRecordDetail::getCategoryId));

        List<BuWorkRecordCategoryBO> categoryList = new ArrayList<>();
        for (Map.Entry<String, List<BuPlanFormWorkRecordDetail>> categoryIdDetailListEntry : categoryIdDetailListMap.entrySet()) {
            String categoryId = categoryIdDetailListEntry.getKey();
            List<BuPlanFormWorkRecordDetail> detailList = categoryIdDetailListEntry.getValue();

            BuWorkRecordCategoryBO categoryBO = new BuWorkRecordCategoryBO()
                    .setId(categoryId)
                    .setRecIndex(detailList.get(0).getRecIndex())
                    .setReguTitle(detailList.get(0).getReguTitle())
                    .setDetailList(detailList);
            categoryList.add(categoryBO);
        }
        planFormWorkRecord.setCategoryList(categoryList);
        planFormWorkRecord.setDetailList(new ArrayList<>());
    }

    private void deleteOldFormsRelationData(List<String> planFormsIdList) {
        if (CollectionUtils.isEmpty(planFormsIdList)) {
            return;
        }

        // 删除在线表单(数据记录表)数据
        LambdaQueryWrapper<BuPlanFormDataRecord> dataRecordWrapper = new LambdaQueryWrapper<BuPlanFormDataRecord>()
                .in(BuPlanFormDataRecord::getPlanFormId, planFormsIdList);
        List<BuPlanFormDataRecord> dataRecordList = buPlanFormDataRecordMapper.selectList(dataRecordWrapper);
        if (CollectionUtils.isNotEmpty(dataRecordList)) {
            Set<String> dataRecordIdSet = new HashSet<>();
            for (BuPlanFormDataRecord dataRecord : dataRecordList) {
                if (dataRecord.getStatus() == 1) {
                    throw new JeecgBootException("更新失败，在线表单(数据记录表)已填写");
                }
                dataRecordIdSet.add(dataRecord.getId());
            }

            LambdaQueryWrapper<BuPlanFormValues> valuesWrapper = new LambdaQueryWrapper<BuPlanFormValues>()
                    .in(BuPlanFormValues::getFormDataRecordId, dataRecordIdSet);
            buPlanFormValuesMapper.delete(valuesWrapper);
            buPlanFormDataRecordMapper.deleteBatchIds(dataRecordIdSet);
        }

        // 删除作业记录表数据
        LambdaQueryWrapper<BuPlanFormWorkRecord> workRecordWrapper = new LambdaQueryWrapper<BuPlanFormWorkRecord>()
                .in(BuPlanFormWorkRecord::getPlanFormId, planFormsIdList);
        List<BuPlanFormWorkRecord> workRecordList = buPlanFormWorkRecordMapper.selectList(workRecordWrapper);
        if (CollectionUtils.isNotEmpty(workRecordList)) {
            Set<String> workRecordIdSet = new HashSet<>();
            for (BuPlanFormWorkRecord workRecord : workRecordList) {
                if (workRecord.getStatus() == 1) {
                    throw new JeecgBootException("更新失败，作业记录表已填写");
                }
                workRecordIdSet.add(workRecord.getId());
            }

            LambdaQueryWrapper<BuPlanFormWorkRecordDetail> workRecordDetailWrapper = new LambdaQueryWrapper<BuPlanFormWorkRecordDetail>()
                    .in(BuPlanFormWorkRecordDetail::getWorkRecordId, workRecordIdSet);
            List<BuPlanFormWorkRecordDetail> workRecordDetailList = buPlanFormWorkRecordDetailMapper.selectList(workRecordDetailWrapper);
            if (CollectionUtils.isNotEmpty(workRecordDetailList)) {
                Set<String> workRecordDetailIdSet = workRecordDetailList.stream()
                        .map(BuPlanFormWorkRecordDetail::getId)
                        .collect(Collectors.toSet());
                LambdaQueryWrapper<BuPlanFormWorkRecordChecks> workRecordCheckWrapper = new LambdaQueryWrapper<BuPlanFormWorkRecordChecks>()
                        .in(BuPlanFormWorkRecordChecks::getRecordDetailId, workRecordDetailIdSet);
                buPlanFormWorkRecordChecksMapper.delete(workRecordCheckWrapper);
            }
            buPlanFormWorkRecordDetailMapper.delete(workRecordDetailWrapper);
            buPlanFormWorkRecordMapper.deleteBatchIds(workRecordIdSet);
        }

        // 删除检查记录表数据
        LambdaQueryWrapper<BuPlanFormCheckRecord> checkRecordWrapper = new LambdaQueryWrapper<BuPlanFormCheckRecord>()
                .in(BuPlanFormCheckRecord::getPlanFormId, planFormsIdList);
        List<BuPlanFormCheckRecord> checkRecordList = buPlanFormCheckRecordMapper.selectList(checkRecordWrapper);
        if (CollectionUtils.isNotEmpty(checkRecordList)) {
            Set<String> checkRecordIdSet = checkRecordList.stream()
                    .map(BuPlanFormCheckRecord::getId)
                    .collect(Collectors.toSet());

            LambdaQueryWrapper<BuPlanFormCheckRecordItem> checkRecordItemWrapper = new LambdaQueryWrapper<BuPlanFormCheckRecordItem>()
                    .in(BuPlanFormCheckRecordItem::getCheckId, checkRecordIdSet);
            List<BuPlanFormCheckRecordItem> checkRecordItemList = buPlanFormCheckRecordItemMapper.selectList(checkRecordItemWrapper);
            if (CollectionUtils.isNotEmpty(checkRecordItemList)) {
                Set<String> checkRecordItemIdSet = checkRecordItemList.stream()
                        .map(BuPlanFormCheckRecordItem::getId)
                        .collect(Collectors.toSet());
                LambdaQueryWrapper<BuPlanFormCheckRecordRectify> checkRecordRectifyWrapper = new LambdaQueryWrapper<BuPlanFormCheckRecordRectify>()
                        .in(BuPlanFormCheckRecordRectify::getCheckId, checkRecordItemIdSet);
                buPlanFormCheckRecordRectifyMapper.delete(checkRecordRectifyWrapper);
            }
            buPlanFormCheckRecordItemMapper.delete(checkRecordItemWrapper);
            buPlanFormWorkRecordMapper.deleteBatchIds(checkRecordList);
        }
    }

    private void savePlanFormInstAndRelation(BuRepairPlanForms planForm, String trainNo, Date now, String userId, String formInstId) {
        Integer formType = planForm.getFormType();

        if (1 == formType) {
            // 在线表单(数据记录表)
            saveFormDataRecord(planForm, trainNo, now, userId, formInstId);
        } else if (2 == formType) {
            // 文件表单，暂时无操作
        } else if (3 == formType) {
            // 作业记录表
            savePlanFormWorkRecord(planForm, trainNo, now, userId, formInstId);
        } else if (4 == formType) {
            // 检查记录表
            savePlanFormCheckRecord(planForm, trainNo, now, userId, formInstId);
        }
    }

    private void saveFormDataRecord(BuRepairPlanForms planForm, String trainNo, Date now, String userId, String formInstId) {
        if (StringUtils.isBlank(formInstId)) {
            formInstId = UUIDGenerator.generate();
        }

        // 插入数据记录表实列
        BuPlanFormDataRecord planFormDataRecord = new BuPlanFormDataRecord()
                .setId(formInstId)
                .setPlanId(planForm.getPlanId())
                .setPlanFormId(planForm.getId())
                .setFormObjId(planForm.getObjId())
                .setFormIndex(1)
                .setTrainNo(trainNo)
                .setAssetTypeId(planForm.getAssetTypeId())
                .setStructDetailId(planForm.getTrainStructId())
                .setTrainAssetId(planForm.getAssetId())
                .setStatus(0)
                .setWriteDate(null)
                .setWriteUserId(null)
                .setResult(null)
                .setCreateTime(now)
                .setCreateBy(userId)
                .setCheckResult(0)
                .setCheckDate(null)
                .setCheckUserId(null)
                .setTitle(planForm.getTitle());
        // 现在使用资产结构，所以要在生产表单实例时，把表单实例的对应的设备类型id，车辆结构id，车辆设备id 置空
//        if (null == asset) {
//            planFormDataRecord.setAssetTypeId(form.getAssetTypeId())
//                    .setStructDetailId(form.getTrainStructId());
//        } else {
//            planFormDataRecord.setAssetTypeId(asset.getAssetTypeId())
//                    .setStructDetailId(asset.getStructDetailId())
//                    .setTrainAssetId(asset.getId());
//        }
        buPlanFormDataRecordMapper.insert(planFormDataRecord);
    }

    private void savePlanFormWorkRecord(BuRepairPlanForms planForm, String trainNo, Date now, String userId, String formInstId) {
        if (StringUtils.isBlank(formInstId)) {
            formInstId = UUIDGenerator.generate();
        }

        // 查询作业记录表明细
        List<BuWorkRecordDetail> workRecordDetailList = buRepairPlanFormsMapper.selectBuWorkRecordDetailByWorkRecordId(planForm.getObjId());

        // 插入作业记录表实列
        BuPlanFormWorkRecord planFormWorkRecord = new BuPlanFormWorkRecord()
                .setId(formInstId)
                .setPlanId(planForm.getPlanId())
                .setPlanFormId(planForm.getId())
                .setFormObjId(planForm.getObjId())
                .setTrainNo(trainNo)
                .setAssetTypeId(planForm.getAssetTypeId())
                .setStructDetailId(planForm.getTrainStructId())
                .setTrainAssetId(planForm.getAssetId())
                .setManufNo(null)
                .setTrainAssetIdUp(null)
                .setManufNoUp(null)
                .setFormIndex(1)
                .setStatus(0)
                .setWorkDate(null)
                .setFinishDate(null)
                .setWorkGroupId(null)
                .setRemark(planForm.getRemark())
                .setCheckResult(0)
                .setCheckDate(null)
                .setCheckUserId(null)
                .setCreateTime(now)
                .setCreateBy(userId)
                .setTitle(planForm.getTitle())
                .setWorkRecordType(planForm.getWorkRecordType() != null ? planForm.getWorkRecordType() : 1);
        // 现在使用资产结构，所以要在生产表单实例时，把表单实例的对应的设备类型id，车辆结构id，车辆设备id 置空
//        if (null == asset) {
//            planFormWorkRecord.setAssetTypeId(planForm.getAssetTypeId())
//                    .setStructDetailId(planForm.getTrainStructId());
//        } else {
//            planFormWorkRecord.setAssetTypeId(asset.getAssetTypeId())
//                    .setStructDetailId(asset.getStructDetailId())
//                    .setTrainAssetId(asset.getId())
//                    .setManufNo(asset.getManufNo());
//        }
        buPlanFormWorkRecordMapper.insert(planFormWorkRecord);

        // 插入作业记录表实列明细
        if (CollectionUtils.isNotEmpty(workRecordDetailList)) {
            List<BuPlanFormWorkRecordDetail> formWorkRecordDetailList = new ArrayList<>();
            for (BuWorkRecordDetail workRecordDetail : workRecordDetailList) {
                BuPlanFormWorkRecordDetail planFormWorkRecordDetail = new BuPlanFormWorkRecordDetail()
                        .setId(UUIDGenerator.generate())
                        .setWorkRecordId(planFormWorkRecord.getId())
                        .setWorkRecDetailId(workRecordDetail.getId())
                        .setReguDetailId(workRecordDetail.getReguDetailId())
                        .setResult(0)
                        .setIsIgnore(0)
                        .setCreateTime(new Date())
                        .setUpdateTime(new Date());
                formWorkRecordDetailList.add(planFormWorkRecordDetail);
            }
            List<List<BuPlanFormWorkRecordDetail>> formWorkRecordDetailBatchSubList = DatabaseBatchSubUtil.batchSubList(formWorkRecordDetailList);
            for (List<BuPlanFormWorkRecordDetail> formWorkRecordDetailBatchSub : formWorkRecordDetailBatchSubList) {
                buPlanFormWorkRecordDetailMapper.insertList(formWorkRecordDetailBatchSub);
            }
        }
    }

    private void savePlanFormCheckRecord(BuRepairPlanForms planForm, String trainNo, Date now, String userId, String formInstId) {
        if (StringUtils.isBlank(formInstId)) {
            formInstId = UUIDGenerator.generate();
        }

        // 查询检查记录表及其检查项明细
        BuWorkCheck workCheck = buWorkCheckDispatchMapper.selectById(planForm.getObjId());
        LambdaQueryWrapper<BuWorkCheckItem> checkItemWrapper = new LambdaQueryWrapper<BuWorkCheckItem>()
                .eq(BuWorkCheckItem::getCheckId, planForm.getObjId());
        List<BuWorkCheckItem> checkItemList = buWorkCheckItemDispatchMapper.selectList(checkItemWrapper);

        // 插入检查记录表实列
        BuPlanFormCheckRecord planFormCheckRecord = new BuPlanFormCheckRecord()
                .setId(formInstId)
                .setPlanId(planForm.getPlanId())
                .setPlanFormId(planForm.getId())
                .setFormObjId(planForm.getObjId())
                .setTitle(planForm.getTitle())
                .setTrainNo(trainNo)
                .setAssetTypeId(StringUtils.isBlank(planForm.getAssetTypeId()) ? workCheck.getAssetTypeId() : planForm.getAssetTypeId())
                .setAssetStructId(StringUtils.isBlank(planForm.getTrainStructId()) ? workCheck.getAssetStructId() : planForm.getTrainStructId())
                .setAssetId(StringUtils.isBlank(planForm.getAssetId()) ? workCheck.getAssetId() : planForm.getAssetId())
                .setAssetName(null)
                .setAssetNo(null)
                .setLocation(null)
                .setPeriod(null)
                .setGroupId(null)
                .setGroupName(null)
                .setStatus(0)
                .setRemark(null)
                .setCreateGroupId(null)
                .setCreateTime(now)
                .setCreateBy(userId)
                .setFormIndex(1);
        // 现在使用资产结构，所以要在生产表单实例时，把表单实例的对应的设备类型id，车辆结构id，车辆设备id 置空
//        if (null == asset) {
//            planFormCheckRecord.setAssetTypeId(StringUtils.isBlank(planForm.getAssetTypeId()) ? workCheck.getAssetTypeId() : planForm.getAssetTypeId())
//                    .setAssetStructId(StringUtils.isBlank(planForm.getTrainStructId()) ? workCheck.getAssetStructId() : planForm.getTrainStructId());
//        } else {
//            planFormCheckRecord.setAssetTypeId(asset.getAssetTypeId())
//                    .setAssetStructId(asset.getStructDetailId())
//                    .setAssetId(asset.getId())
//                    .setAssetName(asset.getAssetName())
//                    .setAssetNo(asset.getAssetNo());
//        }
        buPlanFormCheckRecordMapper.insert(planFormCheckRecord);

        // 插入检查记录表实列检查项明细
        if (CollectionUtils.isNotEmpty(checkItemList)) {
            List<BuPlanFormCheckRecordItem> formCheckRecordItemList = new ArrayList<>();
            for (BuWorkCheckItem checkItem : checkItemList) {
                BuPlanFormCheckRecordItem formCheckRecordItem = new BuPlanFormCheckRecordItem()
                        .setId(UUIDGenerator.generate())
                        .setCheckId(planFormCheckRecord.getId())
                        .setSortNo(checkItem.getSortNo())
                        .setTitle(checkItem.getTitle())
                        .setContent(checkItem.getContent())
                        .setCheckLevel(checkItem.getCheckLevel())
                        .setCheckDesc(checkItem.getCheckDesc())
                        .setCheckResult(1)
                        .setWorkTime(null)
                        .setCheckMethod(checkItem.getCheckMethod())
                        .setCheckUserId(null)
                        .setCheckUserName(null)
                        .setCheckTime(null)
                        .setCreateGroupId(null)
                        .setCheckDetailId(checkItem.getId());
                formCheckRecordItemList.add(formCheckRecordItem);
            }
            List<List<BuPlanFormCheckRecordItem>> formCheckRecordItemBatchSubList = DatabaseBatchSubUtil.batchSubList(formCheckRecordItemList);
            for (List<BuPlanFormCheckRecordItem> formCheckRecordItemBatchSub : formCheckRecordItemBatchSubList) {
                buPlanFormCheckRecordItemMapper.insertList(formCheckRecordItemBatchSub);
            }
        }
    }

    private void deleteFormValues(BuPlanFormDataRecord formDataRecord) {
        if (null == formDataRecord || StringUtils.isBlank(formDataRecord.getId())) {
            return;
        }

        LambdaQueryWrapper<BuPlanFormValues> wrapper = new LambdaQueryWrapper<BuPlanFormValues>()
                .eq(BuPlanFormValues::getFormDataRecordId, formDataRecord.getId());
        buPlanFormValuesMapper.delete(wrapper);
    }

    private void saveFormValues(BuPlanFormDataRecord formDataRecord, Date now) {
        // 查询表单的测量项
        String formObjId = formDataRecord.getFormObjId();
        LambdaQueryWrapper<BuWorkMeasureItem> measureItemWrapper = new LambdaQueryWrapper<BuWorkMeasureItem>()
                .eq(BuWorkMeasureItem::getCustomId, formObjId);
        List<BuWorkMeasureItem> measureItemList = buWorkMeasureItemDispatchMapper.selectList(measureItemWrapper);

        if (CollectionUtils.isEmpty(measureItemList)) {
            return;
        }

        Map<String, Object> replaceParamMap = initReplaceParamMap(formDataRecord);
        Map<Integer, String> operatorTextMap = initOperatorTextMap();

        String result = formDataRecord.getResult();
        JSONArray resultDataArray = JSON.parseArray(result);
        // 当存在记录时，找result中对应的值填写，有填写则保存记录表数据值记录
        List<BuPlanFormValues> valueList = new ArrayList<>();
        for (BuWorkMeasureItem measureItem : measureItemList) {
            Integer row1 = measureItem.getRow1();
            Integer col1 = measureItem.getCol1();
            Integer row2 = measureItem.getRow2();
            Integer col2 = measureItem.getCol2();

            for (int rowIndex = row1; rowIndex <= row2; rowIndex++) {
                for (int columnIndex = col1; columnIndex <= col2; columnIndex++) {
                    Double value = getResultValue(resultDataArray, rowIndex, columnIndex);
                    if (null != value) {
                        // 获取是否预警，0不需要预警 1操作符预警 2操作符2预警
                        int alertType = checkAlertType(value, measureItem);
                        // 保存记录表数据值记录bu_pl_data_record_value
                        BuPlanFormValues formValues = new BuPlanFormValues()
                                .setPlanFormId(formDataRecord.getPlanFormId())
                                .setFormDataRecordId(formDataRecord.getId())
                                .setFormObjId(formDataRecord.getFormObjId())
                                .setMeasureThresholdId(measureItem.getId())
                                .setAlertValue(value)
                                .setGroupId(formDataRecord.getWriteGroupId())
                                .setAlertTime(now)
                                .setOrderId(formDataRecord.getOrderId())
                                .setOrderTaskId(formDataRecord.getOrderTaskId())
                                .setLinkDomain("$" + rowIndex + "$" + columnIndex)
                                .setCreateTime(now)
                                .setUpdateTime(now);
                        if (alertType != 0) {
                            String template = measureItem.getTemplate();
                            replaceParamMap.put("name", measureItem.getItemName());
                            replaceParamMap.put("operator", getOperatorText(alertType, measureItem, operatorTextMap));
                            replaceParamMap.put("value", getThresholdValue(alertType, measureItem));
                            String alertMessage = processTemplate(template, replaceParamMap);
                            formValues.setAlertHappen(1)
                                    .setAlertMessage(alertMessage)
                                    .setStatus(0)
                                    // 需要预警：操作符2预警，当前的阈值设置为测量项的阈值2；操作符预警，当前的阈值设置为测量项的阈值2
                                    .setThresholdValue(2 == alertType ? measureItem.getThreshold2() : measureItem.getThreshold());
                        } else {
                            formValues.setAlertHappen(0)
                                    .setStatus(1)
                                    // 不需要预警，当前的阈值设置为测量项的阈值
                                    .setThresholdValue(measureItem.getThreshold());
                        }
                        valueList.add(formValues);
                    }
                }
            }
        }

        // 批量保存测量值记录
        if (CollectionUtils.isNotEmpty(valueList)) {
            List<List<BuPlanFormValues>> batchSubList = DatabaseBatchSubUtil.batchSubList(valueList);
            for (List<BuPlanFormValues> batchSub : batchSubList) {
                buPlanFormValuesMapper.insertList(batchSub);
            }
        }
    }

    private Double getResultValue(JSONArray resultDataArray, Integer rowIndex, Integer columnIndex) {
        JSONArray rowData = resultDataArray.getJSONArray(rowIndex);
        if (null != rowData) {
            JSONObject columnData = rowData.getJSONObject(columnIndex);
            if (null != columnData) {
                return columnData.getDouble("v");
            }
        }
        return null;
    }

    private int checkAlertType(Double value, BuWorkMeasureItem measureItem) {
        boolean alert1 = checkThreshold(measureItem.getOperator(), measureItem.getThreshold(), value);
        boolean alert2 = checkThreshold(measureItem.getOperator2(), measureItem.getThreshold2(), value);

        if (alert1) {
            return 1;
        } else if (alert2) {
            return 2;
        } else {
            return 0;
        }
    }

    private boolean checkThreshold(Integer operator, Double threshold, Double value) {
        if (null == operator) {
            return false;
        }
        if (null == threshold || null == value) {
            return false;
        }

        boolean alert = true;
        // 操作符 1等于2小于3小于等于4大于5大于等于6不等于
        switch (operator) {
            case 1:
                if (value.equals(threshold)) {
                    alert = false;
                }
                break;
            case 2:
                if (value < threshold) {
                    alert = false;
                }
                break;
            case 3:
                if (value <= threshold) {
                    alert = false;
                }
                break;
            case 4:
                if (value > threshold) {
                    alert = false;
                }
                break;
            case 5:
                if (value >= threshold) {
                    alert = false;
                }
                break;
            case 6:
                if (!value.equals(threshold)) {
                    alert = false;
                }
                break;
            default:
                break;
        }

        return alert;
    }

    private Map<String, Object> initReplaceParamMap(BuPlanFormDataRecord formDataRecord) {
        BuPlanFormDataRecord dataRecordInfo = buPlanFormDataRecordMapper.selectDataRecordById(formDataRecord.getId());

        Map<String, Object> paramMap = new HashMap<>(8);
        paramMap.put("trainNo", dataRecordInfo.getTrainNo());
        paramMap.put("assetTypeName", dataRecordInfo.getAssetTypeName());
        paramMap.put("structDetailName", dataRecordInfo.getStructDetailName());
        paramMap.put("trainAssetName", dataRecordInfo.getTrainAssetName());

        return paramMap;
    }

    private Map<Integer, String> initOperatorTextMap() {
        Map<Integer, String> operatorTextMap = new HashMap<>(8);

        operatorTextMap.put(1, "等于");
        operatorTextMap.put(2, "小于");
        operatorTextMap.put(3, "小于等于");
        operatorTextMap.put(4, "大于");
        operatorTextMap.put(5, "大于等于");
        operatorTextMap.put(6, "不等于");

        return operatorTextMap;
    }

    private Object getOperatorText(int alertType, BuWorkMeasureItem measureItem, Map<Integer, String> operatorTextMap) {
        Integer operator = measureItem.getOperator();
        if (alertType == 2) {
            operator = measureItem.getOperator2();
        }
        return operatorTextMap.get(operator);
    }

    private Double getThresholdValue(int alertType, BuWorkMeasureItem measureItem) {
        Double threshold = measureItem.getThreshold();
        if (alertType == 2) {
            threshold = measureItem.getThreshold2();
        }
        return threshold;
    }

    private String processTemplate(String template, Map<String, Object> params) {
        StringBuffer stringBuffer = new StringBuffer();
        Matcher matcher = TirosConstant.TEMPLATE_PATTERN.matcher(template);
        while (matcher.find()) {
            String param = matcher.group();
            Object value = params.get(param.substring(2, param.length() - 1));
            matcher.appendReplacement(stringBuffer, value == null ? "" : value.toString());
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    private void submitWorkRecordChecks(CheckCommonVO checkVO, String checkUserId, Integer colIndex) {
        if (StringUtils.isBlank(checkVO.getDetailIds())) {
            return;
        }

        Date now = new Date();
        List<String> detailIdList = Arrays.asList(checkVO.getDetailIds().split(","));

        // 是否忽略检查
        if (null == checkVO.getIsIgnore()) {
            checkVO.setIsIgnore(0);
        }
        Integer isIgnore = checkVO.getIsIgnore();
        if (1 == isIgnore) {
            // 忽略检查，设置明细的忽略信息，并且不用设置检查信息
            BuPlanFormWorkRecordDetail ignoreDetail = new BuPlanFormWorkRecordDetail()
                    .setIsIgnore(1)
                    .setIgnoreDesc(StringUtils.isBlank(checkVO.getIgnoreDesc()) ? checkVO.getResultDesc() : checkVO.getIgnoreDesc());
            LambdaQueryWrapper<BuPlanFormWorkRecordDetail> detailWrapper = new LambdaQueryWrapper<BuPlanFormWorkRecordDetail>()
                    .in(BuPlanFormWorkRecordDetail::getId, detailIdList);
            buPlanFormWorkRecordDetailMapper.update(ignoreDetail, detailWrapper);
        } else {
            // 一个明细的一个检查类型只能有一条记录，删除旧的
            LambdaQueryWrapper<BuPlanFormWorkRecordChecks> deleteWrapper = new LambdaQueryWrapper<BuPlanFormWorkRecordChecks>()
                    .eq(BuPlanFormWorkRecordChecks::getCheckType, checkVO.getType())
                    .in(BuPlanFormWorkRecordChecks::getRecordDetailId, detailIdList);
            buPlanFormWorkRecordChecksMapper.delete(deleteWrapper);

            // 查询明细的检查历史信息
            LambdaQueryWrapper<BuPlanFormWorkRecordChecks> detailChecksWrapper = new LambdaQueryWrapper<BuPlanFormWorkRecordChecks>()
                    .in(BuPlanFormWorkRecordChecks::getRecordDetailId, detailIdList);
            List<BuPlanFormWorkRecordChecks> detailCheckList = buPlanFormWorkRecordChecksMapper.selectList(detailChecksWrapper);

            // 保存检查结果
            for (String detailId : detailIdList) {
                BuPlanFormWorkRecordChecks recordCheck = new BuPlanFormWorkRecordChecks()
                        .setRecordDetailId(detailId)
                        .setCheckType(checkVO.getType())
                        .setCheckUserId(checkUserId)
                        .setCheckTime(now)
                        .setResult(checkVO.getResult())
                        .setResultDesc(checkVO.getResultDesc())
                        .setColNum(colIndex)
                        .setTogetherCheckUserIds(checkVO.getTogetherCheckUserIds())
                        .setTogetherCheckUserNames(checkVO.getTogetherCheckUserNames());
                buPlanFormWorkRecordChecksMapper.insert(recordCheck);

                // 根据该明细的检查历史信息，确定是否需要覆盖明细信息中的检查结果和结果描述
                List<BuPlanFormWorkRecordChecks> checkList = detailCheckList.stream()
                        .filter(check -> detailId.equals(check.getRecordDetailId()))
                        .collect(Collectors.toList());
                boolean needRewriteDetailResult = getNeedRewriteDetailResult(checkList, checkVO.getType());
                if (needRewriteDetailResult) {
                    BuPlanFormWorkRecordDetail updateResult = new BuPlanFormWorkRecordDetail()
                            .setId(detailId)
                            .setResult(1 == checkVO.getResult() ? 0 : 1)
                            .setWorkInfo(checkVO.getResultDesc());
                    buPlanFormWorkRecordDetailMapper.updateById(updateResult);
                }
            }
        }

        // 更新作业记录表实例信息
        // 获取作业记录表实例明细
        List<BuPlanFormWorkRecordDetail> workRecordDetailList = buPlanFormWorkRecordDetailMapper.selectBatchIds(detailIdList);
        List<String> formWorkRecordIdList = workRecordDetailList.stream()
                .map(BuPlanFormWorkRecordDetail::getWorkRecordId)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());

        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();
        String groupId = buRepairPlanFormsMapper.selectUserGroupId(userId);

        for (String formWorkRecordId : formWorkRecordIdList) {
            // 如果是自检，更新作业日期、作业班组、状态为已填写
            if (1 == checkVO.getType()) {
                BuPlanFormWorkRecord formWorkRecord = new BuPlanFormWorkRecord()
                        .setId(formWorkRecordId)
                        .setWorkDate(now)
                        .setWorkGroupId(groupId)
                        .setStatus(1);
                buPlanFormWorkRecordMapper.updateById(formWorkRecord);
            }
        }
    }


    private boolean getNeedRewriteDetailResult(List<BuPlanFormWorkRecordChecks> checkList, int type) {
        /**
         * 自检	1
         * 互检	2
         * 专检	3
         * 抽检	4
         * 过程检	5
         * 过程检确认	6
         * 交接检	7
         * 交接检确认	8
         * 专工验收	9
         */
        if (CollectionUtils.isEmpty(checkList)) {
            return true;
        }

        for (BuPlanFormWorkRecordChecks check : checkList) {
            if (null != check.getCheckType() && check.getCheckType() > type) {
                return false;
            }
        }

        return true;
    }

    private void extractRectify(BuPlanFormCheckRecord checkRecord) {
        if (null == checkRecord) {
            return;
        }
        List<BuPlanFormCheckRecordItem> itemList = checkRecord.getItemList();
        if (CollectionUtils.isEmpty(itemList)) {
            return;
        }

        List<BuPlanFormCheckRecordRectify> allRectifyList = new ArrayList<>();
        for (BuPlanFormCheckRecordItem item : itemList) {
            String itemInfo = "序号：" + item.getSortNo() + " 项点：" + item.getTitle() + " 检查内容：" + item.getContent();
            List<BuPlanFormCheckRecordRectify> rectifyList = item.getRectifyList();
            if (CollectionUtils.isNotEmpty(rectifyList)) {
                rectifyList.forEach(rectify -> rectify.setItemInfo(itemInfo));
                allRectifyList.addAll(rectifyList);
            }
        }
        checkRecord.setRectifyList(allRectifyList);
    }

    private void saveCheckRecordItemCheckResult(FormCheckRecordCheckVO checkVO, SysUserBO checkUser) {
        String itemIds = checkVO.getItemIds();
        List<String> checkItemIdList = Arrays.asList(itemIds.split(","));

        if (CollectionUtils.isEmpty(checkItemIdList)) {
            return;
        }

        // 设置检查记录表实例检查项明细属性
        LambdaQueryWrapper<BuPlanFormCheckRecordItem> checkRecordItemWrapper = new LambdaQueryWrapper<BuPlanFormCheckRecordItem>()
                .in(BuPlanFormCheckRecordItem::getId, checkItemIdList);
        BuPlanFormCheckRecordItem checkRecordItem = new BuPlanFormCheckRecordItem()
                .setCheckResult(checkVO.getCheckResult())
                .setCheckMethod(checkVO.getCheckMethod())
                .setCheckDesc(checkVO.getCheckDesc())
                .setCheckUserId(checkUser.getId())
                .setCheckUserName(checkUser.getRealname())
                .setCheckTime(checkVO.getCheckTime());
        buPlanFormCheckRecordItemMapper.update(checkRecordItem, checkRecordItemWrapper);

        // 将检查记录表实例的检查状态改成“已检查”
        List<BuPlanFormCheckRecordItem> checkRecordItemList = buPlanFormCheckRecordItemMapper.selectList(checkRecordItemWrapper);
        Set<String> checkRecordIdSet = checkRecordItemList.stream()
                .map(BuPlanFormCheckRecordItem::getCheckId)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(checkRecordIdSet)) {
            LambdaQueryWrapper<BuPlanFormCheckRecord> checkRecordWrapper = new LambdaQueryWrapper<BuPlanFormCheckRecord>()
                    .in(BuPlanFormCheckRecord::getId, checkRecordIdSet);
            BuPlanFormCheckRecord checkRecord = new BuPlanFormCheckRecord()
                    .setStatus(1);
            buPlanFormCheckRecordMapper.update(checkRecord, checkRecordWrapper);
        }
    }

    private void deleteDataForm(BuRepairPlanFormInstDeleteVO planFormInstDeleteVO) {
        BuPlanFormDataRecord dataRecord = buPlanFormDataRecordMapper.selectById(planFormInstDeleteVO.getFormInstId());
        if (dataRecord.getStatus() == 1) {
            throw new JeecgBootException("删除失败，在线表单(数据记录表)已填写");
        }

        // 删除数据记录表数据值记录
        LambdaQueryWrapper<BuPlanFormValues> valuesWrapper = new LambdaQueryWrapper<BuPlanFormValues>()
                .eq(BuPlanFormValues::getFormDataRecordId, planFormInstDeleteVO.getFormInstId());
        buPlanFormValuesMapper.delete(valuesWrapper);
        // 如果该计划表单只有这一个实例，同时删除该计划表单
        LambdaQueryWrapper<BuPlanFormDataRecord> dataRecordWrapper = new LambdaQueryWrapper<BuPlanFormDataRecord>()
                .eq(BuPlanFormDataRecord::getPlanFormId, dataRecord.getPlanFormId());
        List<BuPlanFormDataRecord> dataRecordList = buPlanFormDataRecordMapper.selectList(dataRecordWrapper);
        if (dataRecordList.size() == 1) {
            buRepairPlanFormsMapper.deleteById(dataRecord.getPlanFormId());
        }
        // 删除数据记录表实例
        buPlanFormDataRecordMapper.deleteById(planFormInstDeleteVO.getFormInstId());
    }

    private void deleteWorkRecordForm(BuRepairPlanFormInstDeleteVO planFormInstDeleteVO) {
        BuPlanFormWorkRecord workRecord = buPlanFormWorkRecordMapper.selectById(planFormInstDeleteVO.getFormInstId());
        if (workRecord.getStatus() == 1) {
            throw new JeecgBootException("删除失败，作业记录表已填写");
        }

        // 删除作业记录表明细
        LambdaQueryWrapper<BuPlanFormWorkRecordDetail> workRecordDetailWrapper = new LambdaQueryWrapper<BuPlanFormWorkRecordDetail>()
                .eq(BuPlanFormWorkRecordDetail::getWorkRecordId, planFormInstDeleteVO.getFormInstId());
        List<BuPlanFormWorkRecordDetail> workRecordDetailList = buPlanFormWorkRecordDetailMapper.selectList(workRecordDetailWrapper);
        if (CollectionUtils.isNotEmpty(workRecordDetailList)) {
            Set<String> workRecordDetailIdSet = workRecordDetailList.stream()
                    .map(BuPlanFormWorkRecordDetail::getId)
                    .collect(Collectors.toSet());
            LambdaQueryWrapper<BuPlanFormWorkRecordChecks> workRecordCheckWrapper = new LambdaQueryWrapper<BuPlanFormWorkRecordChecks>()
                    .in(BuPlanFormWorkRecordChecks::getRecordDetailId, workRecordDetailIdSet);
            buPlanFormWorkRecordChecksMapper.delete(workRecordCheckWrapper);
        }
        buPlanFormWorkRecordDetailMapper.delete(workRecordDetailWrapper);
        // 如果该计划表单只有这一个实例，同时删除该计划表单
        LambdaQueryWrapper<BuPlanFormWorkRecord> workRecordWrapper = new LambdaQueryWrapper<BuPlanFormWorkRecord>()
                .eq(BuPlanFormWorkRecord::getPlanFormId, workRecord.getPlanFormId());
        List<BuPlanFormWorkRecord> workRecordList = buPlanFormWorkRecordMapper.selectList(workRecordWrapper);
        if (workRecordList.size() == 1) {
            buRepairPlanFormsMapper.deleteById(workRecord.getPlanFormId());
        }
        // 删除作业记录表实例
        buPlanFormWorkRecordMapper.deleteById(planFormInstDeleteVO.getFormInstId());
        // 删除作业记录表实例excel结果数据
        buPlWorkRecordExcelDataMapper.deleteById(planFormInstDeleteVO.getFormInstId());
    }

    private void deleteCheckForm(BuRepairPlanFormInstDeleteVO planFormInstDeleteVO) {
        BuPlanFormCheckRecord checkRecord = buPlanFormCheckRecordMapper.selectById(planFormInstDeleteVO.getFormInstId());
        if (checkRecord.getStatus() == 1) {
            throw new JeecgBootException("删除失败，检查记录表已检查");
        }

        // 删除检查记录表明细
        LambdaQueryWrapper<BuPlanFormCheckRecordItem> checkRecordItemWrapper = new LambdaQueryWrapper<BuPlanFormCheckRecordItem>()
                .eq(BuPlanFormCheckRecordItem::getCheckId, checkRecord.getId());
        List<BuPlanFormCheckRecordItem> checkRecordItemList = buPlanFormCheckRecordItemMapper.selectList(checkRecordItemWrapper);
        if (CollectionUtils.isNotEmpty(checkRecordItemList)) {
            Set<String> checkRecordItemIdSet = checkRecordItemList.stream()
                    .map(BuPlanFormCheckRecordItem::getId)
                    .collect(Collectors.toSet());
            LambdaQueryWrapper<BuPlanFormCheckRecordRectify> checkRecordRectifyWrapper = new LambdaQueryWrapper<BuPlanFormCheckRecordRectify>()
                    .in(BuPlanFormCheckRecordRectify::getCheckId, checkRecordItemIdSet);
            buPlanFormCheckRecordRectifyMapper.delete(checkRecordRectifyWrapper);
        }
        buPlanFormCheckRecordItemMapper.delete(checkRecordItemWrapper);
        // 如果该计划表单只有这一个实例，同时删除该计划表单
        LambdaQueryWrapper<BuPlanFormCheckRecord> checkRecordWrapper = new LambdaQueryWrapper<BuPlanFormCheckRecord>()
                .eq(BuPlanFormCheckRecord::getPlanFormId, checkRecord.getPlanFormId());
        List<BuPlanFormCheckRecord> checkRecordList = buPlanFormCheckRecordMapper.selectList(checkRecordWrapper);
        if (checkRecordList.size() == 1) {
            buRepairPlanFormsMapper.deleteById(checkRecord.getPlanFormId());
        }
        // 删除检查记录表实例
        buPlanFormCheckRecordMapper.deleteById(planFormInstDeleteVO.getFormInstId());
    }

}
