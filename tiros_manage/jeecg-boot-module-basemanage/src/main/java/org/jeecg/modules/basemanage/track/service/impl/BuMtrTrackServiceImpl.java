package org.jeecg.modules.basemanage.track.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.basemanage.track.entity.BuMtrTrack;
import org.jeecg.modules.basemanage.track.entity.vo.BuMtrTrackQueryVO;
import org.jeecg.modules.basemanage.track.mapper.BuMtrTrackMapper;
import org.jeecg.modules.basemanage.track.service.IBuMtrTrackService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 股道信息 服务实现类
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-10
 */
@Slf4j
@Service
public class BuMtrTrackServiceImpl extends ServiceImpl<BuMtrTrackMapper, BuMtrTrack> implements IBuMtrTrackService {

    @Resource
    private BuMtrTrackMapper buMtrTrackMapper;

    /**
     * @see IBuMtrTrackService#selectTrackPage(Page, BuMtrTrackQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public Page<BuMtrTrack> selectTrackPage(Page<BuMtrTrack> page, BuMtrTrackQueryVO track) {
        return page.setRecords(buMtrTrackMapper.selectTrackPage(page, track));
    }

    /**
     * @see IBuMtrTrackService#saveTrack(BuMtrTrack)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveTrack(BuMtrTrack buMtrTrack) throws Exception {
        if (isCodeRepeated(buMtrTrack)) {
            throw new JeecgBootException("编码重复");
        }

        buMtrTrack.setName(buMtrTrack.getCode());
        buMtrTrackMapper.insert(buMtrTrack);

        return true;
    }

    /**
     * @see IBuMtrTrackService#updateTrack(BuMtrTrack)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateTrack(BuMtrTrack buMtrTrack) throws Exception {
        if (isCodeRepeated(buMtrTrack)) {
            throw new JeecgBootException("编码重复");
        }

        buMtrTrack.setName(buMtrTrack.getCode());
        buMtrTrackMapper.updateById(buMtrTrack);

        return true;
    }

    /**
     * @see IBuMtrTrackService#deleteBatchByTrackIds(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatchByTrackIds(String ids) {
        if (StringUtils.isBlank(ids)) {
            throw new JeecgBootException("请选择要删除的股道");
        }

        List<String> idList = Arrays.asList(ids.split(","));
        buMtrTrackMapper.deleteBatchIds(idList);

        return true;
    }

    private boolean isCodeRepeated(BuMtrTrack buMtrTrack) {
        LambdaQueryWrapper<BuMtrTrack> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BuMtrTrack::getCode, buMtrTrack.getCode());
        List<BuMtrTrack> buMtrTrackList = buMtrTrackMapper.selectList(wrapper);

        if (CollectionUtils.isEmpty(buMtrTrackList)) {
            return false;
        }
        if (StringUtils.isBlank(buMtrTrack.getId())) {
            return true;
        }
        return !buMtrTrack.getId().equals(buMtrTrackList.get(0).getId());
    }

}
