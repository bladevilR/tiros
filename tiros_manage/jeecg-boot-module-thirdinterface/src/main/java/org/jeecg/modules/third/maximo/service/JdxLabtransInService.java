package org.jeecg.modules.third.maximo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.maximo.bean.JdxLabtransIn;
import org.jeecg.modules.third.maximo.bean.MxinInterTrans;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-31
 */
public interface JdxLabtransInService extends IService<JdxLabtransIn> {

    /**
     * 单个插入
     *
     * @param maximoWorkTime 数据
     * @throws Exception 异常
     */
    boolean saveOne(JdxLabtransIn maximoWorkTime) throws Exception;

    /**
     * 单个插入
     *
     * @param maximoWorkTime 数据
     * @param inTrans        队列
     * @throws Exception 异常
     */
    boolean saveOneAndInTrans(JdxLabtransIn maximoWorkTime, MxinInterTrans inTrans) throws Exception;

    /**
     * 批量插入
     *
     * @param maximoWorkTimeList 数据列表
     * @throws Exception 异常
     */
    boolean saveList(List<JdxLabtransIn> maximoWorkTimeList) throws Exception;

}
