package org.jeecg.modules.dispatch.planform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.dispatch.planform.bean.*;
import org.jeecg.modules.dispatch.planform.bean.vo.*;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderTaskFormInst;
import org.jeecg.modules.dispatch.workorder.bean.vo.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 列计划表单，当列计划审批通过后，根据该配置表自动生成相关数据记录 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-17
 */
public interface BuRepairPlanFormsService extends IService<BuRepairPlanForms> {

    /**
     * 根据条件分页查询列计划表单
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 列计划表单分页结果
     * @throws Exception 异常
     */
    IPage<BuRepairPlanForms> pageForms(BuRepairPlanFormsQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据条件查询列计划表单列表
     *
     * @param queryVO 查询条件
     * @return 列计划表单列表
     * @throws Exception 异常
     */
    List<BuRepairPlanForms> listForms(BuRepairPlanFormsQueryVO queryVO) throws Exception;

    /**
     * 根据id查询列计划表单
     *
     * @param id 列计划表单id
     * @return 列计划表单
     * @throws Exception 异常
     */
    BuRepairPlanForms getFormById(String id) throws Exception;

    /**
     * 更新列计划表单
     *
     * @param buRepairPlanForms 列计划表单
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean updateFormById(BuRepairPlanForms buRepairPlanForms) throws Exception;

    /**
     * 修改表单实例的名称和设备
     *
     * @param planFormEditVO 修改参数
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean editTitleOrAsset(BuRepairPlanFormEditVO planFormEditVO) throws Exception;

    /**
     * 删除列计划表单(批量)
     *
     * @param ids 列计划表单ids，多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean deleteBatch(String ids) throws Exception;

    /**
     * 根据列计划id生成列计划的表单实列
     *
     * @param planId 列计划id
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean generateFormsRelationDataByPlanId(String planId) throws Exception;

    /**
     * 根据工单id查询工单作业记录表列表
     *
     * @param orderId 工单id
     * @return 工单作业记录表列表
     * @throws Exception 异常
     */
    List<BuWorkOrderTaskFormInst> listOrderWorkRecord(String orderId) throws Exception;

    /**
     * 根据列计划记录表(在线表单(数据记录表))id查询列计划记录表(在线表单(数据记录表))
     *
     * @param queryVO 列计划记录表(在线表单(数据记录表))id,是否需要数据值记录
     * @return 列计划记录表(在线表单 ( 数据记录表))
     * @throws Exception 异常
     */
    BuPlanFormDataRecord getFormDataRecordById(BuPlanOnlineFormQueryVO queryVO) throws Exception;

    /**
     * 填写在线表单(数据记录表)结果
     *
     * @param buPlanFormDataRecord 在线表单(数据记录表)结果
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean saveFormDataRecordResult(BuPlanFormDataRecord buPlanFormDataRecord) throws Exception;

    /**
     * 根据数据表单(实例)id查询该实例的预警记录
     *
     * @param dataRecordId 数据表单实例id
     * @return 预警记录
     * @throws Exception 异常
     */
    List<BuPlanFormValues> listAlertRecordByDataRecordFormId(String dataRecordId) throws Exception;

    /**
     * 根据作业记录(实例)id查询列计划作业记录信息
     *
     * @param queryVO 列计划作业记录id,是否需要检查信息,工单任务id(过滤记录明细)
     * @return 列计划作业记录信息
     * @throws Exception 异常
     */
    BuPlanFormWorkRecord getFormWorkRecordById(BuPlanWorkRecordFormQueryVO queryVO) throws Exception;

    /**
     * 作业记录明细检查
     *
     * @param checkVO 信息
     * @return 是否成功
     * @throws Exception 异常
     */
    Result<Map<String, String>> checkFormWorkRecordDetail(BuWorkOrderRecordCheckVO checkVO) throws Exception;

    /**
     * 保存作业记录(实例)的原部件序列号和新部件序列号
     *
     * @param id        作业记录(实例)id
     * @param manufNo   原部件序列号
     * @param manufNoUp 新部件序列号
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean saveFormWorkRecordManufNo(String id, String manufNo, String manufNoUp) throws Exception;

    /**
     * 作业记录明细扫码检查
     *
     * @param checkVO 信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean scanCheckFormWorkRecordDetail(CheckCommonVO checkVO, String checkUserId) throws Exception;

    /**
     * 作业记录明细扫码检查
     *
     * @param checkVO 信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean scanCheckFormWorkRecordDetail(BuWorkOrderRecordScanTaskCheckVO checkVO) throws Exception;

    /**
     * 根据检查记录(实例)id查询检查记录(实例)信息
     *
     * @param queryVO 检查记录(实例)id,是否需要检查项信息
     * @return 检查记录(实例)信息
     * @throws Exception 异常
     */
    BuPlanFormCheckRecord getFormCheckRecordById(BuPlanCheckRecordFormQueryVO queryVO) throws Exception;

    /**
     * 根据检查记录(实例)id查询检查记录(实例)检查项列表
     *
     * @param queryVO 检查记录(实例)id
     * @return 检查记录(实例)检查项列表
     * @throws Exception 异常
     */
    List<BuPlanFormCheckRecordItem> listCheckRecordItemByCheckRecordId(BuPlanCheckRecordFormQueryVO queryVO) throws Exception;

    /**
     * 设置检查记录表(实例)检查项明细（多个）的作业时间
     *
     * @param itemIds  检查记录表(实例)检查项明细ids，多个逗号分隔
     * @param workTime 作业时间
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean setCheckRecordItemWorkTime(String itemIds, Date workTime) throws Exception;

    /**
     * 检查记录表（实例）检查项检查
     *
     * @param checkVO 检查信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean checkCheckRecordItem(FormCheckRecordCheckVO checkVO) throws Exception;

    /**
     * 删除作业记录表检查结果
     *
     * @param delWorkRecordCheckVO 删除项
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean deleteBatchCheckResult(DelWorkRecordCheckVO delWorkRecordCheckVO) throws Exception;

    /**
     * 保存作业记录表excel结果
     *
     * @param excelDataVO
     * @return
     */
    boolean saveFormWorkRecordExcelData(FromExcelDataVO excelDataVO) throws Exception;

    /**
     * 获取作业记录表excel结果
     *
     * @param id
     * @return
     */
    String getFormWorkRecordExcelData(String id);

    /**
     * 根据表单id获取datajson
     *
     * @param id 表单id
     * @return 表单datajson
     */
    String selectFormTemplateDataJsonByFormTemplateId(String id);

    /**
     * 查询列计划表单实例
     *
     * @param queryVO 查询条件
     * @return 列计划表单实例列表
     * @throws Exception 异常
     */
    List<PlanFormInstance> listPlanFormInst(BuRepairPlanFormsInstQueryVO queryVO) throws Exception;

    /**
     * 添加列计划表单实例
     *
     * @param planForm 列计划表单信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean addPlanFormInst(BuRepairPlanForms planForm) throws Exception;

    /**
     * 批量删除列计划表单实例
     *
     * @param formInstDeleteVOList 删除信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean deleteBatchPlanFormInst(List<BuRepairPlanFormInstDeleteVO> formInstDeleteVOList) throws Exception;

    /**
     * 重新生成列计划表单实例
     *
     * @param planFormId  列计划表单id
     * @param oldFormType 旧的表单类型
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean regeneratePlanFormInst(String planFormId, Integer oldFormType) throws Exception;

}
