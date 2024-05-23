package org.jeecg.modules.outsource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.outsource.bean.BuContractAnnex;
import org.jeecg.modules.outsource.bean.vo.BuContractAnnexSaveVO;
import org.jeecg.modules.outsource.mapper.BuContractAnnexMapper;
import org.jeecg.modules.outsource.service.BuContractAnnexService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 合同附件 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-23
 */
@Service
public class BuContractAnnexServiceImpl extends ServiceImpl<BuContractAnnexMapper, BuContractAnnex> implements BuContractAnnexService {

    @Resource
    private BuContractAnnexMapper buContractAnnexMapper;


    /**
     * @see BuContractAnnexService#listAnnex(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuContractAnnex> listAnnex(String contractId) throws Exception {
        LambdaQueryWrapper<BuContractAnnex> wrapper = new LambdaQueryWrapper<BuContractAnnex>()
                .orderByAsc(BuContractAnnex::getType)
                .orderByAsc(BuContractAnnex::getName);
        if (StringUtils.isNotBlank(contractId)) {
            wrapper.eq(BuContractAnnex::getContractId, contractId);
        }

        return buContractAnnexMapper.selectList(wrapper);
    }

    /**
     * @see BuContractAnnexService#saveAnnex(BuContractAnnexSaveVO)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveAnnex(BuContractAnnexSaveVO contractAnnexSaveVO) throws Exception {
        Boolean deleteBefore = contractAnnexSaveVO.getDeleteBefore();
        String contractId = contractAnnexSaveVO.getContractId();

        // 删除以前的附件
        if (null == deleteBefore) {
            deleteBefore = true;
        }
        if (deleteBefore) {
            LambdaQueryWrapper<BuContractAnnex> wrapper = new LambdaQueryWrapper<BuContractAnnex>()
                    .eq(BuContractAnnex::getContractId, contractId);
            buContractAnnexMapper.delete(wrapper);
        }

        // 插入附件
        List<BuContractAnnex> annexList = contractAnnexSaveVO.getAnnexList();
        if (CollectionUtils.isNotEmpty(annexList)) {
            for (BuContractAnnex orderAnnex : annexList) {
                orderAnnex.setContractId(contractId);
                buContractAnnexMapper.insert(orderAnnex);
            }
        }

        return true;
    }

    /**
     * @see BuContractAnnexService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> orderAnnexIdList = Arrays.asList(ids.split(","));

        if (CollectionUtils.isNotEmpty(orderAnnexIdList)) {
            buContractAnnexMapper.deleteBatchIds(orderAnnexIdList);
        }

        return true;
    }

}
