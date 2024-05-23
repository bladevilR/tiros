package org.jeecg.modules.basemanage.holiday.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.holiday.entity.BuBaseHoliday;

/**
 * <p>
 * 节假日信息表，用于存放节假日信息，包括要上班的特殊日期 服务类
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-06
 */
public interface IBuBaseHolidayService extends IService<BuBaseHoliday> {

    /**
     * 新增节假日
     *
     * @param buBaseHoliday 节假日信息
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean saveHoliday(BuBaseHoliday buBaseHoliday) throws Exception;

    /**
     * 修改节假日
     *
     * @param buBaseHoliday 节假日信息
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean updateHoliday(BuBaseHoliday buBaseHoliday) throws Exception;

    /**
     * 删除节假日（批量）
     *
     * @param ids 节假日id，多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean deleteHolidayBatch(String ids) throws Exception;

    /**
     * 从网络获取新增节日
     *
     * @param result  前端返回的数据
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean saveDayByNetWork(String result) throws Exception;

}
