package org.jeecg.modules.outsource.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.tiros.task.service.SupplierAppraiseTaskService;
import org.jeecg.modules.outsource.bean.BuBaseSupplier;
import org.jeecg.modules.outsource.bean.BuOutsourceRateing;
import org.jeecg.modules.outsource.mapper.BuBaseSupplierMapper;
import org.jeecg.modules.outsource.mapper.BuOutsourceRateingMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

/**
 * <p>
 * 计算厂商分数定时任务 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-25
 */
@Service
public class SupplierAppraiseTaskServiceImpl implements SupplierAppraiseTaskService {

    @Resource
    private BuOutsourceRateingMapper buOutsourceRateingMapper;
    @Resource
    private BuBaseSupplierMapper buBaseSupplierMapper;


    /**
     * @see SupplierAppraiseTaskService#computeSupplierAppraise(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean computeSupplierAppraise(String id) throws RuntimeException {
        // 查询厂商评分项
        List<BuOutsourceRateing> outsourceRatingList = buOutsourceRateingMapper.selectListBySupplierId(id);

        // 计算评分并更新
        if (CollectionUtils.isNotEmpty(outsourceRatingList)) {
            Map<String, List<BuOutsourceRateing>> supplierIdRatingListMap = outsourceRatingList.stream()
                    .filter(rateing -> StringUtils.isNotBlank(rateing.getSupplierId()))
                    .collect(Collectors.groupingBy(BuOutsourceRateing::getSupplierId));
            for (Map.Entry<String, List<BuOutsourceRateing>> supplierIdRatingListEntry : supplierIdRatingListMap.entrySet()) {
                String supplierId = supplierIdRatingListEntry.getKey();
                List<BuOutsourceRateing> ratingList = supplierIdRatingListEntry.getValue();

                OptionalDouble average = ratingList.stream()
                        .mapToInt(BuOutsourceRateing::getScore)
                        .average();
                if (average.isPresent()) {
                    double appraise = average.getAsDouble();
                    BuBaseSupplier baseSupplier = new BuBaseSupplier()
                            .setId(supplierId)
                            .setAppraise(appraise);
                    buBaseSupplierMapper.updateById(baseSupplier);
                }
            }
        }

        return true;
    }

}
