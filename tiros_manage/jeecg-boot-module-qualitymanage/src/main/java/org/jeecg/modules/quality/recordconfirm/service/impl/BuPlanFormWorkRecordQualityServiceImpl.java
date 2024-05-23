package org.jeecg.modules.quality.recordconfirm.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.modules.quality.recordconfirm.bean.*;
import org.jeecg.modules.quality.recordconfirm.bean.vo.BuFormInstanceVO;
import org.jeecg.modules.quality.recordconfirm.bean.vo.BuFormRecordQueryVO;
import org.jeecg.modules.quality.recordconfirm.bean.vo.BuFormWorkRecordCheckVO;
import org.jeecg.modules.quality.recordconfirm.mapper.BuPlanFormDataRecordQualityMapper;
import org.jeecg.modules.quality.recordconfirm.mapper.BuPlanFormWorkRecordQualityMapper;
import org.jeecg.modules.quality.recordconfirm.service.BuPlanFormWorkRecordQualityService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 作业记录表 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-11
 */
@Slf4j
@Service
public class BuPlanFormWorkRecordQualityServiceImpl extends ServiceImpl<BuPlanFormWorkRecordQualityMapper, BuPlanFormWorkRecord> implements BuPlanFormWorkRecordQualityService {

    @Resource
    private BuPlanFormWorkRecordQualityMapper buPlanFormWorkRecordQualityMapper;
    @Resource
    private BuPlanFormDataRecordQualityMapper buPlanFormDataRecordQualityMapper;


//    /**
//     * @see BuPlanFormWorkRecordQualityService#listFormOrderRecordVO(BuWorkOrderRecordQueryVO)
//     */
//    @Transactional(readOnly = true, rollbackFor = Exception.class)
//    @Override
//    public List<BuPlanFormWorkRecordVO> listFormOrderRecordVO(BuWorkOrderRecordQueryVO queryVO) throws Exception {
//        List<BuPlanFormWorkRecordVO> formWorkRecordVOList = buPlanFormWorkRecordQualityMapper.selectRecordVOByCondition(queryVO);
//
//        if (CollectionUtils.isNotEmpty(formWorkRecordVOList)) {
//            for (BuPlanFormWorkRecordVO formWorkRecordVO : formWorkRecordVOList) {
//                Double percent = PercentUtils.percent(formWorkRecordVO.getFinishDetailCount(), formWorkRecordVO.getDetailCount());
//
//                formWorkRecordVO.setProgress(percent.intValue());
//                formWorkRecordVO.setStatus(100 == percent ? "已完成" : "未完成");
//            }
//            formWorkRecordVOList.sort(Comparator.comparing(BuPlanFormWorkRecordVO::getProgress).reversed());
//        }
//
//        return formWorkRecordVOList;
//    }

    /**
     * @see BuPlanFormWorkRecordQualityService#pageFormInstanceRecord(BuFormRecordQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuFormInstanceVO> pageFormInstanceRecord(BuFormRecordQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buPlanFormWorkRecordQualityMapper.selectFormWorkAndDataRecordVOPageByFormRecordQueryVO(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuPlanFormWorkRecordQualityService#getFormWorkRecordById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuPlanFormWorkRecord getFormWorkRecordById(String recordId) throws Exception {
        BuPlanFormWorkRecord planFormWorkRecord = buPlanFormWorkRecordQualityMapper.selectFormWorkRecordById(recordId);

        setDetailCheck(planFormWorkRecord);
        setDetailCategory(planFormWorkRecord);

        return planFormWorkRecord;
    }

    /**
     * @see BuPlanFormWorkRecordQualityService#getFormDataRecordById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuPlanFormDataRecord getFormDataRecordById(String dataFormId) throws Exception {
        return buPlanFormDataRecordQualityMapper.selectDataRecordWithValuesById(dataFormId);
    }

    /**
     * @see BuPlanFormWorkRecordQualityService#checkFormWorkRecord(BuFormWorkRecordCheckVO)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean checkFormWorkRecord(BuFormWorkRecordCheckVO checkVO) throws Exception {
        BuPlanFormWorkRecord check = new BuPlanFormWorkRecord();
        BeanUtils.copyProperties(checkVO, check);
        buPlanFormWorkRecordQualityMapper.updateById(check);

        return true;
    }


    private void setDetailCheck(BuPlanFormWorkRecord planFormWorkRecord) {
        if (planFormWorkRecord == null) {
            return;
        }
        List<BuPlanFormWorkRecordDetail> detailList = planFormWorkRecord.getDetailList();
        if (CollectionUtils.isEmpty(detailList)) {
            return;
        }

        for (BuPlanFormWorkRecordDetail detail : detailList) {
            List<BuPlanFormWorkRecordChecks> checksList = detail.getChecksList();
            if (CollectionUtils.isEmpty(checksList)) {
                detail.setChecksList(new ArrayList<>());
                continue;
            }

            for (BuPlanFormWorkRecordChecks check : checksList) {
                Integer checkType = check.getCheckType();
                switch (checkType) {
                    case 1:
                        detail.setSelfCheck(check.getCheckUserName());
                        break;
                    case 2:
                        detail.setGuarderCheck(check.getCheckUserName());
                        break;
                    case 3:
                        detail.setMonitorCheck(check.getCheckUserName());
                        break;
                    case 4:
                        detail.setMonitorAcceptance(check.getCheckUserName());
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void setDetailCategory(BuPlanFormWorkRecord planFormWorkRecord) {
        if (planFormWorkRecord == null) {
            return;
        }

        List<BuPlanFormWorkRecordDetail> workRecordDetailList = planFormWorkRecord.getDetailList();
        Map<String, List<BuPlanFormWorkRecordDetail>> categoryIdDetailListMap = workRecordDetailList.stream().collect(Collectors.groupingBy(BuPlanFormWorkRecordDetail::getCategoryId));

        List<BuWorkRecordCategoryBO> categoryList = new ArrayList<>();
        for (Map.Entry<String, List<BuPlanFormWorkRecordDetail>> categoryIdDetailListEntry : categoryIdDetailListMap.entrySet()) {
            String categoryId = categoryIdDetailListEntry.getKey();
            List<BuPlanFormWorkRecordDetail> detailList = categoryIdDetailListEntry.getValue();

            detailList.sort(Comparator.comparing(BuPlanFormWorkRecordDetail::getItemNo, Comparator.nullsLast(Comparator.naturalOrder())));
            BuWorkRecordCategoryBO categoryBO = new BuWorkRecordCategoryBO()
                    .setId(categoryId)
                    .setRecIndex(detailList.get(0).getRecIndex())
                    .setReguTitle(detailList.get(0).getReguTitle())
                    .setDetailList(detailList);
            categoryList.add(categoryBO);
        }
        categoryList.sort(Comparator.comparing(BuWorkRecordCategoryBO::getRecIndex, Comparator.nullsLast(Comparator.naturalOrder())));
        planFormWorkRecord.setCategoryList(categoryList);
        planFormWorkRecord.setDetailList(new ArrayList<>());
    }

}
