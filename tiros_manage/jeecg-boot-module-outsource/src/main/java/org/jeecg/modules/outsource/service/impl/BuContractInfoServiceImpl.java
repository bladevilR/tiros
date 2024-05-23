package org.jeecg.modules.outsource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.PercentUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.dispatch.workorder.bean.BuOutsourceOutinDetail;
import org.jeecg.modules.dispatch.workorder.mapper.BuOutsourceOutinDetailMapper;
import org.jeecg.modules.outsource.bean.*;
import org.jeecg.modules.outsource.bean.vo.BuContractInfoQueryVO;
import org.jeecg.modules.outsource.bean.vo.BuOutsourceAssetQueryVO;
import org.jeecg.modules.outsource.bean.vo.BuOutsourceOutInQualityVO;
import org.jeecg.modules.outsource.bean.vo.BuOutsourceQualityQueryVO;
import org.jeecg.modules.outsource.mapper.*;
import org.jeecg.modules.outsource.service.BuContractInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 合同信息 服务实现类
 *
 * @author youGen
 * @since 2020-10-16
 */
@Service
public class BuContractInfoServiceImpl extends ServiceImpl<BuContractInfoMapper, BuContractInfo> implements BuContractInfoService {

    @Resource
    private BuContractInfoMapper buContractInfoMapper;
    @Resource
    private BuContractExtInfoMapper buContractExtInfoMapper;
    @Resource
    private BuContractAnnexMapper buContractAnnexMapper;
    @Resource
    private BuContractPayMapper buContractPayMapper;
    @Resource
    private BuContractBakMoneyMapper buContractBakMoneyMapper;
    @Resource
    private BuBaseSupplierMapper buBaseSupplierMapper;
    @Resource
    private BuOutsourceOutinDetailMapper buOutsourceOutinDetailMapper;
    @Resource
    private BuOutsourceResourceMapper outsourceResourceMapper;
    @Resource
    private BuOutsourceRateingMapper outsourceRateingMapper;
    @Resource
    private BuOutsourceRateingAnnexMapper rateingAnnexMapper;


