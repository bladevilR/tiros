package org.jeecg.modules.quality.recordconfirm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.quality.recordconfirm.bean.BuPlanFormCheckRecord;
import org.jeecg.modules.quality.recordconfirm.bean.vo.BuFormCheckRecordVO;
import org.jeecg.modules.quality.recordconfirm.bean.vo.BuFormRecordQueryVO;


/**
 * <p>
 * 作业检查记录表（实例） 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-18
 */
public interface BuPlanFormCheckRecordQualityService extends IService<BuPlanFormCheckRecord> {

    /**
     * 根据条件分页查询检查表单记录
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常
     */
    IPage<BuFormCheckRecordVO> pageFormCheckRecord(BuFormRecordQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

}
