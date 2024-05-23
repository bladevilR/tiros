package org.jeecg.modules.third.maximo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.third.dynamic.DataSource;
import org.jeecg.modules.third.dynamic.DataSourceEnum;
import org.jeecg.modules.third.maximo.bean.JdxWoOut;
import org.jeecg.modules.third.maximo.bean.MxoutInterTrans;
import org.jeecg.modules.third.maximo.bean.MxoutInterTransBak;
import org.jeecg.modules.third.maximo.mapper.JdxWoOutMapper;
import org.jeecg.modules.third.maximo.mapper.MxoutInterTransMapper;
import org.jeecg.modules.third.maximo.service.JdxWoOutService;
import org.jeecg.modules.third.utils.DatabaseBatchSubUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
public class JdxWoOutServiceImpl extends ServiceImpl<JdxWoOutMapper, JdxWoOut> implements JdxWoOutService {

    @Resource
    private JdxWoOutMapper jdxWoOutMapper;
    @Resource
    private MxoutInterTransMapper mxoutInterTransMapper;


    /**
     * @see JdxWoOutService#countTotal()
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public int countTotal() {
        return jdxWoOutMapper.selectCount(Wrappers.emptyWrapper());
    }

    /**
     * @see JdxWoOutService#pageOrder(Integer, Integer)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<JdxWoOut> pageOrder(Integer pageNo, Integer PageSize) throws Exception {
        LambdaQueryWrapper<JdxWoOut> wrapper = new LambdaQueryWrapper<JdxWoOut>()
                .orderByAsc(JdxWoOut::getChangedate)
                .orderByAsc(JdxWoOut::getTransid)
                .orderByAsc(JdxWoOut::getTransseq);
        return jdxWoOutMapper.selectPage(new Page<>(pageNo, PageSize), wrapper);
    }

    /**
     * @see JdxWoOutService#listByOutTransList(List)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<JdxWoOut> listByOutTransList(List<MxoutInterTransBak> outTransBakList) throws Exception {
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

        List<JdxWoOut> resultList = new ArrayList<>();
        List<List<Long>> batchSubList = DatabaseBatchSubUtil.batchSubList(transIdList);
        for (List<Long> batchSub : batchSubList) {
            LambdaQueryWrapper<JdxWoOut> wrapper = new LambdaQueryWrapper<JdxWoOut>()
                    .in(JdxWoOut::getTransid, batchSub)
                    .orderByAsc(JdxWoOut::getChangedate)
                    .orderByAsc(JdxWoOut::getTransid)
                    .orderByAsc(JdxWoOut::getTransseq);
            List<JdxWoOut> subMaximoOrderList = jdxWoOutMapper.selectList(wrapper);
            resultList.addAll(subMaximoOrderList);
        }
        resultList.sort(Comparator.comparing(JdxWoOut::getChangedate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(JdxWoOut::getTransid, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(JdxWoOut::getTransseq, Comparator.nullsLast(Comparator.naturalOrder())));

        // 根据transId设置action
        setTransActionByTransId(resultList, transIdActionMap);

        return resultList;
    }


    private void setTransActionByTransId(List<JdxWoOut> maximoOrderList, Map<Long, String> transIdActionMap) {
        if (CollectionUtils.isEmpty(maximoOrderList)) {
            return;
        }

        if (null == transIdActionMap) {
            transIdActionMap = new HashMap<>();
        }
        if (transIdActionMap.isEmpty()) {
            List<Long> transIdList = maximoOrderList.stream()
                    .map(JdxWoOut::getTransid)
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

        for (JdxWoOut maximoOrder : maximoOrderList) {
            String action = transIdActionMap.get(maximoOrder.getTransid());
            if (StringUtils.isBlank(action)) {
                action = "Add";
            }
            maximoOrder.setTransAction(action);
        }
    }

}
