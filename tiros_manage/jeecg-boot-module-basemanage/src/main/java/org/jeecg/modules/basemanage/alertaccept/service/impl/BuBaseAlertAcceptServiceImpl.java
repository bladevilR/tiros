package org.jeecg.modules.basemanage.alertaccept.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.basemanage.alertaccept.entity.BuBaseAlertAccept;
import org.jeecg.modules.basemanage.alertaccept.entity.vo.BuBaseAlertAcceptVO;
import org.jeecg.modules.basemanage.alertaccept.mapper.BuBaseAlertAcceptMapper;
import org.jeecg.modules.basemanage.alertaccept.service.IBuBaseAlertAcceptService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 预警信息 服务实现类
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-09
 */
@Service
public class BuBaseAlertAcceptServiceImpl extends ServiceImpl<BuBaseAlertAcceptMapper, BuBaseAlertAccept> implements IBuBaseAlertAcceptService {
    @Resource
    private BuBaseAlertAcceptMapper alertAcceptMapper;

    @Override
    public List<String> isExit(BuBaseAlertAccept alertAccept) {
        String target = alertAccept.getTarget();
        List<String> targetList = Arrays.asList(target.split(","));
        List<String> returnList = new ArrayList<>();
        targetList.forEach(e -> {
            alertAccept.setTarget(e);
            BuBaseAlertAccept buBaseAlertAccept = alertAcceptMapper.selectByAlertAccept(alertAccept);
            if (buBaseAlertAccept != null) {
                returnList.add(e);
            }
        });
        alertAccept.setTarget(target);
        return returnList;
    }

    @Override
    public List<BuBaseAlertAccept> listAll(BuBaseAlertAcceptVO buBaseAlertAcceptVO) {
        return alertAcceptMapper.listAll(buBaseAlertAcceptVO);
    }

   /* @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTarget(BuBaseAlertAcceptDelVO delVO) {
        BuBaseAlertAccept  alertAccept =this.getById(delVO.getId());
        if(oConvertUtils.isNotEmpty(alertAccept)){
            List<String> targetStr=new ArrayList<>(Arrays.asList(alertAccept.getTarget().split(",")));
            targetStr.remove(delVO.getTargetId());
            if(targetStr==null||targetStr.size()==0){
                alertAcceptMapper.deleteById(delVO.getId());
            }else {
                alertAccept.setTarget(StringUtils.join(targetStr.toArray(), ","));
                alertAcceptMapper.updateById(alertAccept);
            }
        }
    }*/

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveList(BuBaseAlertAccept alertAccept) {
        if (oConvertUtils.isNotEmpty(alertAccept.getTarget())) {
            List<String> targetStr = Arrays.asList(alertAccept.getTarget().split(","));
            targetStr.forEach(e -> {
                alertAccept.setTarget(e);
                alertAccept.setId("");
                alertAcceptMapper.insert(alertAccept);
            });
        }
    }
}