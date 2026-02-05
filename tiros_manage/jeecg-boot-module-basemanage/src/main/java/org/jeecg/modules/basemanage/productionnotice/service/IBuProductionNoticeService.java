package org.jeecg.modules.basemanage.productionnotice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.productionnotice.entity.BuProductionNotice;
import org.jeecg.modules.basemanage.productionnotice.entity.vo.BuProductionNoticeQueryVO;

public interface IBuProductionNoticeService extends IService<BuProductionNotice> {

    IPage<BuProductionNotice> queryPage(BuProductionNoticeQueryVO queryVO, Integer pageNo, Integer pageSize);

    boolean saveNotice(BuProductionNotice notice);

    boolean updateNotice(BuProductionNotice notice);

    boolean deleteNotice(String ids);
}
