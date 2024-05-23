package org.jeecg.modules.basemanage.alertaccept.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.alertaccept.entity.BuBaseAlertAccept;
import org.jeecg.modules.basemanage.alertaccept.entity.vo.BuBaseAlertAcceptVO;

import java.util.List;

/**
 * <p>
 * 预警接受信息 Mapper 接口
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-09
 */
public interface BuBaseAlertAcceptMapper extends BaseMapper<BuBaseAlertAccept> {
    /**
     * 查找 BuBaseAlertAccept
     *
     * @param alertAccept
     * @return
     */
    BuBaseAlertAccept selectByAlertAccept(@Param("alertAccept") BuBaseAlertAccept alertAccept);

    List<BuBaseAlertAccept> listAll(@Param("alertAcceptVO") BuBaseAlertAcceptVO alertAcceptVO);

    /**
     * 根据预警类型获取预警接收人username列表
     *
     * @param alertType 预警类型
     * @return 预警接收人username列表
     */
    List<String> selectUsernameListByAlertType(@Param("alertType") Integer alertType);

}
