package org.jeecg.modules.material.entry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.material.entry.bean.BuMaterialEntryDetail;
import org.jeecg.modules.material.entry.bean.BuMaterialEntryLevelFourDetail;
import org.jeecg.modules.material.entry.bean.vo.BuMaterialEntryConfirmVO;
import org.jeecg.modules.material.entry.bean.vo.BuMaterialEntryLevelFourDetailVO;
import org.jeecg.modules.material.entry.bean.vo.BuMaterialEntryQueryVO;
import org.jeecg.modules.material.entry.bean.vo.LevelFourEntryData;
import org.jeecg.modules.material.entry.mapper.BuMaterialEntryDetailMapper;
import org.jeecg.modules.material.entry.mapper.BuMaterialEntryLevelFourDetailMapper;
import org.jeecg.modules.material.entry.mapper.BuMaterialEntryOrderMapper;
import org.jeecg.modules.material.entry.service.BuMaterialEntryDetailService;
import org.jeecg.modules.material.material.bean.BuMaterialType;
import org.jeecg.modules.material.material.mapper.BuMaterialTypeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 每次同步新增的物资或数量都都在该表记录，一次同步如果发现有新增，则创建一条入库单及相关物资明细 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-08
 */
@Service
@Slf4j
public class BuMaterialEntryDetailServiceImpl extends ServiceImpl<BuMaterialEntryDetailMapper, BuMaterialEntryDetail> implements BuMaterialEntryDetailService {

    @Resource
    private BuMaterialEntryOrderMapper buMaterialEntryOrderMapper;
    @Resource
    private BuMaterialEntryDetailMapper buMaterialEntryDetailMapper;
    @Resource
    private BuMaterialTypeMapper buMaterialTypeMapper;
    @Resource
    private BuMaterialEntryLevelFourDetailMapper buMaterialEntryLevelFourDetailMapper;


