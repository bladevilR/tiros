package org.jeecg.modules.basemanage.alertaccept.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.alertaccept.entity.BuBaseAlertAccept;
import org.jeecg.modules.basemanage.alertaccept.entity.vo.BuBaseAlertAcceptVO;

import java.util.List;

/**
 * <p>
 * 预警信息 服务类
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-09
 */
public interface IBuBaseAlertAcceptService extends IService<BuBaseAlertAccept> {
    /**
     * 判断对象是否存在
     *
     * @param alertAccept
     * @return
     */
    List<String> isExit(BuBaseAlertAccept alertAccept);

    /**
     * 查询所有
     *
     * @param buBaseAlertAcceptVO
     * @return
     */
    List<BuBaseAlertAccept> listAll(BuBaseAlertAcceptVO buBaseAlertAcceptVO);

    /**
     * 删除接收对象
     * @param delVO
     */
    // void deleteTarget(BuBaseAlertAcceptDelVO delVO);

    /**
     * 新增多个
     *
     * @param alertAccept
     */
    void saveList(BuBaseAlertAccept alertAccept);
}
