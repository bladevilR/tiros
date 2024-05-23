package org.jeecg.modules.dispatch.fault.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.api.client.util.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.dispatch.fault.bean.BuFaultCodeNew;
import org.jeecg.modules.dispatch.fault.mapper.BuFaultCodeNewMapper;
import org.jeecg.modules.dispatch.fault.service.BuFaultCodeNewService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 090102003, 09 表示专业编码，01 表示系统编码  02 表示 子系统编码  003 表示部件编码
 * -& 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2021-06-29
 */
@Service
public class BuFaultCodeNewServiceImpl extends ServiceImpl<BuFaultCodeNewMapper, BuFaultCodeNew> implements BuFaultCodeNewService {
    @Resource
    private BuFaultCodeNewMapper faultCodeNewMapper;

    @Override
    public List<BuFaultCodeNew> selectList(String searchText) {
        return faultCodeNewMapper.selectAll(searchText);
    }

    @Override
    public List<BuFaultCodeNew> selectByParentCode(String code) {
        return faultCodeNewMapper.selectByParentCode(code);
    }

    @Override
    public List<BuFaultCodeNew> selectParentCodeByCode(String code) {
        List<BuFaultCodeNew> result = new ArrayList<>();
        BuFaultCodeNew faultCodeNew = faultCodeNewMapper.selectParentCodeByCode(code);
        if (StringUtils.isBlank(faultCodeNew.getParentCode())) {
            result.add(faultCodeNew);
            return result;
        } else {
            getBuFaultCodeNew(faultCodeNew.getFltCode(), result);
        }
       return result.stream().sorted(Comparator.comparing(BuFaultCodeNew::getFltCode)).collect(Collectors.toList());
    }

    private List<BuFaultCodeNew> getBuFaultCodeNew(String code, List<BuFaultCodeNew> result) {
        BuFaultCodeNew faultCodeNew = faultCodeNewMapper.selectParentCodeByCode(code);
        result.add(faultCodeNew);
        if (StringUtils.isNotBlank(faultCodeNew.getParentCode())) {
            getBuFaultCodeNew(faultCodeNew.getParentCode(), result);
        }
        return result;
    }


}
