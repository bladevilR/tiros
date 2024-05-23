package org.jeecg.modules.outsource.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.outsource.bean.BuContractBakMoney;
import org.jeecg.modules.outsource.bean.BuContractInfo;
import org.jeecg.modules.outsource.bean.BuContractPay;
import org.jeecg.modules.outsource.bean.vo.BuContractBakMoneyQueryVO;
import org.jeecg.modules.outsource.mapper.BuContractBakMoneyMapper;
import org.jeecg.modules.outsource.mapper.BuContractInfoMapper;
import org.jeecg.modules.outsource.service.BuContractBakMoneyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 暂列金使用记录 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
@Service
public class BuContractBakMoneyServiceImpl extends ServiceImpl<BuContractBakMoneyMapper, BuContractBakMoney> implements BuContractBakMoneyService {
@Resource
private BuContractBakMoneyMapper   bakMoneyMapper;
    @Resource
    private BuContractInfoMapper contractInfoMapper;
    @Override
    public IPage<BuContractBakMoney> page(BuContractBakMoneyQueryVO queryVO, Integer pageNo, Integer pageSize) {
        return bakMoneyMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBakMoney(BuContractBakMoney bakMoney) {
        BuContractBakMoney maxNewPay = bakMoneyMapper.selectMaxNewPay(bakMoney.getContractId());
        if (Objects.nonNull(maxNewPay)) {
            bakMoney.setUseNo(maxNewPay.getUseNo() + 1);
            bakMoney.setLeftover(maxNewPay.getLeftover().subtract(bakMoney.getAmount()));
        } else {
            BuContractInfo contractInfo = contractInfoMapper.selectById(bakMoney.getContractId());
            if(Objects.nonNull(contractInfo.getBehindMoney())) {
                bakMoney.setUseNo(1);
                bakMoney.setLeftover(contractInfo.getBehindMoney().subtract(bakMoney.getAmount()));
            }else {
                throw  new JeecgBootException("合同内暂列金为0!");
            }
        }
        return this.save(bakMoney);


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean editBakMoney(BuContractBakMoney bakMoney) {
        BuContractBakMoney  contractBakMoney=bakMoneyMapper.selectById(bakMoney.getId());
        if(contractBakMoney.getAmount().compareTo(bakMoney.getAmount())!=0){
            List<BuContractBakMoney> contractBakMoneyList = bakMoneyMapper.selectList(Wrappers.<BuContractBakMoney>lambdaQuery()
                    .gt(BuContractBakMoney::getUseNo, bakMoney.getUseNo()));
            if(oConvertUtils.listIsNotEmpty(contractBakMoneyList)){
                contractBakMoneyList.forEach(p->{
                    BigDecimal add = p.getLeftover().add(contractBakMoney.getAmount()
                            .subtract(bakMoney.getAmount()));
                    if(add.compareTo(BigDecimal.ZERO) < 0){
                        p.setAmount(p.getAmount().add(add));
                        p.setLeftover(BigDecimal.ZERO);
                    }else{
                        p.setLeftover(add);
                    }
                    p.updateById();

                  /*  bakMoneyMapper.updateLeftover(contractBakMoney.getAmount()
                            .subtract(bakMoney.getAmount()),p.getId());*/
                });
            }
        }
        this.updateById(bakMoney);
        bakMoneyMapper.updateLeftover(contractBakMoney.getAmount()
                .subtract(bakMoney.getAmount()),bakMoney.getId());
        return  true;
    }

    @Override
    public boolean removeByIdsAndRestore(List<String> asList) {
        asList.forEach(id -> {
            BuContractBakMoney contractBakMoney = bakMoneyMapper.selectById(id);
            List<BuContractBakMoney> contractBakMoneyList = bakMoneyMapper.selectList(Wrappers.<BuContractBakMoney>lambdaQuery()
                    .eq(BuContractBakMoney::getContractId, contractBakMoney.getContractId())
                    .gt(BuContractBakMoney::getUseNo,contractBakMoney.getUseNo()));
            if (oConvertUtils.listIsNotEmpty(contractBakMoneyList)) {
                contractBakMoneyList.forEach(pay -> {
                    pay.setLeftover(pay.getLeftover().add(contractBakMoney.getAmount()));
                    pay.updateById();
                });
            }
        });
        return this.removeByIds(asList);
    }
}

