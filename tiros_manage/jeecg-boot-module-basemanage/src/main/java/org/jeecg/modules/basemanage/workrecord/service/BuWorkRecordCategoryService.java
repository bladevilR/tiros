package org.jeecg.modules.basemanage.workrecord.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.workrecord.bean.BuWorkRecord;
import org.jeecg.modules.basemanage.workrecord.bean.BuWorkRecordCategory;

import java.util.List;

/**
 * <p>
 * 作业记录明细分项 服务类
 * </p>
 *
 * @author youGen
 * @since 2020-09-08
 */
public interface BuWorkRecordCategoryService extends IService<BuWorkRecordCategory> {

    /**
     * 根据作业记录表id查询分项
     *
     * @param workRecordId 作业记录表id
     * @return 作业记录明细分项
     * @throws Exception 异常信息
     */
    List<BuWorkRecordCategory> listByWorkRecordId(String workRecordId) throws Exception;

    /**
     * 删除作业记录表明细分项（批量）
     *
     * @param ids 作业记录表明细分项id,多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean deleteBatch(String ids) throws Exception;

}
