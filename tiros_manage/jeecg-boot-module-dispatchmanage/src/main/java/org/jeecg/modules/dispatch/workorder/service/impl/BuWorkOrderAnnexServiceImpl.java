package org.jeecg.modules.dispatch.workorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderAnnex;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuWorkOrderAnnexSaveVO;
import org.jeecg.modules.dispatch.workorder.mapper.BuWorkOrderAnnexMapper;
import org.jeecg.modules.dispatch.workorder.service.BuWorkOrderAnnexService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 工单附件 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-19
 */
@Service
public class BuWorkOrderAnnexServiceImpl extends ServiceImpl<BuWorkOrderAnnexMapper, BuWorkOrderAnnex> implements BuWorkOrderAnnexService {

    @Resource
    private BuWorkOrderAnnexMapper buWorkOrderAnnexMapper;


    /**
     * @see BuWorkOrderAnnexService#listAnnex(String, String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuWorkOrderAnnex> listAnnex(String orderId, String taskId) throws Exception {
        LambdaQueryWrapper<BuWorkOrderAnnex> wrapper = new LambdaQueryWrapper<BuWorkOrderAnnex>()
                .orderByAsc(BuWorkOrderAnnex::getType)
                .orderByAsc(BuWorkOrderAnnex::getName);
        if (StringUtils.isNotBlank(orderId)) {
            wrapper.eq(BuWorkOrderAnnex::getWorkOrderId, orderId);
        }
        if (StringUtils.isNotBlank(taskId)) {
            wrapper.eq(BuWorkOrderAnnex::getTaskId, taskId);
        }

        return buWorkOrderAnnexMapper.selectList(wrapper);
    }

    /**
     * @see BuWorkOrderAnnexService#saveAnnex(BuWorkOrderAnnexSaveVO)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveAnnex(BuWorkOrderAnnexSaveVO orderAnnexSaveVO) throws Exception {
        Boolean deleteBefore = orderAnnexSaveVO.getDeleteBefore();
        String orderId = orderAnnexSaveVO.getOrderId();
        String taskId = orderAnnexSaveVO.getTaskId();

        // 删除以前的附件
        if (null == deleteBefore) {
            deleteBefore = true;
        }
        if (deleteBefore) {
            LambdaQueryWrapper<BuWorkOrderAnnex> wrapper = new LambdaQueryWrapper<BuWorkOrderAnnex>()
                    .eq(BuWorkOrderAnnex::getWorkOrderId, orderId);
            if (StringUtils.isNotBlank(orderAnnexSaveVO.getTaskId())) {
                wrapper.eq(BuWorkOrderAnnex::getTaskId, taskId);
            }
            buWorkOrderAnnexMapper.delete(wrapper);
        }

        // 插入附件
        List<BuWorkOrderAnnex> annexList = orderAnnexSaveVO.getAnnexList();
        if (CollectionUtils.isNotEmpty(annexList)) {
            for (BuWorkOrderAnnex orderAnnex : annexList) {
                orderAnnex.setWorkOrderId(orderId)
                        .setTaskId(taskId);
                buWorkOrderAnnexMapper.insert(orderAnnex);
            }
        }

        return true;
    }

    /**
     * @see BuWorkOrderAnnexService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> orderAnnexIdList = Arrays.asList(ids.split(","));

        if (CollectionUtils.isNotEmpty(orderAnnexIdList)) {
            buWorkOrderAnnexMapper.deleteBatchIds(orderAnnexIdList);
        }

        return true;
    }

}
