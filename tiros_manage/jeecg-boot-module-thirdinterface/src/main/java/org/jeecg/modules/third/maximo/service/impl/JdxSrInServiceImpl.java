package org.jeecg.modules.third.maximo.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.third.common.bean.bo.FaultCompareBO;
import org.jeecg.modules.third.dynamic.DataSource;
import org.jeecg.modules.third.dynamic.DataSourceEnum;
import org.jeecg.modules.third.maximo.bean.JdxSrIn;
import org.jeecg.modules.third.maximo.bean.MxinInterTrans;
import org.jeecg.modules.third.maximo.mapper.JdxSrInMapper;
import org.jeecg.modules.third.maximo.mapper.MxinInterTransMapper;
import org.jeecg.modules.third.maximo.service.JdxSrInService;
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
public class JdxSrInServiceImpl extends ServiceImpl<JdxSrInMapper, JdxSrIn> implements JdxSrInService {

    @Resource
    private JdxSrInMapper jdxSrInMapper;
    @Resource
    private MxinInterTransMapper mxinInterTransMapper;


    /**
     * @see JdxSrInService#saveOne(JdxSrIn)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOne(JdxSrIn maximoFault) throws Exception {
        if (null == maximoFault) {
            return true;
        }

        jdxSrInMapper.insert(maximoFault);

        return true;
    }

    /**
     * @see JdxSrInService#saveOneAndInTrans(JdxSrIn, MxinInterTrans)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOneAndInTrans(JdxSrIn maximoFault, MxinInterTrans inTrans) throws Exception {
        if (null == maximoFault || null == inTrans) {
            return true;
        }

        jdxSrInMapper.insert(maximoFault);
        mxinInterTransMapper.insert(inTrans);

        return true;
    }

    /**
     * @see JdxSrInService#saveList(List)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveList(List<JdxSrIn> maximoFaultList) throws Exception {
        if (CollectionUtils.isEmpty(maximoFaultList)) {
            return true;
        }

        for (JdxSrIn jdxSrIn : maximoFaultList) {
            jdxSrInMapper.insert(jdxSrIn);
        }

        return true;
    }

    /**
     * @see JdxSrInService#listFaultSn()
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<String> listFaultSn() throws Exception {
        List<JdxSrIn> maximoFaultList = jdxSrInMapper.selectList(Wrappers.emptyWrapper());
        return maximoFaultList.stream()
                .map(JdxSrIn::getTicketid)
                .distinct()
                .sorted(String::compareTo)
                .collect(Collectors.toList());
    }

    /**
     * @see JdxSrInService#mapFaultSnStatus()
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public Map<String, FaultCompareBO> mapFaultSnStatus() {
        List<JdxSrIn> maximoFaultList = jdxSrInMapper.selectList(Wrappers.emptyWrapper());

        Map<String, FaultCompareBO> faultSnBOMap = new HashMap<>();
        for (JdxSrIn maximoFault : maximoFaultList) {
            String ticketid = maximoFault.getTicketid();
            String status = maximoFault.getStatus();
            String statusText = getFaultStatusText(status);

            FaultCompareBO faultCompareBO = faultSnBOMap.get(ticketid);
            if (null == faultCompareBO) {
                faultCompareBO = new FaultCompareBO()
                        .setMaximoFaultSn(ticketid)
                        .setMaximoFaultStatus(status)
                        .setMaximoFaultStatusText(statusText);
            } else {
                if ("CLOSED".equals(status)) {
                    faultCompareBO.setMaximoFaultStatus(status)
                            .setMaximoFaultStatusText(statusText);
                }
            }
            faultSnBOMap.put(faultCompareBO.getMaximoFaultSn(), faultCompareBO);
        }

        return faultSnBOMap;
    }


    private String getFaultStatusText(String status) {
        // CLOSED、RESOLVED、NEW
        if (StringUtils.isBlank(status)) {
            return status;
        }
        if ("CLOSED".equals(status)) {
            return "已关闭";
        }
        if ("RESOLVED".equals(status)) {
            return "班组已处理";
        }
        if ("NEW".equals(status)) {
            return "新建";
        }
        return status;
    }

}
