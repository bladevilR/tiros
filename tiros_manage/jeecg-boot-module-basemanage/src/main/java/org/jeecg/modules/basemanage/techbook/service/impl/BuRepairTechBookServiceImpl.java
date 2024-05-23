package org.jeecg.modules.basemanage.techbook.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.basemanage.regu.bean.BuRepairReguTechBookDetail;
import org.jeecg.modules.basemanage.regu.mapper.BuRepairReguTechBookDetailMapper;
import org.jeecg.modules.basemanage.techbook.bean.*;
import org.jeecg.modules.basemanage.techbook.bean.vo.BuRepairTechBookQueryVO;
import org.jeecg.modules.basemanage.techbook.mapper.*;
import org.jeecg.modules.basemanage.techbook.service.BuRepairTechBookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 作业指导书(工艺) 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
@Service
public class BuRepairTechBookServiceImpl extends ServiceImpl<BuRepairTechBookMapper, BuRepairTechBook> implements BuRepairTechBookService {

    @Resource
    private BuRepairTechBookMapper buRepairTechBookMapper;
    @Resource
    private BuRepairTechBookDetailMapper buRepairTechBookDetailMapper;
    @Resource
    private BuRepairTechBookDetailMaterialMapper buRepairTechBookDetailMaterialMapper;
    @Resource
    private BuRepairTechBookDetailToolsMapper buRepairTechBookDetailToolsMapper;
    @Resource
    private BuRepairReguTechBookDetailMapper buRepairReguTechBookDetailMapper;


    /**
     * @see BuRepairTechBookService
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuRepairTechBook> pageTechBook(BuRepairTechBookQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buRepairTechBookMapper.selectPageBySearchText(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuRepairTechBookService#getTechBookById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuRepairTechBook getTechBookById(String id) throws Exception {
        return buRepairTechBookMapper.selectTechBookById(id);
    }

    /**
     * @see BuRepairTechBookService#saveTechBook(BuRepairTechBook)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String saveTechBook(BuRepairTechBook techBook) throws Exception {
        if (StringUtils.isBlank(techBook.getId())) {
            String id = UUIDGenerator.generate();
            techBook.setId(id);
            buRepairTechBookMapper.insert(techBook);
            return id;
        } else {
            buRepairTechBookMapper.updateById(techBook);
            return techBook.getId();
        }
    }

    /**
     * @see BuRepairTechBookService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));

        // 检查是否关联规程
        checkReguRelation(idList);

        LambdaQueryWrapper<BuRepairTechBookDetail> detailWrapper = new LambdaQueryWrapper<BuRepairTechBookDetail>()
                .in(BuRepairTechBookDetail::getBookId, idList);
        List<BuRepairTechBookDetail> detailList = buRepairTechBookDetailMapper.selectList(detailWrapper);
        if (CollectionUtils.isNotEmpty(detailList)) {
            List<String> detailIdList = detailList.stream()
                    .map(BuRepairTechBookDetail::getId)
                    .collect(Collectors.toList());
            // 删除明细关联信息
            deleteDetailRelation(detailIdList);
            // 删除明细
            buRepairTechBookDetailMapper.delete(detailWrapper);
        }

        // 删除指导书
        buRepairTechBookMapper.deleteBatchIds(idList);

        return true;
    }


    private void checkReguRelation(List<String> idList) {
        LambdaQueryWrapper<BuRepairReguTechBookDetail> reguTechBookQueryWrapper = new LambdaQueryWrapper<BuRepairReguTechBookDetail>()
                .in(BuRepairReguTechBookDetail::getTechBookId, idList);
        Integer reguRelationCount = buRepairReguTechBookDetailMapper.selectCount(reguTechBookQueryWrapper);
        if (reguRelationCount > 0) {
            throw new JeecgBootException("作业指导书已关联规程，不能删除");
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

}
