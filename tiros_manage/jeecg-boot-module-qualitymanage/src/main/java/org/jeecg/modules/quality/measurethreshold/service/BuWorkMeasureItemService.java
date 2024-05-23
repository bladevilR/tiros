package org.jeecg.modules.quality.measurethreshold.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.quality.measurethreshold.bean.BuWorkMeasureItem;
import org.jeecg.modules.quality.measurethreshold.bean.vo.BuWorkMeasureItemQueryVO;

import java.util.List;

/**
 * <p>
 * 测量项定义 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-23
 */
public interface BuWorkMeasureItemService extends IService<BuWorkMeasureItem> {

    /**
     * 根据表单id获取测量项列表
     *
     * @param formId 表单id
     * @return 测量项列表
     * @throws Exception 异常信息
     */
    List<BuWorkMeasureItem> listMeasureItemByFormId(String formId) throws Exception;

    /**
     * 根据表单id和行号列号获取测量项
     *
     * @param queryVO 表单id和行号列号
     * @return 测量项
     * @throws Exception 异常信息
     */
    BuWorkMeasureItem getMeasureItemByFormIdAndRow(BuWorkMeasureItemQueryVO queryVO) throws Exception;

    /**
     * 保存测量项
     *
     * @param buWorkMeasureItem 测量项
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean saveMeasureItem(BuWorkMeasureItem buWorkMeasureItem) throws Exception;

    /**
     * 修改测量项
     *
     * @param buWorkMeasureItem 测量项
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean updateMeasureItem(BuWorkMeasureItem buWorkMeasureItem) throws Exception;

}
