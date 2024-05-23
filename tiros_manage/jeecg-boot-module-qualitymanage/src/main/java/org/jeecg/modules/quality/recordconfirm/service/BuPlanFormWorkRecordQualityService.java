package org.jeecg.modules.quality.recordconfirm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.quality.recordconfirm.bean.BuPlanFormDataRecord;
import org.jeecg.modules.quality.recordconfirm.bean.BuPlanFormWorkRecord;
import org.jeecg.modules.quality.recordconfirm.bean.vo.BuFormInstanceVO;
import org.jeecg.modules.quality.recordconfirm.bean.vo.BuFormRecordQueryVO;
import org.jeecg.modules.quality.recordconfirm.bean.vo.BuFormWorkRecordCheckVO;

/**
 * <p>
 * 作业记录表 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-11
 */
public interface BuPlanFormWorkRecordQualityService extends IService<BuPlanFormWorkRecord> {

//    /**
//     * 根据条件查询作业记录表实列
//     *
//     * @param queryVO 查询条件
//     * @return 作业记录表实列
//     * @throws Exception 异常信息
//     */
//    List<BuPlanFormWorkRecordVO> listFormOrderRecordVO(BuWorkOrderRecordQueryVO queryVO) throws Exception;

    /**
     * 根据条件分页查询表单记录
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常
     */
    IPage<BuFormInstanceVO> pageFormInstanceRecord(BuFormRecordQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据作业记录表实例id查询作业记录实例详情
     *
     * @param recordFormId 作业记录表实例id
     * @return 作业记录实例详情
     * @throws Exception 异常
     */
    BuPlanFormWorkRecord getFormWorkRecordById(String recordFormId) throws Exception;

    /**
     * 根据在线表单(数据表)实例id查询在线表单(数据表)详情
     *
     * @param dataFormId 在线表单(数据表)实例id
     * @return 在线表单(数据表)详情
     * @throws Exception 异常
     */
    BuPlanFormDataRecord getFormDataRecordById(String dataFormId) throws Exception;

    /**
     * 工单作业记录检查确认（质量负责人）
     *
     * @param checkVO 检查确认信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean checkFormWorkRecord(BuFormWorkRecordCheckVO checkVO) throws Exception;

}
