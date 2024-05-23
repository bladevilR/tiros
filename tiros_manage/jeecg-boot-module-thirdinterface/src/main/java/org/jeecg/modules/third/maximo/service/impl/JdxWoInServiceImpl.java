package org.jeecg.modules.third.maximo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.third.dynamic.DataSource;
import org.jeecg.modules.third.dynamic.DataSourceEnum;
import org.jeecg.modules.third.jdx.bean.BuWorkOrder;
import org.jeecg.modules.third.maximo.bean.JdxWoIn;
import org.jeecg.modules.third.maximo.bean.MxinInterTrans;
import org.jeecg.modules.third.maximo.mapper.JdxWoInMapper;
import org.jeecg.modules.third.maximo.mapper.MxinInterTransMapper;
import org.jeecg.modules.third.maximo.service.JdxWoInService;
import org.jeecg.modules.third.utils.DatabaseBatchSubUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-31
 */
@Service
public class JdxWoInServiceImpl extends ServiceImpl<JdxWoInMapper, JdxWoIn> implements JdxWoInService {

    @Resource
    private JdxWoInMapper jdxWoInMapper;
    @Resource
    private MxinInterTransMapper mxinInterTransMapper;


    /**
     * @see JdxWoInService#saveOne(JdxWoIn)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOne(JdxWoIn maximoOrder) throws Exception {
        if (null == maximoOrder) {
            return true;
        }

        jdxWoInMapper.insert(maximoOrder);

        return true;
    }

    /**
     * @see JdxWoInService#saveOneAndInTrans(JdxWoIn, MxinInterTrans)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOneAndInTrans(JdxWoIn maximoOrder, MxinInterTrans inTrans) throws Exception {
        if (null == maximoOrder || null == inTrans) {
            return true;
        }

        jdxWoInMapper.insert(maximoOrder);
        mxinInterTransMapper.insert(inTrans);

        return true;
    }

    /**
     * @see JdxWoInService#saveList(List)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveList(List<JdxWoIn> maximoOrderList) throws Exception {
        if (CollectionUtils.isEmpty(maximoOrderList)) {
            return true;
        }

        for (JdxWoIn jdxWoIn : maximoOrderList) {
            jdxWoInMapper.insert(jdxWoIn);
        }

        return true;
    }

    /**
     * @see JdxWoInService#checkVerifiedOrderExistAndNotClosed(String)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public boolean checkVerifiedOrderExistAndNotClosed(String orderCode) throws Exception {
        if (StringUtils.isBlank(orderCode)) {
            return false;
        }

        LambdaQueryWrapper<JdxWoIn> verifiedOrderWrapper = new LambdaQueryWrapper<JdxWoIn>()
                .eq(JdxWoIn::getWonum, orderCode);
        List<JdxWoIn> jdxWoInList = jdxWoInMapper.selectList(verifiedOrderWrapper);
        if (CollectionUtils.isEmpty(jdxWoInList)) {
            return false;
        }

        boolean verifiedOrderExist = false;
        boolean closedOrderExist = false;
        for (JdxWoIn jdxWoIn : jdxWoInList) {
            if ("APPR".equals(jdxWoIn.getStatus())) {
                verifiedOrderExist = true;
            }
            if ("CLOSE".equals(jdxWoIn.getStatus())) {
                closedOrderExist = true;
            }
        }

        return verifiedOrderExist && !closedOrderExist;
    }

    /**
     * @see JdxWoInService#getOrderMaximoWorkOrderId(List)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public boolean getOrderMaximoWorkOrderId(List<BuWorkOrder> orderList) throws Exception {
        if (CollectionUtils.isEmpty(orderList)) {
            return true;
        }

        Map<String, Long> orderCodeMaximoWorkOrderIdMap = new HashMap<>();
        List<String> orderCodeList = orderList.stream()
                .map(BuWorkOrder::getOrderCode)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        List<List<String>> orderCodeBatchSubList = DatabaseBatchSubUtil.batchSubList(orderCodeList);
        for (List<String> orderCodeBatchSub : orderCodeBatchSubList) {
            LambdaQueryWrapper<JdxWoIn> wrapper = new LambdaQueryWrapper<JdxWoIn>()
                    .in(JdxWoIn::getWonum, orderCodeBatchSub);
            List<JdxWoIn> maximoOrderList = jdxWoInMapper.selectList(wrapper);
            for (JdxWoIn maximoOrder : maximoOrderList) {
                orderCodeMaximoWorkOrderIdMap.put(maximoOrder.getWonum(), maximoOrder.getWorkorderid());
            }
        }

        for (BuWorkOrder order : orderList) {
            if (orderCodeMaximoWorkOrderIdMap.containsKey(order.getOrderCode())) {
                Long maximoWorkOrderId = orderCodeMaximoWorkOrderIdMap.get(order.getOrderCode());
                order.setMaximoWorkOrderId(maximoWorkOrderId);
            }
        }

        return true;
    }

    /**
     * @see JdxWoInService#listOrderCode()
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<String> listOrderCode() throws Exception {
        List<JdxWoIn> maximoOrderList = jdxWoInMapper.selectList(Wrappers.emptyWrapper());
        return maximoOrderList.stream()
                .map(JdxWoIn::getWonum)
                .distinct()
                .sorted(String::compareTo)
                .collect(Collectors.toList());
    }

}
