package org.jeecg.modules.quality.rectify.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.modules.quality.rectify.bean.BuWorkRectify;
import org.jeecg.modules.quality.rectify.bean.BuWorkRectifyAnnex;
import org.jeecg.modules.quality.rectify.bean.vo.BuWorkRectifyQueryVO;
import org.jeecg.modules.quality.rectify.mapper.BuWorkRectifyAnnexMapper;
import org.jeecg.modules.quality.rectify.mapper.BuWorkRectifyMapper;
import org.jeecg.modules.quality.rectify.service.BuWorkRectifyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 整改信息 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-15
 */
@Slf4j
@Service
public class BuWorkRectifyServiceImpl extends ServiceImpl<BuWorkRectifyMapper, BuWorkRectify> implements BuWorkRectifyService {

    @Resource
    private BuWorkRectifyMapper buWorkRectifyMapper;
    @Resource
    private BuWorkRectifyAnnexMapper buWorkRectifyAnnexMapper;

    /**
     * @see BuWorkRectifyService#pageRectify(BuWorkRectifyQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuWorkRectify> pageRectify(BuWorkRectifyQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buWorkRectifyMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuWorkRectifyService#getRectifyById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuWorkRectify getRectifyById(String id) throws Exception {
        return buWorkRectifyMapper.selectRectifyById(id);
    }

    /**
     * @see BuWorkRectifyService#saveRectify(BuWorkRectify)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveRectify(BuWorkRectify buWorkRectify) throws Exception {
        // 保存整改信息
        buWorkRectifyMapper.insert(buWorkRectify);
        String rectifyId = buWorkRectify.getId();

        // 保存整改附件
        List<BuWorkRectifyAnnex> annexList = buWorkRectify.getAnnexList();
        if (CollectionUtils.isNotEmpty(annexList)) {
            for (BuWorkRectifyAnnex rectifyAnnex : annexList) {
                rectifyAnnex.setRectifyId(rectifyId);
                buWorkRectifyAnnexMapper.insert(rectifyAnnex);
            }
        }

        return true;
    }

    /**
     * @see BuWorkRectifyService#updateRectify(BuWorkRectify)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateRectify(BuWorkRectify buWorkRectify) throws Exception {
        String rectifyId = buWorkRectify.getId();

        // 删除原来的整改附件
        LambdaQueryWrapper<BuWorkRectifyAnnex> annexWrapper = new LambdaQueryWrapper<>();
        annexWrapper.eq(BuWorkRectifyAnnex::getRectifyId, rectifyId);
        buWorkRectifyAnnexMapper.delete(annexWrapper);

        // 更新整改信息
        buWorkRectifyMapper.updateById(buWorkRectify);

        // 重新保存整改附件
        List<BuWorkRectifyAnnex> annexList = buWorkRectify.getAnnexList();
        if (CollectionUtils.isNotEmpty(annexList)) {
            for (BuWorkRectifyAnnex rectifyAnnex : annexList) {
                rectifyAnnex.setRectifyId(rectifyId);
                buWorkRectifyAnnexMapper.insert(rectifyAnnex);
            }
        }

        return true;
    }

    /**
     * @see BuWorkRectifyService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));

        // 删除整改附件
        LambdaQueryWrapper<BuWorkRectifyAnnex> annexWrapper = new LambdaQueryWrapper<BuWorkRectifyAnnex>()
                .in(BuWorkRectifyAnnex::getRectifyId, idList);
        buWorkRectifyAnnexMapper.delete(annexWrapper);

        // 删除整改信息
        buWorkRectifyMapper.deleteBatchIds(idList);

        return true;
    }

    /**
     * @see BuWorkRectifyService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean closeBatch(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));

        LambdaQueryWrapper<BuWorkRectify> wrapper = new LambdaQueryWrapper<BuWorkRectify>()
                .in(BuWorkRectify::getId, idList);
        BuWorkRectify closeRectify = new BuWorkRectify()
                .setStatus(3);
        buWorkRectifyMapper.update(closeRectify, wrapper);

        return true;
    }

}
