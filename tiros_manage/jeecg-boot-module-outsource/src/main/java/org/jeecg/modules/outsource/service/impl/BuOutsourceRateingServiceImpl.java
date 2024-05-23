package org.jeecg.modules.outsource.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.outsource.bean.BuBaseSupplier;
import org.jeecg.modules.outsource.bean.BuOutsourceRateing;
import org.jeecg.modules.outsource.bean.BuOutsourceRateingAnnex;
import org.jeecg.modules.outsource.bean.vo.BuOutsourceRateingQueryVO;
import org.jeecg.modules.outsource.mapper.BuBaseSupplierMapper;
import org.jeecg.modules.outsource.mapper.BuOutsourceRateingAnnexMapper;
import org.jeecg.modules.outsource.mapper.BuOutsourceRateingMapper;
import org.jeecg.modules.outsource.service.BuOutsourceRateingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * <p>
 * 委外评分 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
@Service
public class BuOutsourceRateingServiceImpl extends ServiceImpl<BuOutsourceRateingMapper, BuOutsourceRateing> implements BuOutsourceRateingService {

    @Resource
    private BuOutsourceRateingMapper rateingMapper;
    @Resource
    private BuOutsourceRateingAnnexMapper annexMapper;


    @Override
    public IPage<BuOutsourceRateing> page(BuOutsourceRateingQueryVO queryVO, Integer pageNo, Integer pageSize) {
        return rateingMapper.page(new Page<>(pageNo, pageSize), queryVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean editRateing(BuOutsourceRateing outsourceRateing) throws Exception {
        String id = outsourceRateing.getId();
        List<BuOutsourceRateingAnnex> annexes = outsourceRateing.getAnnexes();
        if (oConvertUtils.listIsNotEmpty(annexes)) {
            annexes.forEach(a -> {
                a.setRateId(id);
                a.insert();
            });
        }
        return outsourceRateing.updateById();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeBatch(List<String> asList) throws Exception{
        asList.forEach(id -> {
            annexMapper.delete(Wrappers.<BuOutsourceRateingAnnex>lambdaUpdate()
                    .eq(BuOutsourceRateingAnnex::getRateId, id));
        });
        return this.removeByIds(asList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveRateing(BuOutsourceRateing outsourceRateing) throws Exception {
        String id=UUIDGenerator.generate();
        outsourceRateing.setId(id);
        outsourceRateing.insert();
        List<BuOutsourceRateingAnnex> annexList=outsourceRateing.getAnnexes();
        if(CollectionUtils.isNotEmpty(annexList)){
            annexList.forEach(item->{
                item.setRateId(id);
                item.insert();
            });
        }
        return true;
    }

}
