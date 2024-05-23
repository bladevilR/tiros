package org.jeecg.modules.basemanage.workcheck.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.basemanage.workcheck.bean.BuWorkCheck;
import org.jeecg.modules.basemanage.workcheck.bean.BuWorkCheckItem;
import org.jeecg.modules.basemanage.workcheck.bean.BuWorkCheckTechLink;
import org.jeecg.modules.basemanage.workcheck.bean.vo.BuWorkCheckQueryVO;
import org.jeecg.modules.basemanage.workcheck.mapper.BuWorkCheckItemMapper;
import org.jeecg.modules.basemanage.workcheck.mapper.BuWorkCheckMapper;
import org.jeecg.modules.basemanage.workcheck.mapper.BuWorkCheckTechLinkMapper;
import org.jeecg.modules.basemanage.workcheck.service.BuWorkCheckService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2021-05-17
 */
@Service
public class BuWorkCheckServiceImpl extends ServiceImpl<BuWorkCheckMapper, BuWorkCheck> implements BuWorkCheckService {

    @Resource
    private BuWorkCheckMapper buWorkCheckMapper;
    @Resource
    private BuWorkCheckItemMapper buWorkCheckItemMapper;
    @Resource
    private BuWorkCheckTechLinkMapper buWorkCheckTechLinkMapper;


    /**
     * @see BuWorkCheckService#selectPage(Page, BuWorkCheckQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public Page<BuWorkCheck> selectPage(Page<BuWorkCheck> page, BuWorkCheckQueryVO queryVO) {
        Page<BuWorkCheck> workCheckPage = buWorkCheckMapper.selectWorkCheckPage(page, queryVO);
        setFormTypeAndWorkRecordType(workCheckPage.getRecords());
        return workCheckPage;
    }

    /**
     * @see BuWorkCheckService#getWorkCheckById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuWorkCheck getWorkCheckById(String id) throws Exception {
        BuWorkCheck workCheck = buWorkCheckMapper.selectWorkCheckById(id);
        setFormTypeAndWorkRecordType(Collections.singletonList(workCheck));
        return workCheck;
    }

    /**
     * @see BuWorkCheckService#saveOrUpdateWorkCheck(BuWorkCheck)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrUpdateWorkCheck(BuWorkCheck workCheck) throws Exception {
        if (StringUtils.isNotBlank(workCheck.getId())) {
            buWorkCheckMapper.updateById(workCheck);
            deleteOtherInfos(Collections.singletonList(workCheck.getId()));
        } else {
            workCheck.setId(UUIDGenerator.generate());
            workCheck.setCode("JCB" + RandomUtil.randomNumbers(5));
            buWorkCheckMapper.insert(workCheck);
        }

        insertOtherInfos(workCheck);

        return true;
    }

    /**
     * @see BuWorkCheckService#deleteBatchWorkCheck(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatchWorkCheck(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));

        deleteOtherInfos(idList);
        buWorkCheckMapper.deleteBatchIds(idList);

        return true;
    }


    private void setFormTypeAndWorkRecordType(List<BuWorkCheck> workCheckList) {
        if (CollectionUtils.isEmpty(workCheckList)) {
            return;
        }

        for (BuWorkCheck workCheck : workCheckList) {
            workCheck.setFormType(4)
                    .setWorkRecordType(0);
        }
    }

    private void insertOtherInfos(BuWorkCheck check) {
        String checkId = check.getId();

        List<BuWorkCheckItem> itemList = check.getItemList();
        if (CollectionUtils.isNotEmpty(itemList)) {
            for (BuWorkCheckItem item : itemList) {
                item.setCheckId(checkId);
                if (null == item.getCheckResult()) {
                    item.setCheckResult(1);
                }
            }
            buWorkCheckItemMapper.insertList(itemList);
        }

        List<BuWorkCheckTechLink> techLinkList = check.getTechLinkList();
        if (CollectionUtils.isNotEmpty(techLinkList)) {
            techLinkList.forEach(techLink -> techLink.setCheckId(checkId));
            buWorkCheckTechLinkMapper.insertList(techLinkList);
        }
    }

    private void deleteOtherInfos(List<String> checkIdList) {
        if (CollectionUtils.isEmpty(checkIdList)) {
            return;
        }

        LambdaQueryWrapper<BuWorkCheckItem> itemWrapper = new LambdaQueryWrapper<BuWorkCheckItem>()
                .in(BuWorkCheckItem::getCheckId, checkIdList);
        buWorkCheckItemMapper.delete(itemWrapper);

        LambdaQueryWrapper<BuWorkCheckTechLink> techLinkWrapper = new LambdaQueryWrapper<BuWorkCheckTechLink>()
                .in(BuWorkCheckTechLink::getCheckId, checkIdList);
        buWorkCheckTechLinkMapper.delete(techLinkWrapper);
    }

}
