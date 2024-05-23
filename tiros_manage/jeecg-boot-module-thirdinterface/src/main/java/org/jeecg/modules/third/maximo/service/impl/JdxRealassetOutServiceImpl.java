package org.jeecg.modules.third.maximo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.third.constant.MaximoThirdConstant;
import org.jeecg.modules.third.dynamic.DataSource;
import org.jeecg.modules.third.dynamic.DataSourceEnum;
import org.jeecg.modules.third.maximo.bean.JdxRealassetOut;
import org.jeecg.modules.third.maximo.bean.MxoutInterTrans;
import org.jeecg.modules.third.maximo.bean.MxoutInterTransBak;
import org.jeecg.modules.third.maximo.mapper.JdxRealassetOutMapper;
import org.jeecg.modules.third.maximo.mapper.MxoutInterTransMapper;
import org.jeecg.modules.third.maximo.service.JdxRealassetOutService;
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
 * @since 2021-04-01
 */
@Service
public class JdxRealassetOutServiceImpl extends ServiceImpl<JdxRealassetOutMapper, JdxRealassetOut> implements JdxRealassetOutService {

    @Resource
    private JdxRealassetOutMapper jdxRealassetOutMapper;
    @Resource
    private MxoutInterTransMapper mxoutInterTransMapper;


    /**
     * @see JdxRealassetOutService#countTotal()
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public int countTotal() {
        LambdaQueryWrapper<JdxRealassetOut> jdxWorkshopWrapper = getJdxWorkshopWrapper();
        jdxWorkshopWrapper.orderByAsc(JdxRealassetOut::getTransid)
                .orderByAsc(JdxRealassetOut::getTransseq);
        return jdxRealassetOutMapper.selectCount(jdxWorkshopWrapper);
    }

    /**
     * @see JdxRealassetOutService#pageTool(Integer, Integer)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<JdxRealassetOut> pageTool(Integer pageNo, Integer PageSize) throws Exception {
        LambdaQueryWrapper<JdxRealassetOut> jdxWorkshopWrapper = getJdxWorkshopWrapper();
        jdxWorkshopWrapper.orderByAsc(JdxRealassetOut::getTransid)
                .orderByAsc(JdxRealassetOut::getTransseq);
        IPage<JdxRealassetOut> toolPage = jdxRealassetOutMapper.selectPage(new Page<>(pageNo, PageSize), jdxWorkshopWrapper);
        if (CollectionUtils.isNotEmpty(toolPage.getRecords())) {
            // 根据transId设置action
            setTransActionByTransId(toolPage.getRecords());
        }

        return toolPage;
    }

    /**
     * @see JdxRealassetOutService#listByOutTransList(List)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<JdxRealassetOut> listByOutTransList(List<MxoutInterTransBak> outTransBakList) throws Exception {
        if (CollectionUtils.isEmpty(outTransBakList)) {
            return new ArrayList<>();
        }

        List<Long> transIdList = new ArrayList<>();
        Map<Long, String> transIdActionMap = new HashMap<>();
        for (MxoutInterTransBak outTrans : outTransBakList) {
            transIdList.add(outTrans.getTransid());
            transIdActionMap.put(outTrans.getTransid(), outTrans.getAction());
        }
        transIdList.sort(Comparator.comparing(Long::longValue));

        List<JdxRealassetOut> resultList = new ArrayList<>();
        List<List<Long>> batchSubList = DatabaseBatchSubUtil.batchSubList(transIdList);
        for (List<Long> batchSub : batchSubList) {
            LambdaQueryWrapper<JdxRealassetOut> wrapper = getJdxWorkshopWrapper();
            wrapper.in(JdxRealassetOut::getTransid, batchSub)
                    .orderByAsc(JdxRealassetOut::getTransid)
                    .orderByAsc(JdxRealassetOut::getTransseq);
            List<JdxRealassetOut> jdxRealassetOutList = jdxRealassetOutMapper.selectList(wrapper);
            resultList.addAll(jdxRealassetOutList);
        }

        resultList.sort(Comparator.comparing(JdxRealassetOut::getTransid, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(JdxRealassetOut::getTransseq, Comparator.nullsLast(Comparator.naturalOrder())));
        for (JdxRealassetOut tool : resultList) {
            String action = transIdActionMap.get(tool.getTransid());
            if (StringUtils.isBlank(action)) {
                action = "Add";
            }
            tool.setTransAction(action);
        }

        return resultList;
    }


    private LambdaQueryWrapper<JdxRealassetOut> getJdxWorkshopWrapper() {
        List<String> workshopCodeList = Arrays.asList(MaximoThirdConstant.JDX_WORKSHOP_1, MaximoThirdConstant.JDX_WORKSHOP_2);
        return new LambdaQueryWrapper<JdxRealassetOut>()
                .in(JdxRealassetOut::getWorkshop, workshopCodeList);
    }

    private void setTransActionByTransId(List<JdxRealassetOut> toolList) {
        Map<Long, String> transIdActionMap = new HashMap<>();
        List<Long> transIdList = toolList.stream()
                .map(JdxRealassetOut::getTransid)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(transIdList)) {
            List<List<Long>> transIdBatchSubList = DatabaseBatchSubUtil.batchSubList(transIdList);
            for (List<Long> transIdBatchSub : transIdBatchSubList) {
                LambdaQueryWrapper<MxoutInterTrans> outTransWrapper = new LambdaQueryWrapper<MxoutInterTrans>()
                        .in(MxoutInterTrans::getTransid, transIdBatchSub);
                List<MxoutInterTrans> subOutTransList = mxoutInterTransMapper.selectList(outTransWrapper);
                subOutTransList.forEach(outTrans -> transIdActionMap.put(outTrans.getTransid(), outTrans.getAction()));
            }
        }

        for (JdxRealassetOut tool : toolList) {
            String action = transIdActionMap.get(tool.getTransid());
            if (StringUtils.isBlank(action)) {
                action = "Add";
            }
            tool.setTransAction(action);
        }
    }

}
