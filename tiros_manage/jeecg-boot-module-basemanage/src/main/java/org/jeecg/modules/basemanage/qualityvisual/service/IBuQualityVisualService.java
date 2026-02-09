package org.jeecg.modules.basemanage.qualityvisual.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.qualityvisual.entity.BuQualityVisual;

import java.util.List;

public interface IBuQualityVisualService extends IService<BuQualityVisual> {
    IPage<BuQualityVisual> queryPage(BuQualityVisual query, Integer pageNo, Integer pageSize);
    boolean saveRecord(BuQualityVisual record);
    boolean updateRecord(BuQualityVisual record);
    boolean deleteRecord(String ids);
    BuQualityVisual refreshByPlanAndTrain(String planId, String trainNo, String trainType, String projectName);
    List<BuQualityVisual> batchRefreshByPlan(String planId);
}
