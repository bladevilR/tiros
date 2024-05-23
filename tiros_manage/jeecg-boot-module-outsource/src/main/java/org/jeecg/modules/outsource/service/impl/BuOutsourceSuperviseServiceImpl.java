package org.jeecg.modules.outsource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.outsource.bean.BuOutsourceSupervise;
import org.jeecg.modules.outsource.bean.vo.BuOutsourceSuperviseQueryVO;
import org.jeecg.modules.outsource.mapper.BuOutsourceSuperviseMapper;
import org.jeecg.modules.outsource.service.BuOutsourceSuperviseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 委外监修 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
@Service
public class BuOutsourceSuperviseServiceImpl extends ServiceImpl<BuOutsourceSuperviseMapper, BuOutsourceSupervise> implements BuOutsourceSuperviseService {

    @Resource
    private BuOutsourceSuperviseMapper buOutsourceSuperviseMapper;


    /**
     * @see BuOutsourceSuperviseService#page(BuOutsourceSuperviseQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuOutsourceSupervise> page(BuOutsourceSuperviseQueryVO queryVO, Integer pageNo, Integer pageSize) {
        return buOutsourceSuperviseMapper.selectSupervisePage(new Page<>(pageNo, pageSize), queryVO);
    }


    /**
     * @see BuOutsourceSuperviseService#getSuperviseById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuOutsourceSupervise getSuperviseById(String id) throws Exception {
        return buOutsourceSuperviseMapper.selectSuperviseById(id);
    }

    /**
     * @see BuOutsourceSuperviseService#getSuperviseByOrderAndTask(String, String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuOutsourceSupervise getSuperviseByOrderAndTask(String orderId, String orderTaskId) throws Exception {
        List<BuOutsourceSupervise> superviseList = buOutsourceSuperviseMapper.selectOneByOrderIdAndOrderTaskId(orderId,orderTaskId);

        if (CollectionUtils.isNotEmpty(superviseList)) {
            return superviseList.get(0);
        }

        return null;
    }

    /**
     * @see BuOutsourceSuperviseService#saveSupervise(BuOutsourceSupervise)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveSupervise(BuOutsourceSupervise supplier) throws  Exception{
        String id = UUIDGenerator.generate();
        supplier.setId(id);
        supplier.insert();
//        WfBizStatus wfBizStatus = new WfBizStatus();
//        wfBizStatus.setBusinessKey(id);
//        wfBizStatus.setProcessStatus("0");
//        wfBizStatus.setSolutionCode("");
//        return  wfBizStatus.insert();

        return true;
    }

}
