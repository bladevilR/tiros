package org.jeecg.modules.dispatch.exchange.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.common.workflow.bean.WfBizStatus;
import org.jeecg.modules.dispatch.exchange.bean.BuBaseTrainRepairPeriod;
import org.jeecg.modules.dispatch.exchange.bean.BuRepairExchang;
import org.jeecg.modules.dispatch.exchange.bean.BuRepairExchangLeave;
import org.jeecg.modules.dispatch.exchange.bean.BuRepairExchangRectify;
import org.jeecg.modules.dispatch.exchange.bean.vo.BuRepairExchangeQueryVO;
import org.jeecg.modules.dispatch.exchange.bean.vo.BuRepairExchangeRemarkSaveVO;
import org.jeecg.modules.dispatch.exchange.mapper.BuBaseTrainRepairPeriodMapper;
import org.jeecg.modules.dispatch.exchange.mapper.BuRepairExchangLeaveMapper;
import org.jeecg.modules.dispatch.exchange.mapper.BuRepairExchangMapper;
import org.jeecg.modules.dispatch.exchange.mapper.BuRepairExchangRectifyMapper;
import org.jeecg.modules.dispatch.exchange.service.BuRepairExchangService;
import org.jeecg.modules.dispatch.plan.bean.BuRepairPlanYearDetail;
import org.jeecg.modules.dispatch.plan.mapper.BuRepairPlanYearDetailMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 * 交接车记录 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-24
 */
@Slf4j
@Service
public class BuRepairExchangServiceImpl extends ServiceImpl<BuRepairExchangMapper, BuRepairExchang> implements BuRepairExchangService {

    @Resource
    private BuRepairExchangMapper buRepairExchangMapper;
    @Resource
    private BuRepairExchangLeaveMapper buRepairExchangLeaveMapper;
    @Resource
    private BuRepairExchangRectifyMapper buRepairExchangRectifyMapper;
    @Resource
    private BuBaseTrainRepairPeriodMapper buBaseTrainRepairPeriodMapper;
    @Resource
    private BuRepairPlanYearDetailMapper buRepairPlanYearDetailMapper;


