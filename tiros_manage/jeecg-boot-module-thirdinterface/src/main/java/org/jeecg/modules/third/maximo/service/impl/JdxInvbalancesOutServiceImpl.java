package org.jeecg.modules.third.maximo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.third.dynamic.DataSource;
import org.jeecg.modules.third.dynamic.DataSourceEnum;
import org.jeecg.modules.third.jdx.bean.bo.PriceZero;
import org.jeecg.modules.third.maximo.bean.JdxInvbalancesOut;
import org.jeecg.modules.third.maximo.bean.MxoutInterTrans;
import org.jeecg.modules.third.maximo.bean.MxoutInterTransBak;
import org.jeecg.modules.third.maximo.mapper.JdxInvbalancesOutMapper;
import org.jeecg.modules.third.maximo.mapper.MxoutInterTransMapper;
import org.jeecg.modules.third.maximo.service.JdxInvbalancesOutService;
import org.jeecg.modules.third.utils.DatabaseBatchSubUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
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
public class JdxInvbalancesOutServiceImpl extends ServiceImpl<JdxInvbalancesOutMapper, JdxInvbalancesOut> implements JdxInvbalancesOutService {

    @Resource
    private JdxInvbalancesOutMapper jdxInvbalancesOutMapper;
    @Resource
    private MxoutInterTransMapper mxoutInterTransMapper;


