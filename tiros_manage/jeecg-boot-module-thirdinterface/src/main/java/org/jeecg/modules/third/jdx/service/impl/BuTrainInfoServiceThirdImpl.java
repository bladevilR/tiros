package org.jeecg.modules.third.jdx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.third.jdx.bean.BuTrainInfo;
import org.jeecg.modules.third.jdx.mapper.BuTrainInfoThirdMapper;
import org.jeecg.modules.third.jdx.service.BuTrainInfoThirdService;
import org.jeecg.modules.third.maximo.bean.JdxAssetOut;
import org.jeecg.modules.third.utils.DatabaseBatchSubUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 车辆信息 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-21
 */
@Service
public class BuTrainInfoServiceThirdImpl extends ServiceImpl<BuTrainInfoThirdMapper, BuTrainInfo> implements BuTrainInfoThirdService {

    @Resource
    private BuTrainInfoThirdMapper buTrainInfoThirdMapper;


    /**
     * @see BuTrainInfoThirdService#deleteByLineId(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteByLineId(String lineId) throws Exception {
        if (StringUtils.isBlank(lineId)) {
            return true;
        }

        LambdaQueryWrapper<BuTrainInfo> wrapper = new LambdaQueryWrapper<BuTrainInfo>()
                .eq(BuTrainInfo::getLineId, lineId);
        buTrainInfoThirdMapper.delete(wrapper);

        return true;
    }

    /**
     * @see BuTrainInfoThirdService#insertTrainFromMaximoData(List, String, String, String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insertTrainFromMaximoData(List<JdxAssetOut> maximoTrainList, String lineId, String companyId, String workshopId) throws Exception {
        if (CollectionUtils.isEmpty(maximoTrainList)) {
            return true;
        }

        // 查询车辆
        List<BuTrainInfo> trainList = buTrainInfoThirdMapper.selectList(Wrappers.emptyWrapper());
        Set<String> trainNoSet = trainList.stream()
                .map(BuTrainInfo::getTrainNo)
                .collect(Collectors.toSet());
        Set<String> trainIdSet = trainList.stream()
                .map(BuTrainInfo::getId)
                .collect(Collectors.toSet());

        List<BuTrainInfo> needAddTrainList = new ArrayList<>();
        for (JdxAssetOut maximoAsset : maximoTrainList) {
            // 有部分名称为空的，跳过
            if (StringUtils.isBlank(maximoAsset.getDescription())) {
                continue;
            }

            String trainId = maximoAsset.getAssetnum();
//            String trainNo = getNumberTrainNo(maximoAsset.getDescription(), lineId);
            String trainNo = maximoAsset.getDescription().replaceAll("车", "");

            boolean trainIdExists = trainIdSet.contains(trainId);
            boolean trainNoExists = trainNoSet.contains(trainNo);
            if ((!trainIdExists) && (!trainNoExists)) {
                // 车辆数据不存在增加车辆
                // 属性转换
                BuTrainInfo train = new BuTrainInfo()
                        .setId(trainId)
                        .setTrainTypeId("1")
                        .setLineId(lineId)
                        .setTrainNo(trainNo)
                        .setGroupNum(1)
                        .setMileage(0D)
                        .setStatus(getStatusInt(maximoAsset.getStatus()))
                        .setUseDate(maximoAsset.getInstalldate())
                        .setWarrantyDate(maximoAsset.getInstalldate())
                        .setTrackId(null)
                        .setSupplierId(null)
                        .setContractNo(null)
                        .setContractName(null)
                        .setMadeDate(maximoAsset.getInstalldate())
                        .setBuyDate(maximoAsset.getInstalldate())
                        .setTrainStructId(null)
                        .setRemark("线路：" + maximoAsset.getCLinenum() + "车辆：" + maximoAsset.getDescription() + "(maximo导入)")
                        .setIsGenAsset(0)
                        .setCompanyId(companyId)
                        .setWorkshopId(workshopId);
                needAddTrainList.add(train);
                trainIdSet.add(trainId);
                trainNoSet.add(trainNo);
            }
        }

        // 保存数据
        if (CollectionUtils.isNotEmpty(needAddTrainList)) {
            List<List<BuTrainInfo>> batchSubList = DatabaseBatchSubUtil.batchSubList(needAddTrainList);
            for (List<BuTrainInfo> batchSub : batchSubList) {
                buTrainInfoThirdMapper.insertList(batchSub);
            }
        }

        return true;
    }


    private Integer getStatusInt(String status) {
        int statusInt = 0;

        if (StringUtils.isBlank(status)) {
            return statusInt;
        }
        /**
         * DECOMMISSIONED	停止使用
         * NOT READY	新建
         * OPERATING	操作中
         * REPAIR	检修
         * SCRAP	报废
         * WAITUSE	待使用
         */
        switch (status) {
            case "OPERATING":
            case "REPAIR":
                statusInt = 1;
                break;
            default:
                break;
        }

        return statusInt;
    }

//    private String getNumberTrainNo(String description, String lineId) {
//        if (lineId.equals("1")) {
//            return "0" + lineId + getNumberTrainNoByArabic(description);
//        } else if (lineId.equals("2")) {
//            return "0" + lineId + getNumberTrainNoByChinese(description);
//        } else {
//            return "0" + lineId + getNumberTrainNoByArabic(description);
//        }
//    }
//
//    private String getNumberTrainNoByArabic(String description) {
//        Pattern pattern = Pattern.compile("[^0-9]");
//        Matcher matcher = pattern.matcher(description);
//        return matcher.replaceAll("");
//    }
//
//    private String getNumberTrainNoByChinese(String description) {
//        String chineseNo = description.replaceAll("第", "").replaceAll("列车", "");
//        String arabic = NumberUtil.toArabic(chineseNo);
//        if (arabic.length() <= 1) {
//            return "0" + arabic;
//        } else {
//            return arabic;
//        }
//    }

}
