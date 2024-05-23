package org.jeecg.modules.outsource.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.outsource.bean.BuContractInfo;
import org.jeecg.modules.outsource.bean.BuContractPay;
import org.jeecg.modules.outsource.bean.vo.BuContractPayQueryVO;
import org.jeecg.modules.outsource.mapper.BuContractInfoMapper;
import org.jeecg.modules.outsource.mapper.BuContractPayMapper;
import org.jeecg.modules.outsource.service.BuContractPayService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 支付记录 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
@Service
public class BuContractPayServiceImpl extends ServiceImpl<BuContractPayMapper, BuContractPay> implements BuContractPayService {
    @Resource
    private BuContractPayMapper payMapper;
    @Resource
    private BuContractInfoMapper contractInfoMapper;

    @Override
    public IPage<BuContractPay> page(BuContractPayQueryVO queryVO, Integer pageNo, Integer pageSize) {

        IPage<BuContractPay> page = payMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
       /* IPage<BuContractPay> allPage = payMapper.selectPageByCondition(new Page<>(pageNo, -1), queryVO);
        List<BuContractPay> records = allPage.getRecords();
        if (CollectionUtils.isNotEmpty(records)) {
            BuContractPay contractPay = new BuContractPay();
            BigDecimal amountSum = records.stream().map(BuContractPay::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal qaMoneySum = records.stream().filter(item -> item.getQaMoney() != null).map(BuContractPay::getQaMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal ticketAmountSum = records.stream().filter(item -> item.getTicketAmount() != null).map(BuContractPay::getTicketAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            Map<String, Optional<BuContractPay>> leftoversGroup = records.stream().filter(item -> item.getLeftover()!= null).collect(Collectors.groupingBy(
                    BuContractPay::getContractId, Collectors.minBy(Comparator.comparingDouble(item -> item.getLeftover().doubleValue()))));
            Map<String, Optional<BuContractPay>> leftQaMoneyGroup = records.stream().filter(item -> item.getLeftQaMoney() != null).collect(Collectors.groupingBy(
                    BuContractPay::getContractId, Collectors.minBy(Comparator.comparingDouble(item -> item.getLeftQaMoney().doubleValue()))));
            List<BuContractPay> leftoversPays = new ArrayList<>();
            List<BuContractPay> leftQaMoneyPays = new ArrayList<>();
            leftoversGroup.entrySet().stream().forEach(item -> leftoversPays.add(item.getValue().get()));
            leftQaMoneyGroup.entrySet().stream().forEach(item -> leftQaMoneyPays.add(item.getValue().get()));
            BigDecimal leftoverSum = leftoversPays.stream().filter(item -> item.getLeftover() != null).map(BuContractPay::getLeftover).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal leftQaMoneySum = leftQaMoneyPays.stream().filter(item -> item.getLeftQaMoney() != null).map(BuContractPay::getLeftQaMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
            contractPay.setAmount(amountSum);
            contractPay.setQaMoney(qaMoneySum);
            contractPay.setTicketAmount(ticketAmountSum);
            contractPay.setLeftover(leftoverSum);
            contractPay.setLeftQaMoney(leftQaMoneySum);
            page.getRecords().add(contractPay);
        }*/
        return page;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveContractPay(BuContractPay contractPay) throws Exception {
       Integer maxPayNo= payMapper.selectMaxNewPay(contractPay.getContractId());
        if (Objects.nonNull(maxPayNo)) {
            contractPay.setPayNo(maxPayNo + 1);
        } else {
            contractPay.setPayNo(1);
        }
          /* if (null != contractPay.getAmount()){
                contractPay.setLeftover(maxNewPay.getLeftover().subtract(contractPay.getAmount()));
            }
            if (null != contractPay.getQaMoney()){
                contractPay.setLeftQaMoney(maxNewPay.getLeftQaMoney().subtract(contractPay.getQaMoney()));
            }
            BuContractInfo contractInfo = contractInfoMapper.selectById(contractPay.getContractId());
            if (null != contractPay.getAmount()){
                contractPay.setLeftover(contractInfo.getAmount().subtract(contractPay.getAmount()));
            }
            if (null != contractPay.getQaMoney()){
                contractPay.setLeftQaMoney(contractInfo.getDeposit().subtract(contractPay.getQaMoney()));
            }*/
        return this.save(contractPay);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean editContractPay(BuContractPay contractPay) {
      //  BuContractPay pay = payMapper.selectById(contractPay.getId());
        this.updateById(contractPay);
        return true;
           /* List<BuContractPay> payList = payMapper.selectList(Wrappers.<BuContractPay>lambdaQuery()
                .gt(BuContractPay::getPayNo, contractPay.getPayNo()));
        if(CollectionUtils.isNotEmpty(payList)) {
            if (pay.getAmount().compareTo(contractPay.getAmount()) != 0) {
                payList.forEach(p -> {
                    BigDecimal add = p.getLeftover().add(pay.getAmount()
                            .subtract(contractPay.getAmount()));
                    if (add.compareTo(BigDecimal.ZERO) < 0) {
                        p.setAmount(p.getAmount().add(add));
                        p.setLeftover(BigDecimal.ZERO);
                    } else {
                        p.setLeftover(add);
                    }
                    p.updateById();
                });
            }
            if (pay.getQaMoney().compareTo(contractPay.getQaMoney()) != 0) {
                payList.forEach(p -> {
                    BigDecimal add = p.getLeftQaMoney().add(pay.getQaMoney()
                            .subtract(contractPay.getQaMoney()));
                    if (add.compareTo(BigDecimal.ZERO) < 0) {
                        p.setQaMoney(p.getQaMoney().add(add));
                        p.setLeftQaMoney(BigDecimal.ZERO);
                    } else {
                        p.setLeftQaMoney(add);
                    }
                    p.updateById();
                });
            }
        }
          payMapper.updateLeftover(pay.getAmount()
                .subtract(contractPay.getAmount()), contractPay.getId());
        payMapper.updateLeftQaMoney(pay.getQaMoney()
                .subtract(contractPay.getQaMoney()), contractPay.getId());
        */
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIdsAndRestore(List<String> asList) {
        return this.removeByIds(asList);
         /* asList.forEach(id -> {
            BuContractPay contractPay = payMapper.selectById(id);
            List<BuContractPay> contractPays = payMapper.selectList(Wrappers.<BuContractPay>lambdaQuery()
                    .eq(BuContractPay::getContractId, contractPay.getContractId())
                    .gt(BuContractPay::getPayNo, contractPay.getPayNo()));
            if (oConvertUtils.listIsNotEmpty(contractPays)) {
                contractPays.forEach(pay -> {
                    if (null != contractPay.getAmount()){
                        pay.setLeftover(pay.getLeftover().add(contractPay.getAmount()));
                    }

                    if (null != contractPay.getQaMoney()){
                        pay.setLeftQaMoney(pay.getLeftQaMoney().add(contractPay.getQaMoney()));
                    }

                    pay.updateById();
                });
            }
        });*/
    }
}