    /**
     * @see JdxInvbalancesOutService#getAllWarehouse()
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public Map<String, List<String>> getAllWarehouse() throws Exception {
        List<JdxInvbalancesOut> warehouseList = jdxInvbalancesOutMapper.selectWarehouseList();

        if (CollectionUtils.isEmpty(warehouseList)) {
            return new HashMap<>();
        }

        Map<String, List<String>> resultMap = new HashMap<>();
        for (JdxInvbalancesOut warehouse : warehouseList) {
            String location = warehouse.getLocation();
            String binnum = warehouse.getBinnum();

            if (resultMap.containsKey(location)) {
                List<String> binnumList = resultMap.get(location);
                if (StringUtils.isNotBlank(binnum) && !binnumList.contains(binnum)) {
                    binnumList.add(binnum);
                }
            } else {
                List<String> binnumList = new ArrayList<>();
                if (StringUtils.isNotBlank(binnum)) {
                    binnumList.add(binnum);
                }
                resultMap.put(location, binnumList);
            }
        }

        return resultMap;
    }

    /**
     * @see JdxInvbalancesOutService#countTotal()
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public int countTotal() {
//        LambdaQueryWrapper<JdxInvbalancesOut> wrapper = getJdxMaterialWrapper();
        LambdaQueryWrapper<JdxInvbalancesOut> wrapper = new LambdaQueryWrapper<>();
        return jdxInvbalancesOutMapper.selectCount(wrapper);
    }

    /**
     * @see JdxInvbalancesOutService#pageMaterial(Integer, Integer)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<JdxInvbalancesOut> pageMaterial(Integer pageNo, Integer PageSize) throws Exception {
        IPage<JdxInvbalancesOut> maximoStockPage = jdxInvbalancesOutMapper.pageJdxInvbalancesOut(new Page<>(pageNo, PageSize));
        if (CollectionUtils.isNotEmpty(maximoStockPage.getRecords())) {
            List<JdxInvbalancesOut> maximoStockList = maximoStockPage.getRecords();
            // 根据transId设置action
            setTransActionByTransId(maximoStockList, null);
        }

        return maximoStockPage;
    }

    /**
     * @see JdxInvbalancesOutService#listByOutTransList(List)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<JdxInvbalancesOut> listByOutTransList(List<MxoutInterTransBak> outTransBakList) throws Exception {
        if (CollectionUtils.isEmpty(outTransBakList)) {
            return new ArrayList<>();
        }

        List<Long> transIdList = new ArrayList<>();
        Map<Long, String> transIdActionMap = new HashMap<>();
        for (MxoutInterTransBak outTransBak : outTransBakList) {
            transIdList.add(outTransBak.getTransid());
            transIdActionMap.put(outTransBak.getTransid(), outTransBak.getAction());
        }
        transIdList.sort(Comparator.comparing(Long::longValue));

        List<JdxInvbalancesOut> resultList = new ArrayList<>();
        List<List<Long>> transIdBatchSubList = DatabaseBatchSubUtil.batchSubList(transIdList);
        for (List<Long> transIdBatchSub : transIdBatchSubList) {
//            // 直接查出上次库存：有问题
//            List<JdxInvbalancesOut> subMaximoStockList = jdxInvbalancesOutMapper.selectListByTransIdList(transIdBatchSub);
//            resultList.addAll(subMaximoStockList);
            // 不直接查出上次库存
            LambdaQueryWrapper<JdxInvbalancesOut> wrapper = new LambdaQueryWrapper<JdxInvbalancesOut>()
                    .in(JdxInvbalancesOut::getTransid, transIdBatchSub)
                    .orderByAsc(JdxInvbalancesOut::getInsertdate)
                    .orderByAsc(JdxInvbalancesOut::getTransid)
                    .orderByAsc(JdxInvbalancesOut::getTransseq);
            List<JdxInvbalancesOut> subMaximoStockList = jdxInvbalancesOutMapper.selectList(wrapper);
            resultList.addAll(subMaximoStockList);
        }
        resultList.sort(Comparator.comparing(JdxInvbalancesOut::getInsertdate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(JdxInvbalancesOut::getTransid, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(JdxInvbalancesOut::getTransseq, Comparator.nullsLast(Comparator.naturalOrder())));

        // 根据transId设置action
        setTransActionByTransId(resultList, transIdActionMap);
        // 设置库存变化
//        // 直接查出上次库存：有问题
//        setStockChangeByLastStock(resultList);
        // 不直接查出上次库存
        setStockChange(resultList);

        return resultList;
    }

    /**
     * @see JdxInvbalancesOutService#listMaterialStockByLocation(String)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<JdxInvbalancesOut> listMaterialStockByLocation(String location) throws Exception {
        if (StringUtils.isBlank(location)) {
            throw new RuntimeException("请输入指定库房");
        }

        // 查询本库房的所有库存
        LambdaQueryWrapper<JdxInvbalancesOut> wrapper = new LambdaQueryWrapper<JdxInvbalancesOut>()
                .eq(JdxInvbalancesOut::getLocation, location)
                .orderByAsc(JdxInvbalancesOut::getInsertdate)
                .orderByAsc(JdxInvbalancesOut::getTransid)
                .orderByAsc(JdxInvbalancesOut::getTransseq);
        List<JdxInvbalancesOut> resultList = jdxInvbalancesOutMapper.selectList(wrapper);
        resultList.sort(Comparator.comparing(JdxInvbalancesOut::getInsertdate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(JdxInvbalancesOut::getTransid, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(JdxInvbalancesOut::getTransseq, Comparator.nullsLast(Comparator.naturalOrder())));

        // action都为Add，库存变化都为0
        for (JdxInvbalancesOut result : resultList) {
            result.setTransAction("Add")
                    .setStockChange(0D);
        }

        return resultList;
    }

    /**
     * @see JdxInvbalancesOutService#getSiteByLocation(String)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public String getSiteByLocation(String location) throws Exception {
        if (StringUtils.isBlank(location)) {
            return null;
        }

        return jdxInvbalancesOutMapper.selectSiteByLocation(location);
    }

    /**
     * @see JdxInvbalancesOutService#selectPriceOfPriceZeroList(List)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<PriceZero> selectPriceOfPriceZeroList(List<PriceZero> priceZeroList) throws Exception {
        for (PriceZero priceZero : priceZeroList) {
            List<JdxInvbalancesOut> invbalancesOutList = jdxInvbalancesOutMapper.selectPrice(priceZero);
            if (CollectionUtils.isNotEmpty(invbalancesOutList)) {
                Double cAvgcost = invbalancesOutList.get(0).getCAvgcost();
                priceZero.setCurrentMaximoPrice(BigDecimal.valueOf(cAvgcost));
            }
        }
        return priceZeroList;
    }


    private void setTransActionByTransId(List<JdxInvbalancesOut> maximoStockList, Map<Long, String> transIdActionMap) {
        if (CollectionUtils.isEmpty(maximoStockList)) {
            return;
        }

        if (null == transIdActionMap) {
            transIdActionMap = new HashMap<>();
        }
        if (transIdActionMap.isEmpty()) {
            List<Long> transIdList = maximoStockList.stream()
                    .map(JdxInvbalancesOut::getTransid)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(transIdList)) {
                List<List<Long>> transIdBatchSubList = DatabaseBatchSubUtil.batchSubList(transIdList);
                for (List<Long> transIdBatchSub : transIdBatchSubList) {
                    LambdaQueryWrapper<MxoutInterTrans> outTransWrapper = new LambdaQueryWrapper<MxoutInterTrans>()
                            .in(MxoutInterTrans::getTransid, transIdBatchSub);
                    List<MxoutInterTrans> subOutTransList = mxoutInterTransMapper.selectList(outTransWrapper);
                    for (MxoutInterTrans outTrans : subOutTransList) {
                        transIdActionMap.put(outTrans.getTransid(), outTrans.getAction());
                    }
                }
            }
        }

        for (JdxInvbalancesOut maximoStock : maximoStockList) {
            String action = transIdActionMap.get(maximoStock.getTransid());
            if (StringUtils.isBlank(action)) {
                action = "Add";
            }
            maximoStock.setTransAction(action);
        }
    }

    private void setStockChangeByLastStock(List<JdxInvbalancesOut> maximoStockList) {
        if (CollectionUtils.isEmpty(maximoStockList)) {
            return;
        }

        for (JdxInvbalancesOut maximoStock : maximoStockList) {
            Double lastStock = null == maximoStock.getLastStock() ? 0D : maximoStock.getLastStock();
            Double currentStock = null == maximoStock.getCurbal() ? 0D : maximoStock.getCurbal();
            maximoStock.setStockChange(currentStock - lastStock);
        }
    }

    private void setStockChange(List<JdxInvbalancesOut> maximoStockList) {
        if (CollectionUtils.isEmpty(maximoStockList)) {
            return;
        }

        // 根据库存id查询库存对应的所有记录
        List<JdxInvbalancesOut> allMaximoStockList = new ArrayList<>();
        List<Integer> maximoStockIdList = maximoStockList.stream()
                .map(JdxInvbalancesOut::getInvbalancesid)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        List<List<Integer>> maximoStockIdBatchSubList = DatabaseBatchSubUtil.batchSubList(maximoStockIdList);
        for (List<Integer> maximoStockIdBatchSub : maximoStockIdBatchSubList) {
            LambdaQueryWrapper<JdxInvbalancesOut> wrapper = new LambdaQueryWrapper<JdxInvbalancesOut>()
                    .in(JdxInvbalancesOut::getInvbalancesid, maximoStockIdBatchSub)
                    .orderByAsc(JdxInvbalancesOut::getInsertdate)
                    .orderByAsc(JdxInvbalancesOut::getTransid)
                    .orderByAsc(JdxInvbalancesOut::getTransseq);
            List<JdxInvbalancesOut> subMaximoStockList = jdxInvbalancesOutMapper.selectList(wrapper);
            allMaximoStockList.addAll(subMaximoStockList);
        }

        for (JdxInvbalancesOut maximoStock : maximoStockList) {
            Integer maximoStockId = maximoStock.getInvbalancesid();
            Long transid = maximoStock.getTransid();

            List<JdxInvbalancesOut> matchMaximoStockList = allMaximoStockList.stream()
                    .filter(stock -> maximoStockId.equals(stock.getInvbalancesid()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(matchMaximoStockList)) {
                matchMaximoStockList.sort(Comparator.comparing(JdxInvbalancesOut::getInsertdate, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(JdxInvbalancesOut::getTransid, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(JdxInvbalancesOut::getTransseq, Comparator.nullsLast(Comparator.naturalOrder())));
                int thisMaximoStockIndex = matchMaximoStockList.size() - 1;
                for (int i = 0; i < matchMaximoStockList.size(); i++) {
                    JdxInvbalancesOut matchMaximoStock = matchMaximoStockList.get(i);
                    if (transid.equals(matchMaximoStock.getTransid())) {
                        thisMaximoStockIndex = i;
                    }
                }
                int lastMaximoStockIndex = thisMaximoStockIndex - 1;
                if (lastMaximoStockIndex >= 0) {
                    JdxInvbalancesOut lastMaximoStock = matchMaximoStockList.get(lastMaximoStockIndex);
                    Double lastStock = null == lastMaximoStock.getCurbal() ? 0D : lastMaximoStock.getCurbal();
                    Double currentStock = null == maximoStock.getCurbal() ? 0D : maximoStock.getCurbal();
                    maximoStock.setStockChange(currentStock - lastStock);
                }
            }

            if (null == maximoStock.getStockChange()) {
                maximoStock.setStockChange(0D);
            }
        }
    }

}
