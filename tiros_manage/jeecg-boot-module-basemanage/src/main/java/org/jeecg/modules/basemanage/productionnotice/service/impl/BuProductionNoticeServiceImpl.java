package org.jeecg.modules.basemanage.productionnotice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.basemanage.productionnotice.entity.BuProductionNoticeOrderRel;
import org.jeecg.modules.basemanage.productionnotice.entity.BuProductionNotice;
import org.jeecg.modules.basemanage.productionnotice.entity.vo.BuProductionNoticeProgressDetailVO;
import org.jeecg.modules.basemanage.productionnotice.entity.vo.BuProductionNoticeQueryVO;
import org.jeecg.modules.basemanage.productionnotice.mapper.BuProductionNoticeMapper;
import org.jeecg.modules.basemanage.productionnotice.mapper.BuProductionNoticeOrderRelMapper;
import org.jeecg.modules.basemanage.productionnotice.service.IBuProductionNoticeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BuProductionNoticeServiceImpl extends ServiceImpl<BuProductionNoticeMapper, BuProductionNotice> implements IBuProductionNoticeService {

    private final BuProductionNoticeOrderRelMapper noticeOrderRelMapper;

    public BuProductionNoticeServiceImpl(BuProductionNoticeOrderRelMapper noticeOrderRelMapper) {
        this.noticeOrderRelMapper = noticeOrderRelMapper;
    }

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
        validateNoticeType(notice);
        validateNoticeStatusForCreate(notice.getStatus());
        checkNoticeNoUnique(notice.getNoticeNo(), null);
        notice.setCreateTime(new Date());
        if (StringUtils.isBlank(notice.getStatus())) {
            notice.setStatus("0");
        }
        if (notice.getDelFlag() == null) {
            notice.setDelFlag(0);
        }
        normalizeNoticeAfterProgressUpdate(notice, 0, safeInt(notice.getTotalTrains()));
        return this.save(notice);
    }

    @Override
    public boolean updateNotice(BuProductionNotice notice) {
        if (notice == null || StringUtils.isBlank(notice.getId())) {
            throw new JeecgBootException("通知单ID不能为空");
        }
        validateNoticeType(notice);
        BuProductionNotice db = this.getById(notice.getId());
        if (db == null || Integer.valueOf(1).equals(db.getDelFlag())) {
            throw new JeecgBootException("通知单不存在");
        }
        checkNoticeNoUnique(notice.getNoticeNo(), notice.getId());
        guardManualStatusChange(db, notice);
        if ("2".equals(db.getStatus()) || "3".equals(db.getStatus())) {
            if (notice.getTotalTrains() != null && !Objects.equals(db.getTotalTrains(), notice.getTotalTrains())) {
                throw new JeecgBootException("已发布/已关闭通知单不允许修改涉及列车总数");
            }
        }

        notice.setUpdateTime(new Date());
        boolean updated = this.updateById(notice);
        if (!updated) {
            return false;
        }
        refreshProgress(notice.getId());
        return true;
    }

    @Override
    public boolean deleteNotice(String ids) {
        List<String> idArray = parseIdList(ids);
        if (idArray.isEmpty()) {
            throw new JeecgBootException("通知单ID不能为空");
        }
        java.util.Collection<BuProductionNotice> notices = this.listByIds(idArray);
        if (notices.stream().anyMatch(item -> "1".equals(item.getStatus()) || "2".equals(item.getStatus()))) {
            throw new JeecgBootException("审核中或已发布的通知单不允许删除");
        }
        return this.removeByIds(idArray);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitNotice(String id) {
        BuProductionNotice notice = getAndCheckExists(id);
        if (!"0".equals(notice.getStatus())) {
            throw new JeecgBootException("仅草稿状态可以提交审核");
        }
        notice.setStatus("1");
        notice.setUpdateTime(new Date());
        return this.updateById(notice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishNotice(String id) {
        BuProductionNotice notice = getAndCheckExists(id);
        if (!"1".equals(notice.getStatus())) {
            throw new JeecgBootException("仅审核中状态可以发布");
        }
        notice.setStatus("2");
        notice.setPublishTime(new Date());
        notice.setUpdateTime(new Date());
        if (StringUtils.isBlank(notice.getProgress())) {
            notice.setProgress("0%");
        }
        boolean updated = this.updateById(notice);
        if (updated) {
            refreshProgress(id);
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean closeNotice(String id) {
        BuProductionNotice notice = getAndCheckExists(id);
        if (!"2".equals(notice.getStatus())) {
            throw new JeecgBootException("仅已发布状态可以关闭");
        }
        refreshProgress(id);
        BuProductionNotice latest = getAndCheckExists(id);
        if (!"100%".equals(latest.getProgress())) {
            throw new JeecgBootException("通知单执行进度未完成，不能关闭");
        }
        latest.setStatus("3");
        latest.setUpdateTime(new Date());
        return this.updateById(latest);
    }

    @Override
    public List<BuProductionNotice> listPendingTechnicalNotices(String lineId, String trainNo) {
        QueryWrapper<BuProductionNotice> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag", 0)
                .eq("notice_type", "1")
                .eq("status", "2");
        if (StringUtils.isNotBlank(lineId)) {
            wrapper.and(w -> w.like("line", lineId).or().isNull("line"));
        }
        wrapper.orderByDesc("publish_time").orderByDesc("create_time");
        List<BuProductionNotice> notices = this.list(wrapper);
        if (StringUtils.isBlank(trainNo)) {
            return notices;
        }

        return notices.stream().filter(item -> {
            Integer count = baseMapper.countActiveBindingByTrain(item.getId(), trainNo);
            return count == null || count == 0;
        }).collect(Collectors.toList());
    }

    @Override
    public List<BuProductionNoticeProgressDetailVO> listProgressDetails(String id) {
        getAndCheckExists(id);
        return baseMapper.listProgressDetails(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void bindOrder(String noticeId, String orderId, String orderCode, String trainNo) {
        if (StringUtils.isAnyBlank(noticeId, orderId, orderCode, trainNo)) {
            throw new JeecgBootException("生产通知单绑定工单参数不完整");
        }

        BuProductionNotice notice = getAndCheckExists(noticeId);
        if (!"1".equals(notice.getNoticeType())) {
            throw new JeecgBootException("仅技术类生产通知单可以绑定工单");
        }
        if (!"2".equals(notice.getStatus())) {
            throw new JeecgBootException("仅已发布状态的技术通知单可以绑定工单");
        }

        QueryWrapper<BuProductionNoticeOrderRel> existWrapper = new QueryWrapper<>();
        existWrapper.eq("notice_id", noticeId)
                .eq("order_id", orderId)
                .eq("del_flag", 0);
        Integer count = noticeOrderRelMapper.selectCount(existWrapper);
        if (count != null && count > 0) {
            return;
        }

        BuProductionNoticeOrderRel rel = new BuProductionNoticeOrderRel();
        rel.setNoticeId(noticeId);
        rel.setOrderId(orderId);
        rel.setOrderCode(orderCode);
        rel.setTrainNo(trainNo);
        rel.setDelFlag(0);
        rel.setCreateTime(new Date());
        rel.setUpdateTime(new Date());
        noticeOrderRelMapper.insert(rel);
        refreshProgress(noticeId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void refreshProgressByOrder(String orderId, String trainNo) {
        if (StringUtils.isBlank(orderId)) {
            return;
        }
        QueryWrapper<BuProductionNoticeOrderRel> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderId)
                .eq("del_flag", 0);
        List<BuProductionNoticeOrderRel> rels = noticeOrderRelMapper.selectList(wrapper);
        if (rels.isEmpty()) {
            return;
        }
        rels.stream().map(BuProductionNoticeOrderRel::getNoticeId).distinct().forEach(this::refreshProgress);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void unbindOrder(String orderId) {
        if (StringUtils.isBlank(orderId)) {
            return;
        }
        List<BuProductionNoticeOrderRel> rels = noticeOrderRelMapper.selectList(new QueryWrapper<BuProductionNoticeOrderRel>()
                .eq("order_id", orderId)
                .eq("del_flag", 0));
        if (rels.isEmpty()) {
            return;
        }

        Date now = new Date();
        UpdateWrapper<BuProductionNoticeOrderRel> wrapper = new UpdateWrapper<>();
        wrapper.eq("order_id", orderId)
                .eq("del_flag", 0)
                .set("del_flag", 1)
                .set("update_time", now);
        noticeOrderRelMapper.update(null, wrapper);

        rels.stream().map(BuProductionNoticeOrderRel::getNoticeId).distinct().forEach(this::refreshProgress);
    }

    @Transactional(rollbackFor = Exception.class)
    public void refreshProgress(String noticeId) {
        BuProductionNotice notice = this.getById(noticeId);
        if (notice == null || Integer.valueOf(1).equals(notice.getDelFlag())) {
            return;
        }

        Integer closedCount = defaultZero(baseMapper.countClosedTrainByNoticeId(noticeId));
        Integer boundCount = defaultZero(baseMapper.countBoundTrainByNoticeId(noticeId));
        int total = safeInt(notice.getTotalTrains());
        if (total <= 0) {
            total = boundCount;
        }

        normalizeNoticeAfterProgressUpdate(notice, closedCount, total);
        notice.setUpdateTime(new Date());
        this.updateById(notice);
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

    private void validateNoticeType(BuProductionNotice notice) {
        if (notice == null) {
            return;
        }
        if (StringUtils.isBlank(notice.getNoticeType())) {
            throw new JeecgBootException("通知类型不能为空");
        }
        if (!"1".equals(notice.getNoticeType()) && !"2".equals(notice.getNoticeType())) {
            throw new JeecgBootException("通知类型不正确");
        }
    }

    private void validateNoticeStatusForCreate(String status) {
        if (StringUtils.isBlank(status)) {
            return;
        }
        if (!"0".equals(status)) {
            throw new JeecgBootException("新增通知单仅允许草稿状态");
        }
    }

    private void checkNoticeNoUnique(String noticeNo, String excludeId) {
        if (StringUtils.isBlank(noticeNo)) {
            throw new JeecgBootException("通知单号不能为空");
        }
        QueryWrapper<BuProductionNotice> wrapper = new QueryWrapper<>();
        wrapper.eq("notice_no", noticeNo)
                .eq("del_flag", 0);
        if (StringUtils.isNotBlank(excludeId)) {
            wrapper.ne("id", excludeId);
        }
        Integer count = this.baseMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            throw new JeecgBootException("通知单号已存在");
        }
    }

    private void guardManualStatusChange(BuProductionNotice db, BuProductionNotice update) {
        if (update == null || StringUtils.isBlank(update.getStatus())) {
            return;
        }
        if (!Objects.equals(db.getStatus(), update.getStatus())) {
            throw new JeecgBootException("通知单状态不允许在编辑接口直接修改，请使用提交/发布/关闭操作");
        }
    }

    private BuProductionNotice getAndCheckExists(String id) {
        if (StringUtils.isBlank(id)) {
            throw new JeecgBootException("通知单ID不能为空");
        }
        BuProductionNotice notice = this.getById(id);
        if (notice == null || Integer.valueOf(1).equals(notice.getDelFlag())) {
            throw new JeecgBootException("通知单不存在");
        }
        return notice;
    }

    private void normalizeNoticeAfterProgressUpdate(BuProductionNotice notice, int completed, int total) {
        int normalizedTotal = Math.max(total, 0);
        int normalizedCompleted = Math.max(completed, 0);
        if (normalizedTotal > 0) {
            normalizedCompleted = Math.min(normalizedCompleted, normalizedTotal);
        }
        String progress;
        if (normalizedTotal <= 0) {
            progress = "0%";
        } else {
            int percentage = Math.round((normalizedCompleted * 100.0f) / normalizedTotal);
            progress = percentage + "%";
        }

        notice.setTotalTrains(normalizedTotal);
        notice.setCompletedTrains(normalizedCompleted);
        notice.setProgress(progress);
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : Math.max(value, 0);
    }

    private Integer defaultZero(Integer value) {
        return value == null ? 0 : value;
    }
}
