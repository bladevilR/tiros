package org.jeecg.modules.third.maximo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.third.dynamic.DataSource;
import org.jeecg.modules.third.dynamic.DataSourceEnum;
import org.jeecg.modules.third.maximo.bean.MxoutInterTrans;
import org.jeecg.modules.third.maximo.bean.MxoutInterTransBak;
import org.jeecg.modules.third.maximo.mapper.MxoutInterTransBakMapper;
import org.jeecg.modules.third.maximo.mapper.MxoutInterTransMapper;
import org.jeecg.modules.third.maximo.service.MxoutInterTransService;
import org.jeecg.modules.third.utils.DatabaseBatchSubUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * maximo输出队列服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-31
 */
@Service
public class MxoutInterTransServiceImpl extends ServiceImpl<MxoutInterTransMapper, MxoutInterTrans> implements MxoutInterTransService {

    @Resource
    private MxoutInterTransMapper mxoutInterTransMapper;
    @Resource
    private MxoutInterTransBakMapper mxoutInterTransBakMapper;


    /**
     * @see MxoutInterTransService#listOutTransQueueByTransIdList(List)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<MxoutInterTransBak> listOutTransQueueByTransIdList(List<Long> transIdList) throws Exception {
        if (CollectionUtils.isEmpty(transIdList)) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<MxoutInterTrans> wrapper = new LambdaQueryWrapper<MxoutInterTrans>()
                .in(MxoutInterTrans::getTransid, transIdList);
        List<MxoutInterTrans> outTransList = mxoutInterTransMapper.selectList(wrapper);

        List<MxoutInterTransBak> outTransBakList = new ArrayList<>();
        for (MxoutInterTrans outTrans : outTransList) {
            MxoutInterTransBak outTransBak = new MxoutInterTransBak();
            BeanUtils.copyProperties(outTrans, outTransBak);
            outTransBakList.add(outTransBak);
        }
        return outTransBakList;
    }

    /**
     * @see MxoutInterTransService#listOutTransQueueByIFaceName(String)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<MxoutInterTransBak> listOutTransQueueByIFaceName(String iFaceName) throws Exception {
        if (StringUtils.isBlank(iFaceName)) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<MxoutInterTransBak> wrapper = new LambdaQueryWrapper<MxoutInterTransBak>()
                .eq(MxoutInterTransBak::getIfacename, iFaceName)
                .orderByAsc(MxoutInterTransBak::getTransid);
        return mxoutInterTransBakMapper.selectList(wrapper);
    }

    /**
     * @see MxoutInterTransService#deleteOutTransQueue(List)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteOutTransQueue(List<MxoutInterTransBak> outTransBakList) throws Exception {
        if (CollectionUtils.isEmpty(outTransBakList)) {
            return true;
        }

        List<Long> transIdList = outTransBakList.stream()
                .map(MxoutInterTransBak::getTransid)
                .collect(Collectors.toList());
        List<List<Long>> transIdBatchSubList = DatabaseBatchSubUtil.batchSubList(transIdList);
        for (List<Long> transIdBatchSub : transIdBatchSubList) {
            LambdaQueryWrapper<MxoutInterTransBak> transBakWrapper = new LambdaQueryWrapper<MxoutInterTransBak>()
                    .in(MxoutInterTransBak::getTransid, transIdBatchSub);
            mxoutInterTransBakMapper.delete(transBakWrapper);
        }

        return true;
    }

//    /**
//     * @see MxoutInterTransService#listTransQueueByMinTransId(String, Long)
//     */
//    @DataSource(DataSourceEnum.maximodb)
//    @Transactional(readOnly = true, rollbackFor = Exception.class)
//    @Override
//    public List<MxoutInterTrans> listTransQueueByMinTransId(String iFaceName, Long minTransId) throws Exception {
//        LambdaQueryWrapper<MxoutInterTrans> wrapper = new LambdaQueryWrapper<MxoutInterTrans>()
//                .eq(MxoutInterTrans::getIfacename, iFaceName)
//                .gt(MxoutInterTrans::getTransid, minTransId)
//                .orderByAsc(MxoutInterTrans::getTransid);
//        return mxoutInterTransMapper.selectList(wrapper);
//    }

}