    /**
     * @see BuRepairExchangService#page(BuRepairExchangeQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuRepairExchang> page(BuRepairExchangeQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buRepairExchangMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuRepairExchangService#getRepairExchangeById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuRepairExchang getRepairExchangeById(String id) throws Exception {
        return buRepairExchangMapper.selectRepairExchangeById(id);
    }

    /**
     * @see BuRepairExchangService#getRepairExchangeWithRelationById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuRepairExchang getRepairExchangeWithRelationById(String id) throws Exception {
        BuRepairExchang exchange = buRepairExchangMapper.selectById(id);

        // 查询开口项和整改项
        LambdaQueryWrapper<BuRepairExchangLeave> leaveWrapper = new LambdaQueryWrapper<BuRepairExchangLeave>()
                .eq(BuRepairExchangLeave::getExchangeId, id);
        List<BuRepairExchangLeave> leaveList = buRepairExchangLeaveMapper.selectList(leaveWrapper);
        LambdaQueryWrapper<BuRepairExchangRectify> rectifyWrapper = new LambdaQueryWrapper<BuRepairExchangRectify>()
                .eq(BuRepairExchangRectify::getExchangeId, id);
        List<BuRepairExchangRectify> rectifyList = buRepairExchangRectifyMapper.selectList(rectifyWrapper);

        return exchange
                .setLeaveList(leaveList)
                .setRectifyList(rectifyList);
    }

    /**
     * @see BuRepairExchangService#listDeliverable()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuRepairExchang> listDeliverable() throws Exception {
        return buRepairExchangMapper.selectReceiveListDeliverable();
    }

    /**
     * @see BuRepairExchangService#saveRepairExchange(BuRepairExchang)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveRepairExchange(BuRepairExchang exchange) throws Exception {
        // 设置年计划明细id
        getAndSetPlanYearDetailId(exchange);
        buRepairExchangMapper.insert(exchange);

        // 插入关联信息
        insertRelations(exchange);

        return true;
    }

    /**
     * @see BuRepairExchangService#updateRepairExchange(BuRepairExchang)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateRepairExchange(BuRepairExchang exchange) throws Exception {
        // 设置年计划明细id
        getAndSetPlanYearDetailId(exchange);
        buRepairExchangMapper.updateById(exchange);

        // 删除关联信息
        deleteRelations(Collections.singletonList(exchange.getId()));
        // 插入关联信息
        insertRelations(exchange);

        return true;
    }

    /**
     * @see BuRepairExchangService#updateRemarkById(BuRepairExchangeRemarkSaveVO)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateRemarkById(BuRepairExchangeRemarkSaveVO remarkSaveVO) throws Exception {
        BuRepairExchang update = new BuRepairExchang()
                .setId(remarkSaveVO.getId())
                .setRemark(remarkSaveVO.getRemark());
        buRepairExchangMapper.updateById(update);

        return true;
    }

    /**
     * @see BuRepairExchangService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));
        if (CollectionUtils.isNotEmpty(idList)) {
            LambdaQueryWrapper<WfBizStatus> wfWrapper = new LambdaQueryWrapper<>();
            wfWrapper.in(WfBizStatus::getBusinessKey, idList);
            new WfBizStatus().delete(wfWrapper);

            // 删除关联信息
            deleteRelations(idList);

            buRepairExchangMapper.deleteBatchIds(idList);
        }


        return true;
    }

    /**
     * @see BuRepairExchangService#confirmExchange(BuRepairExchang)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean confirmExchange(BuRepairExchang buRepairExchang) throws Exception {
        // 获取登录用户信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();
        String orgCode = sysUser.getOrgCode();
        String departId = null;

        List<Map<String, Object>> userDepartList = buRepairExchangMapper.getUserDepartList(userId);
        if (CollectionUtils.isNotEmpty(userDepartList)) {
            Map<String, Object> matchItemMap = userDepartList.get(0);

            if (StringUtils.isNotBlank(orgCode)) {
                for (Map<String, Object> itemMap : userDepartList) {
                    if (orgCode.equals(itemMap.get("orgCode"))) {
                        matchItemMap = itemMap;
                        break;
                    }
                }
            }

            departId = (String) matchItemMap.get("departId");
        }

        buRepairExchang.setAcceptDeptId(departId)
                .setAcceptUserId(userId)
                .setAcceptDate(new Date());
        buRepairExchangMapper.updateById(buRepairExchang);

        return true;
    }

    /**
     * @see BuRepairExchangService#updateRepairPeriodByExchangeId(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateRepairPeriodByExchangeId(String exchangeId) throws Exception {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        int year = calendar.get(Calendar.YEAR);

        BuRepairExchang exchange = buRepairExchangMapper.selectRepairExchangeById(exchangeId);
        if (null != exchange) {
            String trainNo = exchange.getTrainNo();

            LambdaQueryWrapper<BuBaseTrainRepairPeriod> periodWrapper = new LambdaQueryWrapper<BuBaseTrainRepairPeriod>()
                    .eq(BuBaseTrainRepairPeriod::getTrainNo, trainNo);
            List<BuBaseTrainRepairPeriod> periodList = buBaseTrainRepairPeriodMapper.selectList(periodWrapper);

            // 如果已存在同样的车辆了，则填写该记录的结束日期，并根据结束日期计算质保日期
            Integer services = exchange.getProgramServices();
            if (null == services) {
                services = 365;
            }
            calendar.add(Calendar.DATE, services);
            Date lifeDate = calendar.getTime();
            if (CollectionUtils.isNotEmpty(periodList)) {
                for (BuBaseTrainRepairPeriod period : periodList) {
                    if (null == period.getEnd()) {
                        period.setEnd(now)
                                .setLifeDate(lifeDate);
                        buBaseTrainRepairPeriodMapper.updateById(period);
                    }
                }
            }

            // 创建一条新的记录
            BuBaseTrainRepairPeriod period = new BuBaseTrainRepairPeriod()
                    .setTrainId(exchange.getTrainId())
                    .setTrainNo(exchange.getTrainNo())
                    .setStartTime(now)
                    .setLifeDate(lifeDate)
                    .setYear(year)
                    .setRepairIndex(exchange.getTrainIndex())
                    .setRepairProgram(exchange.getProgramId())
                    .setCompanyId(exchange.getCompanyId())
                    .setWorkshopId(exchange.getWorkshopId())
                    .setLineId(exchange.getLineId());
            buBaseTrainRepairPeriodMapper.insert(period);
        }

        return true;
    }

    /**
     * @see BuRepairExchangService#getRepairedReceiveExchangeByTrainNo(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuRepairExchang getRepairedReceiveExchangeByTrainNo(String trainNo) throws Exception {
        return buRepairExchangMapper.selectRepairedReceiveExchangeByTrainNo(trainNo);
    }

    @Override
    public Integer getTrainNumber() throws Exception {
        Integer trainNumber = buRepairExchangMapper.getTrainNumber();
        return trainNumber != null ? trainNumber + 1 : 1;
    }

    @Override
    public Integer getItemNo(String programId) throws Exception {
        Integer itemNo = buRepairExchangMapper.getItemNo(programId);
        return itemNo != null ? itemNo + 1 : 1;
    }

    @Override
    public Boolean validationTrainNumber(String trainNo, Integer trainNumber) {
        List<Integer> trainIndexList = buRepairExchangMapper.selectListTrainIndex(trainNo);
        if (CollectionUtils.isNotEmpty(trainIndexList)) {
            return trainIndexList.stream().anyMatch((e) -> e == trainNumber);
        }
        return false;
    }


    private void getAndSetPlanYearDetailId(BuRepairExchang exchange) {
        if (StringUtils.isNotBlank(exchange.getYearDetailId())) {
            // 如果前端设置了年计划明细id，不需处理
            return;
        }

        Integer tradeType = exchange.getTradeType();
        if (0 == tradeType) {
            // 通过接车记录的线路line_id、接修序号train_index、接车时间accept_date所在年份找对应的年计划明细
            int exchangeYear = DateUtils.getYear(exchange.getAcceptDate());
            List<BuRepairPlanYearDetail> planYearDetailList = buRepairPlanYearDetailMapper.selectListByLineAndTrainIndexAndYear(exchange.getLineId(), exchange.getTrainIndex(), exchangeYear);
            if (CollectionUtils.isNotEmpty(planYearDetailList)) {
                exchange.setYearDetailId(planYearDetailList.get(0).getId());
            }
        } else {
            String receiveId = exchange.getReceiveId();
            BuRepairExchang receiveExchange = buRepairExchangMapper.selectById(receiveId);
            exchange.setYearDetailId(receiveExchange.getYearDetailId());
        }
    }

    private void deleteRelations(List<String> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return;
        }

        LambdaQueryWrapper<BuRepairExchangLeave> leaveWrapper = new LambdaQueryWrapper<BuRepairExchangLeave>()
                .in(BuRepairExchangLeave::getExchangeId, idList);
        buRepairExchangLeaveMapper.delete(leaveWrapper);
        LambdaQueryWrapper<BuRepairExchangRectify> rectifyWrapper = new LambdaQueryWrapper<BuRepairExchangRectify>()
                .in(BuRepairExchangRectify::getExchangeId, idList);
        buRepairExchangRectifyMapper.delete(rectifyWrapper);
    }


    private void insertRelations(BuRepairExchang buRepairExchang) {
        if (null == buRepairExchang) {
            return;
        }

        String exchangeId = buRepairExchang.getId();

        List<BuRepairExchangLeave> leaveList = buRepairExchang.getLeaveList();
        if (CollectionUtils.isNotEmpty(leaveList)) {
            for (BuRepairExchangLeave leave : leaveList) {
                leave.setExchangeId(exchangeId);
                leave.setId(UUIDGenerator.generate());
                buRepairExchangLeaveMapper.insert(leave);
            }
        }
        List<BuRepairExchangRectify> rectifyList = buRepairExchang.getRectifyList();
        if (CollectionUtils.isNotEmpty(rectifyList)) {
            for (BuRepairExchangRectify rectify : rectifyList) {
                rectify.setExchangeId(exchangeId);
                rectify.setId(UUIDGenerator.generate());
                buRepairExchangRectifyMapper.insert(rectify);
            }
        }
    }

}
