package org.jeecg.modules.third.maximo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.third.dynamic.DataSource;
import org.jeecg.modules.third.dynamic.DataSourceEnum;
import org.jeecg.modules.third.maximo.bean.*;
import org.jeecg.modules.third.maximo.bean.JdxProjectOut;
import org.jeecg.modules.third.maximo.mapper.JdxProjectOutMapper;
import org.jeecg.modules.third.maximo.mapper.MxoutInterTransMapper;
import org.jeecg.modules.third.maximo.service.JdxProjectOutService;
import org.jeecg.modules.third.maximo.service.JdxProjectOutService;
import org.jeecg.modules.third.utils.DatabaseBatchSubUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 财务项目 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-20
 */
@Service
public class JdxProjectOutServiceImpl extends ServiceImpl<JdxProjectOutMapper, JdxProjectOut> implements JdxProjectOutService {

    @Resource
    private JdxProjectOutMapper jdxProjectOutMapper;
    @Resource
    private MxoutInterTransMapper mxoutInterTransMapper;


    /**
     * @see JdxProjectOutService#listAll()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    @DataSource(DataSourceEnum.maximodb)
    public List<JdxProjectOut> listAll() {
        LambdaQueryWrapper<JdxProjectOut> wrapper = new LambdaQueryWrapper<JdxProjectOut>()
                .orderByAsc(JdxProjectOut::getTransid)
                .orderByAsc(JdxProjectOut::getTransseq);
        List<JdxProjectOut> projectList = jdxProjectOutMapper.selectList(wrapper);

        if (CollectionUtils.isNotEmpty(projectList)) {
            // 根据transId设置action
            setTransActionByTransId(projectList);
        }

        return projectList;
    }

    /**
     * @see JdxProjectOutService#listByOutTransList(List)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<JdxProjectOut> listByOutTransList(List<MxoutInterTransBak> outTransBakList) throws Exception {
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


        List<JdxProjectOut> resultList = new ArrayList<>();
        List<List<Long>> batchSubList = DatabaseBatchSubUtil.batchSubList(transIdList);
        for (List<Long> batchSub : batchSubList) {
            LambdaQueryWrapper<JdxProjectOut> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(JdxProjectOut::getTransid, batchSub)
                    .orderByAsc(JdxProjectOut::getTransid)
                    .orderByAsc(JdxProjectOut::getTransseq);
            List<JdxProjectOut> JdxProjectOutList = jdxProjectOutMapper.selectList(wrapper);
            resultList.addAll(JdxProjectOutList);
        }

        resultList.sort(Comparator.comparing(JdxProjectOut::getTransid, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(JdxProjectOut::getTransseq, Comparator.nullsLast(Comparator.naturalOrder())));
        for (JdxProjectOut project : resultList) {
            String action = transIdActionMap.get(project.getTransid());
            if (StringUtils.isBlank(action)) {
                action = "Add";
            }
            project.setTransAction(action);
        }

        return resultList;
    }

    private void setTransActionByTransId(List<JdxProjectOut> projectList) {
        Map<Long, String> transIdActionMap = new HashMap<>();
        List<Long> transIdList = projectList.stream()
                .map(JdxProjectOut::getTransid)
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

        for (JdxProjectOut project : projectList) {
            String action = transIdActionMap.get(project.getTransid());
            if (StringUtils.isBlank(action)) {
                action = "Add";
            }
            project.setTransAction(action);
        }
    }

}
