package org.jeecg.modules.third.maximo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.maximo.bean.JdxMatusetransIn;
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
public interface JdxMatusetransInService extends IService<JdxMatusetransIn> {

    /**
     * 单个插入
     *
     * @param maximoOrderMaterial 数据
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean saveOne(JdxMatusetransIn maximoOrderMaterial) throws Exception;

    /**
     * 单个插入
     *
     * @param maximoOrderMaterial 数据
     * @param inTrans             队列
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean saveOneAndInTrans(JdxMatusetransIn maximoOrderMaterial, MxinInterTrans inTrans) throws Exception;

    /**
     * 批量插入
     *
     * @param maximoOrderMaterialList 数据列表
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean saveList(List<JdxMatusetransIn> maximoOrderMaterialList) throws Exception;

    /**
     * 根据处理标志获取工单物料数据列表
     *
     * @param ready 处理标志
     * @return 工单物料数据列表
     * @throws Exception 异常
     */
    List<JdxMatusetransIn> listMaximoMaterialByReady(String ready) throws Exception;

    /**
     * 更新工单物料数据的处理标志
     *
     * @param maximoOrderMaterialList 工单物料数据列表
     * @param ready                   处理标志
     * @return 是否成功
     */
    boolean updateMaximoMaterialReady(List<JdxMatusetransIn> maximoOrderMaterialList, String ready) throws Exception;

}
