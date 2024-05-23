package org.jeecg.modules.basemanage.workrecord.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.workrecord.bean.BuWorkRecordDetail;
import org.jeecg.modules.basemanage.workrecord.bean.vo.BuWorkRecordDetailQueryVO;

import java.util.List;

/**
 * <p>
 * 作业记录表明细, 一条规程项关联一条或多条作业记录明细，如果是多条表示对这条规程项的拆分 服务类
 * </p>
 *
 * @author youGen
 * @since 2020-08-10
 */
public interface BuWorkRecordDetailService extends IService<BuWorkRecordDetail> {

    /**
     * 根据条件查询作业记录表明细列表
     *
     * @param queryVO 查询条件
     * @return 作业记录表明细列表
     * @throws Exception 异常信息
     */
    List<BuWorkRecordDetail> listWorkRecordDetail(BuWorkRecordDetailQueryVO queryVO) throws Exception;

}
