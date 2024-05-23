package org.jeecg.modules.basemanage.techbook.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.basemanage.regu.bean.BuRepairReguTechBookDetail;
import org.jeecg.modules.basemanage.techbook.bean.BuRepairTechBookDetail;
import org.jeecg.modules.basemanage.techbook.bean.BuRepairTechBookDetailMaterial;
import org.jeecg.modules.basemanage.techbook.bean.BuRepairTechBookDetailTools;
import org.jeecg.modules.basemanage.regu.mapper.BuRepairReguTechBookDetailMapper;
import org.jeecg.modules.basemanage.techbook.mapper.BuRepairTechBookDetailMapper;
import org.jeecg.modules.basemanage.techbook.mapper.BuRepairTechBookDetailMaterialMapper;
import org.jeecg.modules.basemanage.techbook.mapper.BuRepairTechBookDetailToolsMapper;
import org.jeecg.modules.basemanage.techbook.service.BuRepairTechBookDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 作业指导书(工艺)明细 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
@Service
public class BuRepairTechBookDetailServiceImpl extends ServiceImpl<BuRepairTechBookDetailMapper, BuRepairTechBookDetail> implements BuRepairTechBookDetailService {

    @Resource
    private BuRepairTechBookDetailMapper buRepairTechBookDetailMapper;
    @Resource
    private BuRepairTechBookDetailMaterialMapper buRepairTechBookDetailMaterialMapper;
    @Resource
    private BuRepairTechBookDetailToolsMapper buRepairTechBookDetailToolsMapper;
    @Resource
    private BuRepairReguTechBookDetailMapper buRepairReguTechBookDetailMapper;


    /**
     * @see BuRepairTechBookDetailService#listDetail(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuRepairTechBookDetail> listDetail(String bookId) throws Exception {
        LambdaQueryWrapper<BuRepairTechBookDetail> wrapper = new LambdaQueryWrapper<BuRepairTechBookDetail>()
                .select(BuRepairTechBookDetail::getId, BuRepairTechBookDetail::getBookId,
                        BuRepairTechBookDetail::getStepTitle, BuRepairTechBookDetail::getStepNum)
                .eq(BuRepairTechBookDetail::getBookId, bookId)
                .orderByAsc(BuRepairTechBookDetail::getStepNum);
        return buRepairTechBookDetailMapper.selectList(wrapper);
    }

    /**
     * @see BuRepairTechBookDetailService#listDetail(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuRepairTechBookDetail getDetailAndRelation(String bookDetailId) throws Exception {
        BuRepairTechBookDetail detail = buRepairTechBookDetailMapper.selectById(bookDetailId);

        List<BuRepairTechBookDetailMaterial> materialList = buRepairTechBookDetailMaterialMapper.selectListByDetailId(bookDetailId);
        List<BuRepairTechBookDetailTools> toolList = buRepairTechBookDetailToolsMapper.selectListByDetailId(bookDetailId);

        detail.setMaterialList(materialList)
                .setToolList(toolList);

        return detail;
    }

    /**
     * @see BuRepairTechBookDetailService#saveDetail(BuRepairTechBookDetail)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveDetail(BuRepairTechBookDetail detail) throws Exception {
        if (StringUtils.isNotBlank(detail.getId())) {
            // 删除明细关联信息
            deleteDetailRelation(Collections.singletonList(detail.getId()));
            // 删除明细
            buRepairTechBookDetailMapper.deleteBatchIds(Collections.singletonList(detail.getId()));
        }

        // 插入明细
        buRepairTechBookDetailMapper.insert(detail);
        // 插入明细关联信息
        insertDetailRelation(detail);

        return true;
    }

    /**
     * @see BuRepairTechBookDetailService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> detailIdList = Arrays.asList(ids.split(","));

        // 检查是否关联规程
        checkReguRelation(detailIdList);

        // 删除明细关联信息
        deleteDetailRelation(detailIdList);
        // 删除明细
        buRepairTechBookDetailMapper.deleteBatchIds(detailIdList);

        return true;
    }


    private void checkReguRelation(List<String> detailIdList) {
        if (CollectionUtils.isEmpty(detailIdList)) {
            return;
        }

        LambdaQueryWrapper<BuRepairReguTechBookDetail> reguTechBookQueryWrapper = new LambdaQueryWrapper<BuRepairReguTechBookDetail>()
                .in(BuRepairReguTechBookDetail::getBookDetailId, detailIdList);
        Integer reguRelationCount = buRepairReguTechBookDetailMapper.selectCount(reguTechBookQueryWrapper);
        if (reguRelationCount > 0) {
            throw new JeecgBootException("作业指导书明细已关联规程，不能删除");
        }
    }

    private void deleteDetailRelation(List<String> detailIdList) {
        if (CollectionUtils.isEmpty(detailIdList)) {
            return;
        }

        LambdaQueryWrapper<BuRepairTechBookDetailMaterial> detailMaterialWrapper = new LambdaQueryWrapper<BuRepairTechBookDetailMaterial>()
                .in(BuRepairTechBookDetailMaterial::getBookDetailId, detailIdList);
        buRepairTechBookDetailMaterialMapper.delete(detailMaterialWrapper);
        LambdaQueryWrapper<BuRepairTechBookDetailTools> detailToolsWrapper = new LambdaQueryWrapper<BuRepairTechBookDetailTools>()
                .in(BuRepairTechBookDetailTools::getBookDetailId, detailIdList);
        buRepairTechBookDetailToolsMapper.delete(detailToolsWrapper);
    }

    private void insertDetailRelation(BuRepairTechBookDetail detail) {
        if (null == detail) {
            return;
        }

        String detailId = detail.getId();

        List<BuRepairTechBookDetailMaterial> materialList = detail.getMaterialList();
        if (CollectionUtils.isNotEmpty(materialList)) {
            for (BuRepairTechBookDetailMaterial material : materialList) {
                material.setBookDetailId(detailId);
                if (null == material.getAmount()) {
                    material.setAmount(0D);
                }
            }
            buRepairTechBookDetailMaterialMapper.insertList(materialList);
        }

        List<BuRepairTechBookDetailTools> toolList = detail.getToolList();
        if (CollectionUtils.isNotEmpty(toolList)) {
            for (BuRepairTechBookDetailTools tool : toolList) {
                tool.setBookDetailId(detailId);
                if (null == tool.getAmount()) {
                    tool.setAmount(0D);
                }
            }
            buRepairTechBookDetailToolsMapper.insertList(toolList);
        }
    }

}
