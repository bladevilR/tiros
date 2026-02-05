package org.jeecg.modules.basemanage.productionnotice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.basemanage.productionnotice.entity.BuProductionNotice;
import org.jeecg.modules.basemanage.productionnotice.entity.vo.BuProductionNoticeQueryVO;
import org.jeecg.modules.basemanage.productionnotice.mapper.BuProductionNoticeMapper;
import org.jeecg.modules.basemanage.productionnotice.service.IBuProductionNoticeService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;

@Service
public class BuProductionNoticeServiceImpl extends ServiceImpl<BuProductionNoticeMapper, BuProductionNotice> implements IBuProductionNoticeService {

    @Override
    public IPage<BuProductionNotice> queryPage(BuProductionNoticeQueryVO queryVO, Integer pageNo, Integer pageSize) {
        QueryWrapper<BuProductionNotice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", 0);

        if (StringUtils.isNotBlank(queryVO.getNoticeNo())) {
            queryWrapper.like("notice_no", queryVO.getNoticeNo());
        }
        if (StringUtils.isNotBlank(queryVO.getTitle())) {
            queryWrapper.like("title", queryVO.getTitle());
        }
        if (StringUtils.isNotBlank(queryVO.getNoticeType())) {
            queryWrapper.eq("notice_type", queryVO.getNoticeType());
        }
        if (StringUtils.isNotBlank(queryVO.getStatus())) {
            queryWrapper.eq("status", queryVO.getStatus());
        }
        if (StringUtils.isNotBlank(queryVO.getTrainType())) {
            queryWrapper.like("train_type", queryVO.getTrainType());
        }
        if (StringUtils.isNotBlank(queryVO.getLine())) {
            queryWrapper.like("line", queryVO.getLine());
        }

        queryWrapper.orderByDesc("create_time");

        Page<BuProductionNotice> page = new Page<>(pageNo, pageSize);
        return this.page(page, queryWrapper);
    }

    @Override
    public boolean saveNotice(BuProductionNotice notice) {
        notice.setCreateTime(new Date());
        notice.setDelFlag(0);
        return this.save(notice);
    }

    @Override
    public boolean updateNotice(BuProductionNotice notice) {
        notice.setUpdateTime(new Date());
        return this.updateById(notice);
    }

    @Override
    public boolean deleteNotice(String ids) {
        if (StringUtils.isBlank(ids)) {
            return false;
        }
        String[] idArray = ids.split(",");
        return this.removeByIds(Arrays.asList(idArray));
    }
}
