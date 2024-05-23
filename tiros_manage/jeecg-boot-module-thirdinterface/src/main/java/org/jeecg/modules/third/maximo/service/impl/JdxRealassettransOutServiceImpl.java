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
import org.jeecg.modules.third.maximo.bean.JdxRealassettransOut;
import org.jeecg.modules.third.maximo.bean.MxoutInterTrans;
import org.jeecg.modules.third.maximo.bean.MxoutInterTransBak;
import org.jeecg.modules.third.maximo.mapper.JdxRealassettransOutMapper;
import org.jeecg.modules.third.maximo.mapper.MxoutInterTransMapper;
import org.jeecg.modules.third.maximo.service.JdxRealassettransOutService;
import org.jeecg.modules.third.utils.DatabaseBatchSubUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 换上换下 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-31
 */
@Service
public class JdxRealassettransOutServiceImpl extends ServiceImpl<JdxRealassettransOutMapper, JdxRealassettransOut> implements JdxRealassettransOutService {

    @Resource
    private JdxRealassettransOutMapper jdxRealassettransOutMapper;
    @Resource
    private MxoutInterTransMapper mxoutInterTransMapper;


    /**
     * @see JdxRealassettransOutService#countTotal()
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public int countTotal() {
        return jdxRealassettransOutMapper.selectCount(Wrappers.emptyWrapper());
    }

    /**
     * @see JdxRealassettransOutService#pageChange(Integer, Integer)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<JdxRealassettransOut> pageChange(Integer pageNo, Integer PageSize) throws Exception {
        LambdaQueryWrapper<JdxRealassettransOut> wrapper = new LambdaQueryWrapper<JdxRealassettransOut>()
                .orderByAsc(JdxRealassettransOut::getTransdate)
                .orderByAsc(JdxRealassettransOut::getTransid)
                .orderByAsc(JdxRealassettransOut::getTransseq);
        return jdxRealassettransOutMapper.selectPage(new Page<>(pageNo, PageSize), wrapper);
    }

    /**
     * @see JdxRealassettransOutService#listByOutTransList(List)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<JdxRealassettransOut> listByOutTransList(List<MxoutInterTransBak> outTransBakList) throws Exception {
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

        List<JdxRealassettransOut> resultList = new ArrayList<>();
        List<List<Long>> batchSubList = DatabaseBatchSubUtil.batchSubList(transIdList);
        for (List<Long> batchSub : batchSubList) {
            LambdaQueryWrapper<JdxRealassettransOut> wrapper = new LambdaQueryWrapper<JdxRealassettransOut>()
                    .in(JdxRealassettransOut::getTransid, batchSub)
                    .orderByAsc(JdxRealassettransOut::getTransdate)
                    .orderByAsc(JdxRealassettransOut::getTransid)
                    .orderByAsc(JdxRealassettransOut::getTransseq);
            List<JdxRealassettransOut> subMaximoChangeList = jdxRealassettransOutMapper.selectList(wrapper);
            resultList.addAll(subMaximoChangeList);
        }
        resultList.sort(Comparator.comparing(JdxRealassettransOut::getTransdate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(JdxRealassettransOut::getTransid, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(JdxRealassettransOut::getTransseq, Comparator.nullsLast(Comparator.naturalOrder())));

        // 根据transId设置action
        setTransActionByTransId(resultList, transIdActionMap);

        return resultList;
    }


    private void setTransActionByTransId(List<JdxRealassettransOut> maximoChangeList, Map<Long, String> transIdActionMap) {
        if (CollectionUtils.isEmpty(maximoChangeList)) {
            return;
        }

        if (null == transIdActionMap) {
            transIdActionMap = new HashMap<>();
        }
        if (transIdActionMap.isEmpty()) {
            List<Long> transIdList = maximoChangeList.stream()
                    .map(JdxRealassettransOut::getTransid)
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

        for (JdxRealassettransOut maximoChange : maximoChangeList) {
            String action = transIdActionMap.get(maximoChange.getTransid());
            if (StringUtils.isBlank(action)) {
                action = "Add";
            }
            maximoChange.setTransAction(action);
        }
    }

}
