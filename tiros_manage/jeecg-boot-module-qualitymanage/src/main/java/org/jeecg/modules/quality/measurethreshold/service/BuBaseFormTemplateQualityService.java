package org.jeecg.modules.quality.measurethreshold.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.quality.measurethreshold.bean.BuBaseFormTemplate;
import org.jeecg.modules.quality.measurethreshold.bean.vo.BuBaseFormTemplateQueryVO;

/**
 * <p>
 * 数据记录表模版信息 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-21
 */
public interface BuBaseFormTemplateQualityService extends IService<BuBaseFormTemplate> {

    /**
     * 根据条件分页查询数据记录表单
     *
     * @param queryVO 查询条件
     * @return 数据记录表单列表
     * @throws Exception 异常
     */
    IPage<BuBaseFormTemplate> pageDataRecordForm(BuBaseFormTemplateQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

}