    /**
     * @see BuContractInfoService#saveContractInfo(BuContractInfo)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveContractInfo(BuContractInfo contractInfo) throws Exception {
        String contractId = contractInfo.getId();
        if (StringUtils.isNotEmpty(contractId)) {
            // 删除关联信息
            deleteRelations(Collections.singletonList(contractId));
            // 删除合同
            buContractInfoMapper.deleteById(contractId);
        }

        // 插入合同
        buContractInfoMapper.insert(contractInfo);
        // 插入关联信息
        insertRelations(contractInfo);

        return true;
    }

    /**
     * @see BuContractInfoService#editContractInfo(BuContractInfo)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean editContractInfo(BuContractInfo contractInfo) throws Exception {
        String contractId = contractInfo.getId();
        // 删除关联信息
        deleteRelations(Collections.singletonList(contractId));
        // 删除合同
        buContractInfoMapper.deleteById(contractId);

        // 插入合同
        buContractInfoMapper.insert(contractInfo);
        // 插入关联信息
        insertRelations(contractInfo);

        return true;
    }

    @Override
    public IPage<BuContractInfo> page(BuContractInfoQueryVO queryVO, Integer pageNo, Integer pageSize) {
        return buContractInfoMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeBachByIds(List<String> ids) throws Exception {
        ids.forEach(
                id -> {
                    buContractExtInfoMapper.delete(Wrappers.<BuContractExtInfo>lambdaUpdate().eq(BuContractExtInfo::getContractId, id));
                    new BuContractBakMoney().delete(Wrappers.<BuContractBakMoney>lambdaUpdate().eq(BuContractBakMoney::getContractId, id));
                    new BuContractPay().delete(Wrappers.<BuContractPay>lambdaUpdate().eq(BuContractPay::getContractId, id));
                });
        return this.removeByIds(ids);
    }

    @Override
    public ContractMonitor contractMonitor(String id) {
        BuContractInfo contractInfo = buContractInfoMapper.selectById(id);
        ContractMonitor monitor = new ContractMonitor();
        BigDecimal amount = contractInfo.getAmount();
        BigDecimal behindMoney = contractInfo.getBehindMoney();
        BigDecimal sumPay = buContractInfoMapper.selectSumPay(id);
        BuBaseSupplier supplier = buBaseSupplierMapper.selectById(contractInfo.getSupplierId());
        if (supplier != null) {
            monitor.setScore(supplier.getAppraise());
        }
        if (sumPay != null && amount != null) {
            monitor.setPayed(PercentUtils.percent(sumPay.doubleValue(), amount.doubleValue()));
        }
        BigDecimal sumBak = buContractInfoMapper.selectSumBak(id);
        if (sumBak != null && behindMoney != null) {
            monitor.setBakMoney(PercentUtils.percent(sumBak.doubleValue(), behindMoney.doubleValue()));
        }
        Integer expiration = contractInfo.getExpiration();
        Long startTime = contractInfo.getStartDate().getTime();
        Long finishTime = contractInfo.getFinishDate().getTime();
        Long nowTime = System.currentTimeMillis();
        long poor = nowTime - startTime;
        long quality = nowTime - finishTime;
        if (quality > 0) {
            monitor.setQuality((double) (quality / 1000 / 60 / 60 / 24 * 100 / 30 / expiration));
        }
        if (poor > 0) {
            long contractPoor = finishTime - startTime;
            if (contractPoor <= 0) {
                monitor.setContractProcess(100.00);
            } else {
                monitor.setContractProcess((double) (poor / contractPoor * 100));
            }
        }
        return monitor;
    }

    @Override
    public IPage<BuOutsourceQuality> qualityPage(
            BuOutsourceQualityQueryVO queryVO, Integer pageNo, Integer pageSize) {
        IPage<BuOutsourceQuality> qualityIPage = buContractInfoMapper.qualityPage(new Page<>(pageNo, pageSize), queryVO);
        List<BuOutsourceQuality> qualities = qualityIPage.getRecords();
        if (oConvertUtils.listIsNotEmpty(qualities)) {
            qualities.forEach(
                    item -> {
                        Date start = item.getStartDate();
                        Integer surplusDays = item.getDayCount();
                        if (Objects.nonNull(start) && Objects.nonNull(surplusDays)) {
                            long endTime = start.getTime();
                            long nowTime = System.currentTimeMillis();
                            if (nowTime - endTime < 0) {
                                item.setSurplusDays(item.getDayCount());
                            } else {
                                int days = (int) ((nowTime - endTime) / (1000 * 60 * 60 * 24));
                                item.setSurplusDays(surplusDays - days);
                            }

                            if (item.getSurplusDays() < 0) {
                                item.setQualityDayStr("0/" + item.getDayCount());
                            } else {
                                item.setQualityDayStr(item.getSurplusDays() + "/" + item.getDayCount());
                            }
                        }
                    });
            qualities.stream().filter(item -> item.getSurplusDays() != null).sorted(Comparator.comparingInt(BuOutsourceQuality::getSurplusDays));
        }
        return qualityIPage;
    }

    @Override
    public IPage<BuOutsourceAsset> partsPage(
            BuOutsourceAssetQueryVO queryVO, Integer pageNo, Integer pageSize) {
        queryVO.toStartEndDate();
        return buContractInfoMapper.partsPage(new Page<>(pageNo, pageSize), queryVO);
    }

    @Override
    public Integer getAssetTypeNeedDay(String assetTypeId) {
        return buContractInfoMapper.getAssetTypeNeedDay(assetTypeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateQuality(BuOutsourceOutInQualityVO qualityVO) throws Exception {
        List<String> detailIdList = Arrays.asList(qualityVO.getDetailId().split(","));
        detailIdList.forEach(detailId -> {
            BuOutsourceOutinDetail outinDetail = buOutsourceOutinDetailMapper.selectById(detailId);
            if (StringUtils.isNotBlank(qualityVO.getRemark())) {
                buOutsourceOutinDetailMapper.updateById(new BuOutsourceOutinDetail()
                        .setId(detailId).setRemark(qualityVO.getRemark()));
            }
            saveOutInQuality(qualityVO, detailId, outinDetail);
        });
        return true;
    }

    private void saveOutInQuality(BuOutsourceOutInQualityVO qualityVO, String detailId, BuOutsourceOutinDetail outinDetail) {
        new BuOutsourceOutinQuality()
                .setAmount(outinDetail.getAmount())
                .setAssetName(outinDetail.getAssetName())
                .setAssetNo(outinDetail.getAssetNo())
                .setSystemId(outinDetail.getAssetTypeId())
                .setDetailId(detailId)
                .setTrainNo(outinDetail.getTrainNo())
                .setTurnoverAssetId(outinDetail.getTurnoverAssetId())
                .setCheckDate(qualityVO.getCheckDate())
                .setStartDate(qualityVO.getStartDate())
                .setEndDate(qualityVO.getEndDate())
                .setDayCount(qualityVO.getDayCount())
                .insert();
    }

    @Override
    public List<ContractPrice> getContractPrice(List<String> ids) throws Exception {
        List<ContractPrice> priceList = new ArrayList<>();
        ids.forEach(id -> {
            BuContractInfo contract = buContractInfoMapper.selectById(id);
            if (contract != null) {
                Integer trainAmount = contract.getTrainAmount();
                BigDecimal contractAmount = contract.getAmount();
                Integer assetAmount = contract.getAssetAmount();
                ContractPrice price = new ContractPrice();
                price.setId(contract.getId());
                price.setContractNo(contract.getContractNo());
                price.setContractName(contract.getContractName());
                price.setSignDate(contract.getSignDate());
                price.setContractAmount(contractAmount);
                price.setTrainAmount(trainAmount);
                if (trainAmount != null && contractAmount != null) {
                    price.setSinglePrice(price.getContractAmount().divide(BigDecimal.valueOf(trainAmount), 8, BigDecimal.ROUND_HALF_UP));
                }
                if (assetAmount != null && price.getSinglePrice() != null) {
                    price.setPartPrice(price.getSinglePrice().divide(BigDecimal.valueOf(assetAmount), 8, BigDecimal.ROUND_HALF_UP));
                }
                priceList.add(price);
            }
        });
        return priceList;
    }


    private void deleteRelations(List<String> contractIdList) {
        if (CollectionUtils.isEmpty(contractIdList)) {
            return;
        }

        LambdaQueryWrapper<BuContractExtInfo> extInfoWrapper = new LambdaQueryWrapper<BuContractExtInfo>()
                .in(BuContractExtInfo::getContractId, contractIdList);
        buContractExtInfoMapper.delete(extInfoWrapper);
        LambdaQueryWrapper<BuContractAnnex> annexWrapper = new LambdaQueryWrapper<BuContractAnnex>()
                .in(BuContractAnnex::getContractId, contractIdList);
        buContractAnnexMapper.delete(annexWrapper);
        LambdaQueryWrapper<BuContractPay> payWrapper = new LambdaQueryWrapper<BuContractPay>()
                .in(BuContractPay::getContractId, contractIdList);
        buContractPayMapper.delete(payWrapper);
        LambdaQueryWrapper<BuContractBakMoney> bakMoneyWrapper = new LambdaQueryWrapper<BuContractBakMoney>()
                .in(BuContractBakMoney::getContractId, contractIdList);
        buContractBakMoneyMapper.delete(bakMoneyWrapper);
        LambdaQueryWrapper<BuOutsourceResource> resourceWrapper = new LambdaQueryWrapper<BuOutsourceResource>()
                .in(BuOutsourceResource::getContractId, contractIdList);
        outsourceResourceMapper.delete(resourceWrapper);
        LambdaQueryWrapper<BuOutsourceRateing> rateingWrapper = new LambdaQueryWrapper<BuOutsourceRateing>()
                .in(BuOutsourceRateing::getContractId, contractIdList);
        List<BuOutsourceRateing> rateingList = outsourceRateingMapper.selectList(Wrappers.<BuOutsourceRateing>lambdaQuery().in(BuOutsourceRateing::getContractId, contractIdList));
        if (CollectionUtils.isNotEmpty(rateingList)) {
            List<String> rateingIdList = rateingList.stream().map(BuOutsourceRateing::getId).collect(Collectors.toList());
            LambdaQueryWrapper<BuOutsourceRateingAnnex> rateingAnnexWrapper = new LambdaQueryWrapper<BuOutsourceRateingAnnex>()
                    .in(BuOutsourceRateingAnnex::getRateId, rateingIdList);
            rateingAnnexMapper.delete(rateingAnnexWrapper);
        }
        outsourceRateingMapper.delete(rateingWrapper);


    }

    private void insertRelations(BuContractInfo contractInfo) {
        String contractId = contractInfo.getId();
        BuContractExtInfo extInfo = contractInfo.getExtInfo();
        if (null != extInfo) {
            extInfo.setContractId(contractId);
            buContractExtInfoMapper.insert(extInfo);
        }

        List<BuContractAnnex> annexList = contractInfo.getAnnexList();
        if (CollectionUtils.isNotEmpty(annexList)) {
            for (BuContractAnnex annex : annexList) {
                annex.setContractId(contractId);
                buContractAnnexMapper.insert(annex);
            }
        }

        List<BuContractPay> payList = contractInfo.getPayList();
        if (CollectionUtils.isNotEmpty(payList)) {
            for (BuContractPay pay : payList) {
                pay.setContractId(contractId);
                buContractPayMapper.insert(pay);
            }
        }

        List<BuContractBakMoney> bakMoneyList = contractInfo.getBakMoneyList();
        if (CollectionUtils.isNotEmpty(bakMoneyList)) {
            for (BuContractBakMoney bakMoney : bakMoneyList) {
                bakMoney.setContractId(contractId);
                buContractBakMoneyMapper.insert(bakMoney);
            }
        }
    }

}
