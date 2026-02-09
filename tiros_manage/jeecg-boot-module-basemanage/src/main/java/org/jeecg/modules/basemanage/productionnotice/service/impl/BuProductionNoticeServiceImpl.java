package org.jeecg.modules.basemanage.productionnotice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.basemanage.productionnotice.entity.BuProductionNotice;
import org.jeecg.modules.basemanage.productionnotice.entity.vo.BuProductionNoticeQueryVO;
import org.jeecg.modules.basemanage.productionnotice.mapper.BuProductionNoticeMapper;
import org.jeecg.modules.basemanage.productionnotice.service.IBuProductionNoticeService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
        if (notice == null) {
            throw new JeecgBootException("参数不能为空");
        }
        notice.setCreateTime(new Date());
        if (StringUtils.isBlank(notice.getStatus())) {
            notice.setStatus("0");
        }
        if (notice.getDelFlag() == null) {
            notice.setDelFlag(0);
        }
        return this.save(notice);
    }

    @Override
    public boolean updateNotice(BuProductionNotice notice) {
        if (notice == null || StringUtils.isBlank(notice.getId())) {
            throw new JeecgBootException("通知单ID不能为空");
        }
        notice.setUpdateTime(new Date());
        return this.updateById(notice);
    }

    @Override
    public boolean deleteNotice(String ids) {
        List<String> idArray = parseIdList(ids);
        if (idArray.isEmpty()) {
            throw new JeecgBootException("通知单ID不能为空");
        }
        return this.removeByIds(idArray);
    }

    private List<String> parseIdList(String ids) {
        if (StringUtils.isBlank(ids)) {
            return Collections.emptyList();
        }
        return Arrays.stream(ids.split(","))
                .map(StringUtils::trimToNull)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
    }
}
