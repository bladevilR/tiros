package org.jeecg.modules.basemanage.productionnotice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.productionnotice.entity.BuProductionNotice;
import org.jeecg.modules.basemanage.productionnotice.entity.vo.BuProductionNoticeProgressDetailVO;
import org.jeecg.modules.basemanage.productionnotice.entity.vo.BuProductionNoticeQueryVO;

import java.util.List;

public interface IBuProductionNoticeService extends IService<BuProductionNotice> {

    IPage<BuProductionNotice> queryPage(BuProductionNoticeQueryVO queryVO, Integer pageNo, Integer pageSize);

    boolean saveNotice(BuProductionNotice notice);

    boolean updateNotice(BuProductionNotice notice);

    boolean deleteNotice(String ids);

    boolean submitNotice(String id);

    boolean publishNotice(String id);

    boolean closeNotice(String id);

    List<BuProductionNotice> listPendingTechnicalNotices(String lineId, String trainNo);

    List<BuProductionNoticeProgressDetailVO> listProgressDetails(String id);

    void bindOrder(String noticeId, String orderId, String orderCode, String trainNo);

    void refreshProgressByOrder(String orderId, String trainNo);

    void unbindOrder(String orderId);
}
