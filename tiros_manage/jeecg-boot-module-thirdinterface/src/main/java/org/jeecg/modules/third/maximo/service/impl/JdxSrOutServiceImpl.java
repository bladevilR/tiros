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
import org.jeecg.modules.third.maximo.bean.JdxSrOut;
import org.jeecg.modules.third.maximo.bean.MxoutInterTrans;
import org.jeecg.modules.third.maximo.bean.MxoutInterTransBak;
import org.jeecg.modules.third.maximo.mapper.JdxSrOutMapper;
import org.jeecg.modules.third.maximo.mapper.MxoutInterTransMapper;
import org.jeecg.modules.third.maximo.service.JdxSrOutService;
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
public class JdxSrOutServiceImpl extends ServiceImpl<JdxSrOutMapper, JdxSrOut> implements JdxSrOutService {

    @Resource
    private JdxSrOutMapper jdxSrOutMapper;
    @Resource
    private MxoutInterTransMapper mxoutInterTransMapper;


    /**
     * @see JdxSrOutService#countTotal()
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public int countTotal() {
        return jdxSrOutMapper.selectCount(Wrappers.emptyWrapper());
    }

    /**
     * @see JdxSrOutService#pageFault(Integer, Integer)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<JdxSrOut> pageFault(Integer pageNo, Integer PageSize) throws Exception {
        LambdaQueryWrapper<JdxSrOut> wrapper = new LambdaQueryWrapper<JdxSrOut>()
                .orderByAsc(JdxSrOut::getChangedate)
                .orderByAsc(JdxSrOut::getTransid)
                .orderByAsc(JdxSrOut::getTransseq);
        return jdxSrOutMapper.selectPage(new Page<>(pageNo, PageSize), wrapper);
    }

    /**
     * @see JdxSrOutService#listByOutTransList(List)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<JdxSrOut> listByOutTransList(List<MxoutInterTransBak> outTransBakList) throws Exception {
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

        List<JdxSrOut> resultList = new ArrayList<>();
        List<List<Long>> batchSubList = DatabaseBatchSubUtil.batchSubList(transIdList);
        for (List<Long> batchSub : batchSubList) {
            LambdaQueryWrapper<JdxSrOut> wrapper = new LambdaQueryWrapper<JdxSrOut>()
                    .in(JdxSrOut::getTransid, batchSub)
                    .orderByAsc(JdxSrOut::getChangedate)
                    .orderByAsc(JdxSrOut::getTransid)
                    .orderByAsc(JdxSrOut::getTransseq);
            List<JdxSrOut> subMaximoFaultList = jdxSrOutMapper.selectList(wrapper);
            resultList.addAll(subMaximoFaultList);
        }
        resultList.sort(Comparator.comparing(JdxSrOut::getChangedate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(JdxSrOut::getTransid, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(JdxSrOut::getTransseq, Comparator.nullsLast(Comparator.naturalOrder())));

        // 根据transId设置action
        setTransActionByTransId(resultList, transIdActionMap);

        return resultList;
    }


    private void setTransActionByTransId(List<JdxSrOut> maximoFaultList, Map<Long, String> transIdActionMap) {
        if (CollectionUtils.isEmpty(maximoFaultList)) {
            return;
        }

        if (null == transIdActionMap) {
            transIdActionMap = new HashMap<>();
        }
        if (transIdActionMap.isEmpty()) {
            List<Long> transIdList = maximoFaultList.stream()
                    .map(JdxSrOut::getTransid)
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

        for (JdxSrOut maximoFault : maximoFaultList) {
            String action = transIdActionMap.get(maximoFault.getTransid());
            if (StringUtils.isBlank(action)) {
                action = "Add";
            }
            maximoFault.setTransAction(action);
        }
    }

}
