package org.jeecg.modules.quality.leaverecord.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecg.modules.quality.leaverecord.bean.BuWorkLeaveRecord;
import org.jeecg.modules.quality.leaverecord.bean.vo.BuWorkLeaveRecordQueryVO;

/**
 * <p>
 * 开口项 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020 -10-21
 */
public interface BuWorkLeaveRecordService extends IService<BuWorkLeaveRecord> {

    /**
     * 分页查询开口项记录
     *
     * @param queryVO  开
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果 page
     * @throws Exception 异常信息
     */
    IPage<BuWorkLeaveRecord> page(BuWorkLeaveRecordQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 保存开口项记录
     *
     * @param buWorkLeaveRecord 开口项记录
     * @return 是否保存成功 boolean
     * @throws Exception 异常信息
     */
    boolean saveWorkLeaveRecord(BuWorkLeaveRecord buWorkLeaveRecord) throws Exception;

    /**
     * 批量删除开口项记录
     *
     * @param ids 开口项记录ids 多个逗号分隔
     * @return 是否删除成功 boolean
     * @throws Exception 异常信息
     */
    boolean deleteBatch(String ids) throws Exception;

    /**
     * Close batch boolean.
     *
     * @param ids the ids
     * @return the boolean
     * @throws Exception the exception
     */
    boolean closeBatch(String ids) throws Exception;

    /**
     * BuWorkLeaveRecord
     *
     * @param queryVO
     * @return boolean
     * @throws Exception the exception
     */
    HSSFWorkbook workLeaveRecordExport(BuWorkLeaveRecordQueryVO queryVO) throws Exception;
}
