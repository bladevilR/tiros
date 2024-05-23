package org.jeecg.modules.third.jdx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.modules.third.jdx.bean.BuMaterialType;
import org.jeecg.modules.third.jdx.bean.BuRptMaterialUseRecord;
import org.jeecg.modules.third.jdx.mapper.BuMaterialTypeThirdMapper;
import org.jeecg.modules.third.jdx.mapper.BuRptMaterialUseRecordThirdMapper;
import org.jeecg.modules.third.jdx.service.BuRptMaterialUseRecordThirdService;
import org.jeecg.modules.third.maximo.bean.JdxMatusetransOut;
import org.jeecg.modules.third.utils.DatabaseBatchSubUtil;
import org.jeecg.modules.third.utils.UUIDGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 物料消耗明细 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-13
 */
@Service
public class BuRptMaterialUseRecordThirdServiceImpl extends ServiceImpl<BuRptMaterialUseRecordThirdMapper, BuRptMaterialUseRecord> implements BuRptMaterialUseRecordThirdService {

    @Resource
    private BuRptMaterialUseRecordThirdMapper buRptMaterialUseRecordThirdMapper;
    @Resource
    private BuMaterialTypeThirdMapper buMaterialTypeThirdMapper;


    /**
     * @see BuRptMaterialUseRecordThirdService#insertOrderMaterialFromMaximoData(List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insertOrderMaterialFromMaximoData(List<JdxMatusetransOut> maximoOrderMaterialList) throws Exception {
        if (CollectionUtils.isEmpty(maximoOrderMaterialList)) {
            return true;
        }

        // 获取物质信息
        List<String> materialTypeCodeList = maximoOrderMaterialList.stream()
                .map(JdxMatusetransOut::getItemnum)
                .collect(Collectors.toList());
        Map<String, BuMaterialType> codeMaterialTypeMap = new HashMap<>(1024);
        if (CollectionUtils.isNotEmpty(materialTypeCodeList)) {
            List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(materialTypeCodeList);
            for (List<String> batchSub : batchSubList) {
                LambdaQueryWrapper<BuMaterialType> materialTypeWrapper = new LambdaQueryWrapper<BuMaterialType>()
                        .in(BuMaterialType::getCode, batchSub);
                List<BuMaterialType> materialTypeList = buMaterialTypeThirdMapper.selectList(materialTypeWrapper);
                materialTypeList.forEach(material -> codeMaterialTypeMap.put(material.getCode(), material));
            }
        }

        List<BuRptMaterialUseRecord> materialUseRecordList = new ArrayList<>();
        for (JdxMatusetransOut maximoOrderMaterial : maximoOrderMaterialList) {
            BuRptMaterialUseRecord materialUseRecord = new BuRptMaterialUseRecord()
                    .setId(UUIDGenerator.generate())
                    .setArchiveType(1)
                    .setWorkOrderId(maximoOrderMaterial.getRefwo())
                    .setWorkOrderName(maximoOrderMaterial.getRefwo())
                    .setOrderTaskId(null)
                    .setOrderTaskName(null)
                    .setMaterialTypeId(maximoOrderMaterial.getItemnum())
                    .setMaterialTypeName(maximoOrderMaterial.getDescription())
                    .setMaterialTypeCode(maximoOrderMaterial.getItemnum())
                    .setMaterialTypeModel(maximoOrderMaterial.getDescription())
                    .setMaterialDesc(maximoOrderMaterial.getDescription())
                    .setUnit(null)
                    .setKind(-1)
                    .setCategory1("-1")
                    .setCategory2(null)
                    .setAmount(BigDecimal.valueOf(maximoOrderMaterial.getPositivequantity()))
                    .setUnitPrice(BigDecimal.valueOf(maximoOrderMaterial.getUnitcost()))
                    .setTotalPrice(BigDecimal.valueOf(maximoOrderMaterial.getLinecost()))
                    .setUseDate(maximoOrderMaterial.getTransdate())
                    .setAssetCode(null)
                    .setDepotId("待从配置获取")
                    .setDepotName("待从配置获取")
                    .setWorkshopId(null)
                    .setWorkshopName(null)
                    .setLineId("待关联maximo工单表查询")
                    .setLineName("待关联maximo工单表查询")
                    .setTrainId("待关联maximo工单表查询")
                    .setTrainName("待关联maximo工单表查询")
                    .setTsystemId(null)
                    .setSystemName(null)
                    .setPeriodId(null)
                    .setGroupId("待关联maximo工单表查询")
                    .setGroupName("待关联maximo工单表查询")
                    .setMonitor(null)
                    .setMonitorName(null)
                    .setStationId(null)
                    .setStationNo(null)
                    .setStationName(null);

            String itemnum = maximoOrderMaterial.getItemnum();
            BuMaterialType materialType = codeMaterialTypeMap.get(itemnum);
            if (null != materialType) {
                materialUseRecord
                        .setMaterialTypeId(materialType.getId())
                        .setMaterialTypeName(materialType.getName())
                        .setMaterialTypeCode(materialType.getCode())
                        .setMaterialTypeModel(materialType.getSpec())
                        .setMaterialDesc(materialType.getSpec())
                        .setUnit(materialType.getUnit())
                        .setKind(materialType.getKind())
                        .setCategory1(String.valueOf(materialType.getCategory1()))
                        .setCategory2(materialType.getCategory2());
            }

            materialUseRecordList.add(materialUseRecord);
        }

        if (CollectionUtils.isNotEmpty(materialUseRecordList)) {
            List<List<BuRptMaterialUseRecord>> batchSubList = DatabaseBatchSubUtil.batchSubList(materialUseRecordList);
            for (List<BuRptMaterialUseRecord> batchSub : batchSubList) {
                buRptMaterialUseRecordThirdMapper.insertList(batchSub);
            }
        }

        return true;
    }
}
