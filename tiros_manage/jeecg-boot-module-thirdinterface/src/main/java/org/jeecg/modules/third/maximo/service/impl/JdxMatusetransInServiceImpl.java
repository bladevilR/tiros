package org.jeecg.modules.third.maximo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.modules.third.dynamic.DataSource;
import org.jeecg.modules.third.dynamic.DataSourceEnum;
import org.jeecg.modules.third.maximo.bean.JdxMatusetransIn;
import org.jeecg.modules.third.maximo.bean.MxinInterTrans;
import org.jeecg.modules.third.maximo.mapper.JdxMatusetransInMapper;
import org.jeecg.modules.third.maximo.mapper.MxinInterTransMapper;
import org.jeecg.modules.third.maximo.service.JdxMatusetransInService;
import org.jeecg.modules.third.utils.DatabaseBatchSubUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
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
public class JdxMatusetransInServiceImpl extends ServiceImpl<JdxMatusetransInMapper, JdxMatusetransIn> implements JdxMatusetransInService {

    @Resource
    private JdxMatusetransInMapper jdxMatusetransInMapper;
    @Resource
    private MxinInterTransMapper mxinInterTransMapper;


    /**
     * @see JdxMatusetransInService#saveOne(JdxMatusetransIn)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOne(JdxMatusetransIn maximoOrderMaterial) throws Exception {
        if (null == maximoOrderMaterial) {
            return true;
        }

        jdxMatusetransInMapper.insert(maximoOrderMaterial);

        return true;
    }

    /**
     * @see JdxMatusetransInService#saveOneAndInTrans(JdxMatusetransIn, MxinInterTrans)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOneAndInTrans(JdxMatusetransIn maximoOrderMaterial, MxinInterTrans inTrans) throws Exception {
        if (null == maximoOrderMaterial || null == inTrans) {
            return true;
        }

        jdxMatusetransInMapper.insert(maximoOrderMaterial);
        mxinInterTransMapper.insert(inTrans);

        return true;
    }

    /**
     * @see JdxMatusetransInService#saveList(List)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveList(List<JdxMatusetransIn> maximoOrderMaterialList) throws Exception {
        if (CollectionUtils.isEmpty(maximoOrderMaterialList)) {
            return true;
        }

        for (JdxMatusetransIn jdxMatusetransIn : maximoOrderMaterialList) {
            jdxMatusetransInMapper.insert(jdxMatusetransIn);
        }

        return true;
    }

    /**
     * @see JdxMatusetransInService#listMaximoMaterialByReady(String)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<JdxMatusetransIn> listMaximoMaterialByReady(String ready) throws Exception {
        LambdaQueryWrapper<JdxMatusetransIn> wrapper = new LambdaQueryWrapper<JdxMatusetransIn>()
                .eq(JdxMatusetransIn::getReady, ready)
                .orderByAsc(JdxMatusetransIn::getTransid)
                .orderByAsc(JdxMatusetransIn::getTransseq);
        return jdxMatusetransInMapper.selectList(wrapper);
    }

    /**
     * @see JdxMatusetransInService#updateMaximoMaterialReady(List, String)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public boolean updateMaximoMaterialReady(List<JdxMatusetransIn> maximoOrderMaterialList, String ready) throws Exception {
        if (CollectionUtils.isEmpty(maximoOrderMaterialList)) {
            return true;
        }

        JdxMatusetransIn update = new JdxMatusetransIn()
                .setReady(ready);
        List<Long> transIdList = maximoOrderMaterialList.stream()
                .map(JdxMatusetransIn::getTransid)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<List<Long>> transIdBatchSubList = DatabaseBatchSubUtil.batchSubList(transIdList);
        for (List<Long> transIdBatchSub : transIdBatchSubList) {
            LambdaQueryWrapper<JdxMatusetransIn> wrapper = new LambdaQueryWrapper<JdxMatusetransIn>()
                    .in(JdxMatusetransIn::getTransid, transIdBatchSub);
            jdxMatusetransInMapper.update(update, wrapper);
        }

        return true;
    }

}