    /**
     * @see BuMaterialEntryDetailService#pageEntryDetail(BuMaterialEntryQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuMaterialEntryDetail> pageEntryDetail(BuMaterialEntryQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        IPage<BuMaterialEntryDetail> page = buMaterialEntryDetailMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
        setTotalPrice(page.getRecords());

        return page;
    }

    /**
     * @see BuMaterialEntryDetailService#getEntryDetailById(String)`
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuMaterialEntryDetail getEntryDetailById(String entryDetailId) throws Exception {
        BuMaterialEntryDetail entryDetail = buMaterialEntryDetailMapper.selectEntryDetailById(entryDetailId);
        setTotalPrice(Collections.singletonList(entryDetail));
        setLevelFourEntry(entryDetail);

        return entryDetail;
    }

    private void setLevelFourEntry(BuMaterialEntryDetail entryDetail) {
        List<BuMaterialEntryLevelFourDetail> detailList = entryDetail.getDetailList();
        if (CollectionUtils.isNotEmpty(detailList)) {
            List<LevelFourEntryData> entryDataList = new ArrayList<>();
            detailList.forEach(item -> {
                LevelFourEntryData entryData = new LevelFourEntryData();
                entryData.setAmount(item.getAmount());
                entryData.setSelfWarehouseId(item.getSelfWarehouseId());
                entryData.setSelfWarehouseName(item.getSelfWarehouseName());
                entryDataList.add(entryData);
            });
            entryDetail.setLevelFourDetail(entryDataList);
        }
    }

    /**
     * @see BuMaterialEntryDetailService#saveEntryDetail(BuMaterialEntryDetail)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveEntryDetail(BuMaterialEntryDetail detail) throws Exception {
        if (StringUtils.isNotBlank(detail.getId())) {
            buMaterialEntryDetailMapper.deleteById(detail.getId());
        }

        Date now = new Date();

        // 获取物资以前是否有入库明细记录
        String materialTypeId = detail.getMaterialTypeId();
        LambdaQueryWrapper<BuMaterialEntryDetail> searchDetailHistoryWrapper = new LambdaQueryWrapper<BuMaterialEntryDetail>()
                .eq(BuMaterialEntryDetail::getMaterialTypeId, materialTypeId);
        List<BuMaterialEntryDetail> detailHistoryList = buMaterialEntryDetailMapper.selectList(searchDetailHistoryWrapper);
        Map<String, Long> materialTypeIdHistoryCount = detailHistoryList.stream()
                .collect(Collectors.groupingBy(BuMaterialEntryDetail::getMaterialTypeId, Collectors.counting()));

        // 设置入库明细属性
        detail.setEbsWarehouseId(detail.getEbsWarehouseLevl2());
        if (null == detail.getConfirm()) {
            detail.setConfirm(0);
        }
        if (StringUtils.isBlank(detail.getEntryUserId())) {
            // 获取登录人信息
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
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

        buMaterialEntryDetailMapper.insert(detail);

        return true;
    }

    /**
     * @see BuMaterialEntryDetailService#confirmEntry(BuMaterialEntryConfirmVO)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean confirmEntry(BuMaterialEntryConfirmVO confirmVO) throws Exception {
        BuMaterialEntryDetail entryDetail = buMaterialEntryDetailMapper.selectById(confirmVO.getBuMaterialEntryDetailId());
        if (null == entryDetail) {
            throw new JeecgBootException("入库明细信息不存在");
        }

        // 设置入库明细
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        entryDetail
                .setProductionDate(confirmVO.getProductionDate())
                .setExpirDate(confirmVO.getExpirDate())
                .setExpirDay(confirmVO.getExpirDay())
                .setSelfWarehouseId(confirmVO.getSelfWarehouseId())
                .setConfirmAmount(confirmVO.getConfirmAmount())
                .setConfirm(1)
                .setConfirmUserId(sysUser.getId());
        buMaterialEntryDetailMapper.updateById(entryDetail);

        // 设置物资属性
        if (null != confirmVO.getMaterialAttr()) {
            String materialTypeId = entryDetail.getMaterialTypeId();
            BuMaterialType buMaterialType = buMaterialTypeMapper.selectById(materialTypeId);
            if (null != buMaterialType) {
                buMaterialType.setCategory2(confirmVO.getMaterialAttr());
                buMaterialTypeMapper.updateById(buMaterialType);
            }
        }

        // 暂时不做库存修改

        return true;
    }

    /**
     * @see BuMaterialEntryDetailService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> detailIdList = Arrays.asList(ids.split(","));

        LambdaQueryWrapper<BuMaterialEntryDetail> detailWrapper = new LambdaQueryWrapper<BuMaterialEntryDetail>()
                .in(BuMaterialEntryDetail::getId, detailIdList);
        List<BuMaterialEntryDetail> detailList = buMaterialEntryDetailMapper.selectList(detailWrapper);
        Set<String> entryOrderIdSet = detailList.stream()
                .map(BuMaterialEntryDetail::getEntryOrderId)
                .collect(Collectors.toSet());

        // 删除入库明细
        buMaterialEntryDetailMapper.deleteBatchIds(detailIdList);

        // 检查入库单，如果不包含入库明细了，删除入库单
        LambdaQueryWrapper<BuMaterialEntryDetail> checkOrderHasDetailWrapper = new LambdaQueryWrapper<BuMaterialEntryDetail>()
                .in(BuMaterialEntryDetail::getEntryOrderId, entryOrderIdSet);
        List<BuMaterialEntryDetail> checkOrderHasDetailList = buMaterialEntryDetailMapper.selectList(checkOrderHasDetailWrapper);

        Set<String> hasDetailOrderIdSet = checkOrderHasDetailList.stream()
                .map(BuMaterialEntryDetail::getEntryOrderId)
                .collect(Collectors.toSet());
        entryOrderIdSet.removeAll(hasDetailOrderIdSet);
        if (CollectionUtils.isNotEmpty(entryOrderIdSet)) {
            buMaterialEntryOrderMapper.deleteBatchIds(entryOrderIdSet);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmEntryLevelFourWarehouse(BuMaterialEntryLevelFourDetailVO confirmVO) throws Exception {
        BuMaterialEntryDetail entryDetail = buMaterialEntryDetailMapper.selectById(confirmVO.getBuMaterialEntryDetailId());
        if (null == entryDetail) {
            throw new JeecgBootException("入库明细信息不存在");
        }

        // 设置入库明细
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        entryDetail
                .setProductionDate(confirmVO.getProductionDate())
                .setExpirDate(confirmVO.getExpirDate())
                .setExpirDay(confirmVO.getExpirDay())
                .setConfirmAmount(confirmVO.getConfirmAmount())
                .setConfirm(1)
                .setConfirmUserId(sysUser.getId());
        buMaterialEntryDetailMapper.updateById(entryDetail);

        // 设置物资属性
        if (null != confirmVO.getMaterialAttr()) {
            String materialTypeId = entryDetail.getMaterialTypeId();
            BuMaterialType buMaterialType = buMaterialTypeMapper.selectById(materialTypeId);
            if (null != buMaterialType) {
                buMaterialType.setCategory2(confirmVO.getMaterialAttr());
                buMaterialTypeMapper.updateById(buMaterialType);
            }
        }
        //设置四级库位
        List<BuMaterialEntryLevelFourDetail> detailList = new ArrayList<>();
        List<LevelFourEntryData> entryDataList = confirmVO.getConfirmVO();
        entryDataList.forEach(item -> {
            BuMaterialEntryLevelFourDetail detail = new BuMaterialEntryLevelFourDetail()
                    .setEnterDetailId(confirmVO.getBuMaterialEntryDetailId())
                    .setEntryOrderId(confirmVO.getEntryOrderId())
                    .setMaterialTypeId(confirmVO.getBuMaterialEntryDetailId())
                    .setAmount(item.getAmount())
                    .setSelfWarehouseId(item.getSelfWarehouseId())
                    .setOprationDate(new Date())
                    .setOprationUserId(sysUser.getId());
            detailList.add(detail);

        });
        buMaterialEntryLevelFourDetailMapper.insertBatch(detailList);
        return true;
    }


    private void setTotalPrice(List<BuMaterialEntryDetail> entryDetailList) {
        if (CollectionUtils.isEmpty(entryDetailList)) {
            return;
        }

        for (BuMaterialEntryDetail entryDetail : entryDetailList) {
            entryDetail.setTotalPrice(entryDetail.getPrice().multiply(entryDetail.getAmount()));
        }
    }

}
