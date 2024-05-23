package org.jeecg.modules.dispatch.exchange.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.dispatch.exchange.bean.BuRepairExchang;
import org.jeecg.modules.dispatch.exchange.bean.vo.BuRepairExchangeQueryVO;
import org.jeecg.modules.dispatch.exchange.bean.vo.BuRepairExchangeRemarkSaveVO;

import java.util.List;

/**
 * <p>
 * 交接车记录 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020 -09-24
 */
public interface BuRepairExchangService extends IService<BuRepairExchang> {

    /**
     * 分页查询交接车记录
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常
     */
    IPage<BuRepairExchang> page(BuRepairExchangeQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据id查询交接车记录
     *
     * @param id 交接车记录id
     * @return 交接车记录
     * @throws Exception 异常
     */
    BuRepairExchang getRepairExchangeById(String id) throws Exception;

    /**
     * 根据id查询交接车记录
     *
     * @param id 交接车记录id
     * @return 交接车记录
     * @throws Exception 异常
     */
    BuRepairExchang getRepairExchangeWithRelationById(String id) throws Exception;

    /**
     * 查询可交车的接车记录
     *
     * @return 已维修且未关联交车记录的接车记录
     * @throws Exception 异常
     */
    List<BuRepairExchang> listDeliverable() throws Exception;

    /**
     * 新增交接车记录
     *
     * @param buRepairExchang 交接车记录信息
     * @return 是否新增成功
     * @throws Exception 异常
     */
    boolean saveRepairExchange(BuRepairExchang buRepairExchang) throws Exception;

    /**
     * 修改交接车记录
     *
     * @param buRepairExchang 交接车记录信息
     * @return 是否修改成功
     * @throws Exception 异常
     */
    boolean updateRepairExchange(BuRepairExchang buRepairExchang) throws Exception;

    /**
     * 修改交接车记录的备注
     *
     * @param remarkSaveVO id+备注
     * @return 是否修改成功
     * @throws Exception 异常
     */
    boolean updateRemarkById(BuRepairExchangeRemarkSaveVO remarkSaveVO) throws Exception;

    /**
     * 批量删除交接车记录
     *
     * @param ids 交接车记录ids 多个逗号分隔
     * @return 是否删除成功
     * @throws Exception 异常
     */
    boolean deleteBatch(String ids) throws Exception;

    /**
     * 交接车确认
     * 设置当前登录人员和部门到接受部门和人员
     *
     * @param buRepairExchang 交接车信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean confirmExchange(BuRepairExchang buRepairExchang) throws Exception;

    /**
     * 根据接车id更新架修周期记录表
     *
     * @param exchangeId 接车id
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean updateRepairPeriodByExchangeId(String exchangeId) throws Exception;

    /**
     * 根据trainNo查询交接车记录
     *
     * @param trainNo 车号
     * @return 交接车记录
     * @throws Exception 异常
     */
    BuRepairExchang getRepairedReceiveExchangeByTrainNo(String trainNo) throws Exception;

    /**
     * Gets train number.
     *
     * @return the train number
     */
    Integer getTrainNumber() throws Exception;

    /**
     * Gets item no.
     *
     * @param trainNo   the train no
     * @param programId the program id
     * @return the item no
     */
    Integer getItemNo(String programId) throws Exception;

    Boolean validationTrainNumber(String trainNo, Integer trainNumber);

}
