package org.jeecg.modules.basemanage.line.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.basemanage.line.entity.BuMtrLine;
import org.jeecg.modules.basemanage.line.mapper.BuMtrLineMapper;
import org.jeecg.modules.basemanage.line.service.IBuMtrLineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 线路 服务实现类
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-13
 */
@Slf4j
@Service
public class BuMtrLineServiceImpl extends ServiceImpl<BuMtrLineMapper, BuMtrLine> implements IBuMtrLineService {

    @Resource
    private BuMtrLineMapper buMtrLineMapper;

    /**
     * @see IBuMtrLineService#listAll()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuMtrLine> listAll() {
        LambdaQueryWrapper<BuMtrLine> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(BuMtrLine::getLineNum);
        wrapper.orderByAsc(BuMtrLine::getLineName);
        return buMtrLineMapper.selectList(wrapper);
    }

    /**
     * @see IBuMtrLineService#listByCurrentUser()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuMtrLine> listByCurrentUser() {
        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        // 暂时返回所有的，不过滤
        //TODO-zhaiyantao 2021/2/24 15:20 根据当前人员所属车辆段过滤
        LambdaQueryWrapper<BuMtrLine> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(BuMtrLine::getLineNum);
        return buMtrLineMapper.selectList(wrapper);
    }

    /**
     * @see IBuMtrLineService#getLineById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuMtrLine getLineById(String lineId) throws Exception {
        return buMtrLineMapper.selectLineById(lineId);
    }

}
