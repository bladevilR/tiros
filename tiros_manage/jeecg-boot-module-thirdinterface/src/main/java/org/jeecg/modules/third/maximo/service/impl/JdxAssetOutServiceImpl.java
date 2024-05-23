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
import org.jeecg.modules.third.maximo.bean.JdxAssetOut;
import org.jeecg.modules.third.maximo.mapper.JdxAssetOutMapper;
import org.jeecg.modules.third.maximo.service.JdxAssetOutService;
import org.jeecg.modules.third.utils.DatabaseBatchSubUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-31
 */
@Service
public class JdxAssetOutServiceImpl extends ServiceImpl<JdxAssetOutMapper, JdxAssetOut> implements JdxAssetOutService {

    @Resource
    private JdxAssetOutMapper jdxAssetOutMapper;


    /**
     * @see JdxAssetOutService#countTrainTotal(String)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public int countTrainTotal(String line) {
        LambdaQueryWrapper<JdxAssetOut> wrapper = getTrainWrapper(line);
        return jdxAssetOutMapper.selectCount(wrapper);
    }

    /**
     * @see JdxAssetOutService#pageTrain(Integer, Integer, String)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<JdxAssetOut> pageTrain(Integer pageNo, Integer PageSize, String line) throws Exception {
        if (StringUtils.isBlank(line)) {
            throw new RuntimeException("请指定线路");
        }

        LambdaQueryWrapper<JdxAssetOut> wrapper = getTrainWrapper(line);
        wrapper.orderByAsc(JdxAssetOut::getChangedate)
                .orderByAsc(JdxAssetOut::getTransid)
                .orderByAsc(JdxAssetOut::getTransseq);
        return jdxAssetOutMapper.selectPage(new Page<>(pageNo, PageSize), wrapper);
    }

    /**
     * @see JdxAssetOutService#listAssetOut(String)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<JdxAssetOut> listAssetOut(String line) throws Exception {
        LambdaQueryWrapper<JdxAssetOut> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(line)) {
            wrapper.eq(JdxAssetOut::getSiteid, MaximoThirdConstant.SITE_PREFIX + line);
        }
        wrapper.orderByAsc(JdxAssetOut::getChangedate)
                .orderByAsc(JdxAssetOut::getTransid)
                .orderByAsc(JdxAssetOut::getTransseq);
        return jdxAssetOutMapper.selectList(wrapper);
    }

    /**
     * @see JdxAssetOutService#listAll()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    @DataSource(DataSourceEnum.maximodb)
    public List<JdxAssetOut> listAll() {
        LambdaQueryWrapper<JdxAssetOut> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(JdxAssetOut::getChangedate)
                .orderByAsc(JdxAssetOut::getTransid)
                .orderByAsc(JdxAssetOut::getTransseq);
        return jdxAssetOutMapper.selectList(wrapper);
    }

    /**
     * @see JdxAssetOutService#listByTransIdList(List)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<JdxAssetOut> listByTransIdList(List<Long> transIdList) throws Exception {
        if (CollectionUtils.isEmpty(transIdList)) {
            return new ArrayList<>();
        }
        transIdList.sort(Comparator.comparing(Long::longValue));

        List<JdxAssetOut> resultList = new ArrayList<>();

        List<List<Long>> batchSubList = DatabaseBatchSubUtil.batchSubList(transIdList);
        for (List<Long> batchSub : batchSubList) {
            LambdaQueryWrapper<JdxAssetOut> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(JdxAssetOut::getTransid, batchSub)
                    .orderByAsc(JdxAssetOut::getChangedate)
                    .orderByAsc(JdxAssetOut::getTransid)
                    .orderByAsc(JdxAssetOut::getTransseq);
            List<JdxAssetOut> jdxAssetOutList = jdxAssetOutMapper.selectList(wrapper);
            resultList.addAll(jdxAssetOutList);
        }

        resultList.sort(Comparator.comparing(JdxAssetOut::getChangedate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(JdxAssetOut::getTransid, Comparator.nullsLast(Comparator.naturalOrder())));
        return resultList;
    }


    private LambdaQueryWrapper<JdxAssetOut> getTrainWrapper(String line) {
        LambdaQueryWrapper<JdxAssetOut> wrapper = new LambdaQueryWrapper<JdxAssetOut>()
                .eq(JdxAssetOut::getAssettype, "固定资产")
                .eq(JdxAssetOut::getCSpecialty, "电客车")
                .like(JdxAssetOut::getDescription, "车")
                .like(JdxAssetOut::getDescription, "0");
        if (StringUtils.isNotBlank(line)) {
            wrapper.eq(JdxAssetOut::getSiteid, MaximoThirdConstant.SITE_PREFIX + line);

            // 1、2、3号线的PARENT=01020，4号线的上级=01008
            if (line.equals("1") || line.equals("2") || line.equals("3")) {
                wrapper.eq(JdxAssetOut::getParent, "01020");
            } else if (line.equals("4")) {
                wrapper.eq(JdxAssetOut::getParent, "01008");
            }
        }

        return wrapper;
    }

}
