package org.jeecg.modules.basemanage.standardprocess.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.standardprocess.entity.BuStandardProcess;

import java.util.List;

public interface IBuStandardProcessService extends IService<BuStandardProcess> {
    IPage<BuStandardProcess> queryPage(BuStandardProcess query, Integer pageNo, Integer pageSize);
    boolean saveRecord(BuStandardProcess record);
    boolean updateRecord(BuStandardProcess record);
    boolean deleteRecord(String ids);
    byte[] exportPdf(List<String> ids);
}
