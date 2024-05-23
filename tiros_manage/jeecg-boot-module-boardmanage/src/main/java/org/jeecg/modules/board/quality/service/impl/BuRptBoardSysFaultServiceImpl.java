package org.jeecg.modules.board.quality.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.bean.vo.chart.SingleBarChartVO;
import org.jeecg.common.tiros.cache.assettype.AssetTypeCacheService;
import org.jeecg.common.tiros.cache.assettype.BuTrainAssetTypeBO;
import org.jeecg.modules.board.quality.bean.BuRptBoardSysFault;
import org.jeecg.modules.board.quality.bean.vo.BuBoardQualityQueryVO;
import org.jeecg.modules.board.quality.bean.vo.BuCenterQualitySysFaultVO;
import org.jeecg.modules.board.quality.mapper.BuRptBoardSysFaultMapper;
import org.jeecg.modules.board.quality.service.BuRptBoardSysFaultService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 故障统计-中间表-系统维度 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-12
 */
@Slf4j
@Service
public class BuRptBoardSysFaultServiceImpl extends ServiceImpl<BuRptBoardSysFaultMapper, BuRptBoardSysFault> implements BuRptBoardSysFaultService {

    @Resource
    private BuRptBoardSysFaultMapper buRptBoardSysFaultMapper;
    @Resource
    private AssetTypeCacheService assetTypeCacheService;


    /**
     * @see BuRptBoardSysFaultService#listWarrantyPeriodSysFault(BuBoardQualityQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<SingleBarChartVO> listWarrantyPeriodSysFault(BuBoardQualityQueryVO queryVO) throws Exception {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        List<BuRptBoardSysFault> sysFaultList = buRptBoardSysFaultMapper.listByCondition(queryVO, year);

        List<BuCenterQualitySysFaultVO> sysFaultVOList = transformToSysFaultVO(sysFaultList);

        return transformToSingleBarChartVOList(sysFaultVOList);
    }


    private List<BuCenterQualitySysFaultVO> transformToSysFaultVO(List<BuRptBoardSysFault> sysFaultList) {
        List<BuCenterQualitySysFaultVO> sysFaultVOList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(sysFaultList)) {
            Map<String, BuTrainAssetTypeBO> sysMap = assetTypeCacheService.mapSys(null);

            for (Map.Entry<String, BuTrainAssetTypeBO> sysEntry : sysMap.entrySet()) {
                String sysId = sysEntry.getKey();
                BuTrainAssetTypeBO sysAssetTypeBO = sysEntry.getValue();
                String sysShortName = sysAssetTypeBO.getShortName();

                List<BuRptBoardSysFault> faultList = sysFaultList.stream()
                        .filter(sysFault -> sysId.equals(sysFault.getSysId()))
                        .collect(Collectors.toList());
                int sum = 0;
                if (CollectionUtils.isNotEmpty(faultList)) {
                    sum = faultList.stream()
                            .mapToInt(BuRptBoardSysFault::getSum3)
                            .sum();
                }

                sysFaultVOList.add(
                        new BuCenterQualitySysFaultVO()
                                .setSysName(sysShortName)
                                .setSum(sum)
                );
            }
        }

        return sysFaultVOList;
    }

    private List<SingleBarChartVO> transformToSingleBarChartVOList(List<BuCenterQualitySysFaultVO> sysFaultVOList) {
        List<SingleBarChartVO> result = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(sysFaultVOList)) {
            for (BuCenterQualitySysFaultVO sysFaultVO : sysFaultVOList) {
                result.add(
                        new SingleBarChartVO()
                                .setX(sysFaultVO.getSysName())
                                .setY(sysFaultVO.getSum().doubleValue())
                );
            }
        }

        return result;
    }

}
