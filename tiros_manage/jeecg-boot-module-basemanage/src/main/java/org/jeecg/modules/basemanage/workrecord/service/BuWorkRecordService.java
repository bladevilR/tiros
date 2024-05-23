package org.jeecg.modules.basemanage.workrecord.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.workrecord.bean.BuWorkRecord;
import org.jeecg.modules.basemanage.workrecord.bean.vo.BuWorkRecordQueryVO;

/**
 * <p>
 * 作业记录表 服务类
 * </p>
 *
 * @author youGen
 * @since 2020-08-10
 */
public interface BuWorkRecordService extends IService<BuWorkRecord> {

    /**
     * 根据条件分页查询作业记录表
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     * @throws Exception 异常信息
     */
    Page<BuWorkRecord> pageWorkRecord(Page<BuWorkRecord> page, BuWorkRecordQueryVO queryVO) throws Exception;

    /**
     * 根据id查询作业记录表(含明细分类和明细)
     *
     * @param id 作业记录表id
     * @return 作业记录表(含明细分类和明细)
     * @throws Exception 异常信息
     */
    BuWorkRecord getWorkRecordById(String id) throws Exception;

    /**
     * 删除作业记录表（批量）
     *
     * @param ids 作业记录表id,多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean deleteBatch(String ids) throws Exception;

}
