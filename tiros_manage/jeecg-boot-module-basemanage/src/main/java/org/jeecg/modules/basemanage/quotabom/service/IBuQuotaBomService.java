package org.jeecg.modules.basemanage.quotabom.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.quotabom.entity.BuQuotaBom;
import org.jeecg.modules.basemanage.quotabom.entity.vo.BuQuotaBomTreeNodeVO;

import java.util.List;

public interface IBuQuotaBomService extends IService<BuQuotaBom> {
    IPage<BuQuotaBom> queryPage(BuQuotaBom query, Integer pageNo, Integer pageSize);
    boolean saveRecord(BuQuotaBom record);
    boolean updateRecord(BuQuotaBom record);
    boolean deleteRecord(String ids);
    List<BuQuotaBomTreeNodeVO> listTree(String trainType);
}
