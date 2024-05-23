package org.jeecg.modules.third.maximo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.third.maximo.bean.JdxMatusetransOut;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 工单物料 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-31
 */
public interface JdxMatusetransOutService extends IService<JdxMatusetransOut> {

    /**
     * 获取工单物料总数
     *
     * @return 工单物料总数
     */
    int countTotal();

    /**
     * 分页获取工单物料
     *
     * @param pageNo   页码
     * @param PageSize 页大小
     * @return 分页结果
     * @throws Exception 异常
     */
    IPage<JdxMatusetransOut> pageOrderMaterial(Integer pageNo, Integer PageSize) throws Exception;

    /**
     * 根据transId列表获取工单物料数据列表
     *
     * @param transIdList        transId列表
     * @return 工单物料数据列表
     * @throws Exception 异常
     */
    List<JdxMatusetransOut> listByTransIdList(List<Long> transIdList) throws Exception;

}
