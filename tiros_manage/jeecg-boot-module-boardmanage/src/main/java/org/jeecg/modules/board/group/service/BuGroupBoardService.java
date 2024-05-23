package org.jeecg.modules.board.group.service;

import org.jeecg.modules.board.group.bean.vo.*;
import org.jeecg.modules.board.progress.bean.BuWorkOrder;

import java.util.List;

/**
 * <p>
 * 班组看板 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-09-15
 */
public interface BuGroupBoardService {

    /**
     * 查询班组工单进度
     *
     * @return 班组工单进度
     * @throws Exception 异常
     */
    BuGroupOrderCount getGroupOrderCount() throws Exception;

    /**
     * 查询班组当日工单列表
     *
     * @return 班组当日工单列表
     * @throws Exception 异常
     */
    List<BuWorkOrder> listGroupTodayOrder() throws Exception;

    /**
     * 查询班组人员排名
     *
     * @return 班组人员排名
     * @throws Exception 异常
     */
    List<BuGroupPersonRank> listGroupPersonRank() throws Exception;

    /**
     * 查询班组缺料情况
     *
     * @return 班组缺料情况
     * @throws Exception 异常
     */
    List<BuGroupMaterialLack> listGroupMaterialLack() throws Exception;

    /**
     * 查询班组工器具预警
     *
     * @return 班组工器具预警
     * @throws Exception 异常
     */
    List<BuGroupToolCheckAlert> listGroupToolCheckAlert() throws Exception;

    /**
     * 查询班组排名
     *
     * @return 班组排名
     * @throws Exception 异常
     */
    BuGroupRank getGroupRank() throws Exception;

}
