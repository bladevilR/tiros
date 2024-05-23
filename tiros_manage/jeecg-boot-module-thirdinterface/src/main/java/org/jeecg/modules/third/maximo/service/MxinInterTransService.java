package org.jeecg.modules.third.maximo.service;

import com.baomidou.mybatisplus.extension.service.IService;
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
public interface MxinInterTransService extends IService<MxinInterTrans> {

    /**
     * 获取最大的输入（架大修写入）transId
     *
     * @return 最大的输入（架大修写入）transId
     * @throws Exception 异常
     */
    Long getInTransId() throws Exception;

    /**
     * 单个插入
     *
     * @param inTrans 数据
     * @throws Exception 异常
     */
    boolean saveOne(MxinInterTrans inTrans) throws Exception;

    /**
     * 批量插入
     *
     * @param inTransList 数据列表
     * @throws Exception 异常
     */
    boolean saveList(List<MxinInterTrans> inTransList) throws Exception;

}
