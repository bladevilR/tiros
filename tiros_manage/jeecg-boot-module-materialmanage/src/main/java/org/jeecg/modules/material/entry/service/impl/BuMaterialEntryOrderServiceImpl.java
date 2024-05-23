package org.jeecg.modules.material.entry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.tiros.serialnumber.SerialNumberGenerate;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.material.entry.bean.BuMaterialEntryDetail;
import org.jeecg.modules.material.entry.bean.BuMaterialEntryOrder;
import org.jeecg.modules.material.entry.mapper.BuMaterialEntryDetailMapper;
import org.jeecg.modules.material.entry.mapper.BuMaterialEntryOrderMapper;
import org.jeecg.modules.material.entry.service.BuMaterialEntryOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 入库单 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-08
 */
@Service
public class BuMaterialEntryOrderServiceImpl extends ServiceImpl<BuMaterialEntryOrderMapper, BuMaterialEntryOrder> implements BuMaterialEntryOrderService {

    @Resource
    private BuMaterialEntryOrderMapper buMaterialEntryOrderMapper;
    @Resource
    private BuMaterialEntryDetailMapper buMaterialEntryDetailMapper;
    @Resource
    private SerialNumberGenerate serialNumberGenerate;


    /**
     * @see BuMaterialEntryOrderService#getEntryOrderById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuMaterialEntryOrder getEntryOrderById(String entryOrderId) throws Exception {
        BuMaterialEntryOrder entryOrder = buMaterialEntryOrderMapper.selectEntryOrderById(entryOrderId);

        List<BuMaterialEntryDetail> entryDetailList = buMaterialEntryDetailMapper.selectListByEntryOrderId(entryOrderId);
        entryOrder.setDetailList(entryDetailList);

        return entryOrder;
    }

    /**
     * @see BuMaterialEntryOrderService#saveEntryOrder(BuMaterialEntryOrder)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveEntryOrder(BuMaterialEntryOrder entryOrder) throws Exception {
        Date now = new Date();
        if (StringUtils.isBlank(entryOrder.getEntryOrderId())) {
            String id = UUIDGenerator.generate();
            String code = serialNumberGenerate.generateSerialNumberByCode("EntryOrderCode");
            entryOrder.setId(id)
                    .setEntryNo(code);
            buMaterialEntryOrderMapper.insert(entryOrder);
        } else {
            buMaterialEntryDetailMapper.deleteById(entryOrder.getId());
            entryOrder.setId(entryOrder.getEntryOrderId());
            buMaterialEntryOrderMapper.updateById(entryOrder);
        }
        String entryOrderId = entryOrder.getId();

        // 删除入库单明细
       /* LambdaQueryWrapper<BuMaterialEntryDetail> detailWrapper = new LambdaQueryWrapper<BuMaterialEntryDetail>()
                .eq(BuMaterialEntryDetail::getEntryOrderId, entryOrderId);
        List<BuMaterialEntryDetail> localDetails = buMaterialEntryDetailMapper.selectList(detailWrapper);
        buMaterialEntryDetailMapper.delete(detailWrapper);*/
        // 删除入库单单
      //  buMaterialEntryOrderMapper.deleteById(entryOrderId);

        // 插入入库单单
       // buMaterialEntryOrderMapper.insert(entryOrder);
        // 插入入库单明细
        List<BuMaterialEntryDetail> detailList = entryOrder.getDetailList();
        if (CollectionUtils.isNotEmpty(detailList)) {
            // 获取物资以前是否有入库明细记录
            Set<String> materialTypeIdSet = detailList.stream()
                    .map(BuMaterialEntryDetail::getMaterialTypeId)
                    .collect(Collectors.toSet());
            LambdaQueryWrapper<BuMaterialEntryDetail> searchDetailHistoryWrapper = new LambdaQueryWrapper<BuMaterialEntryDetail>()
                    .in(BuMaterialEntryDetail::getMaterialTypeId, materialTypeIdSet);
            List<BuMaterialEntryDetail> detailHistoryList = buMaterialEntryDetailMapper.selectList(searchDetailHistoryWrapper);
            Map<String, Long> materialTypeIdHistoryCount = detailHistoryList.stream()
                    .collect(Collectors.groupingBy(BuMaterialEntryDetail::getMaterialTypeId, Collectors.counting()));
            // 获取登录人信息
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

            for (BuMaterialEntryDetail detail : detailList) {
                // 设置入库明细属性
                detail.setEntryOrderId(entryOrderId)
                        .setEbsWarehouseId(detail.getEbsWarehouseLevl2());
                if (null == detail.getConfirm()) {
                    detail.setConfirm(0);
                }

                if (StringUtils.isBlank(detail.getEntryUserId())) {
                    detail.setEntryUserId(sysUser.getId());
                }
                if (null == detail.getEntryDate()) {
                    detail.setEntryDate(now);
                }
                if (null == detail.getEntryClass()) {
                    Long historyCount = materialTypeIdHistoryCount.get(detail.getMaterialTypeId());
                    if (null == historyCount || 0 == historyCount) {
                        detail.setEntryClass(1);
                    } else {
                        detail.setEntryClass(2);
                    }
                }
            }

            buMaterialEntryDetailMapper.insertList(detailList);
        }

        return true;
    }

}
