package org.jeecg.modules.message.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.basemanage.alertaccept.entity.BuBaseAlert;
import org.jeecg.modules.basemanage.alertaccept.entity.BuBaseAlertAccept;
import org.jeecg.modules.basemanage.alertaccept.mapper.BuBaseAlertAcceptMapper;
import org.jeecg.modules.basemanage.alertaccept.mapper.BuBaseAlertMapper;
import org.jeecg.modules.message.entity.BuMailSendMessage;
import org.jeecg.modules.message.mapper.BuMailSendMessageMapper;
import org.jeecg.modules.message.service.BuMailSendMessageService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.entity.SysUserDepart;
import org.jeecg.modules.system.entity.SysUserRole;
import org.jeecg.modules.system.mapper.SysUserDepartMapper;
import org.jeecg.modules.system.mapper.SysUserMapper;
import org.jeecg.modules.system.mapper.SysUserRoleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yyg
 */
@Service
public class BuMailSendMessageServiceImpl extends ServiceImpl<BuMailSendMessageMapper, BuMailSendMessage> implements BuMailSendMessageService {

    @Resource
    private BuBaseAlertMapper buBaseAlertMapper;
    @Resource
    private BuMailSendMessageMapper buMailSendMessageMapper;
    @Resource
    private BuBaseAlertAcceptMapper buBaseAlertAcceptMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysUserRoleMapper sysUserRoleMapper;
    @Resource
    private SysUserDepartMapper sysUserDepartMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMailMsg() {
        List<BuMailSendMessage> mailMsgList = buMailSendMessageMapper.selectList(Wrappers.lambdaQuery());
        List<BuBaseAlert> alertList = buBaseAlertMapper.selectList(Wrappers.emptyWrapper());
        if (CollectionUtil.isNotEmpty(alertList)) {
            List<BuMailSendMessage> saveMailSendMessageList = new ArrayList<>();
            List<BuBaseAlertAccept> alertAcceptList = buBaseAlertAcceptMapper.selectList(Wrappers.lambdaQuery());
            Map<Integer, List<BuBaseAlertAccept>> alertAcceptMap = alertAcceptList.stream().collect(Collectors.groupingBy(BuBaseAlertAccept::getAlertType));
            if (CollectionUtil.isNotEmpty(mailMsgList)) {
                List<String> mailMsgIdList = mailMsgList.stream().map(BuMailSendMessage::getFromId).collect(Collectors.toList());
                alertList.forEach(alert -> {
                    if (!mailMsgIdList.contains(alert.getId())) {
                        createMailMsg(saveMailSendMessageList, alertAcceptMap, alert);
                    }
                });
            } else {
                alertList.forEach(alert -> createMailMsg(saveMailSendMessageList, alertAcceptMap, alert));
            }

            if(CollectionUtil.isNotEmpty(saveMailSendMessageList)){
                this.saveBatch(saveMailSendMessageList);
            }
        }
    }

    private void createMailMsg(List<BuMailSendMessage> saveMailSendMessageList, Map<Integer, List<BuBaseAlertAccept>> alertAcceptMap, BuBaseAlert alert) {
        List<BuBaseAlertAccept> alertAccepts = alertAcceptMap.get(alert.getAlertType());
        List<String> receiveMailList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(alertAccepts)) {
            alertAccepts.forEach(accept -> {
                String receiveMail = "";
                Integer dimension = accept.getDimension();
                List<String> targetIdList = Arrays.asList(accept.getTarget().split(","));
                List<String> sysUserIdList = new ArrayList<>();
                if (dimension == 1) { //人员
                    sysUserIdList.addAll(targetIdList);
                } else if (dimension == 2) { //角色
                    List<SysUserRole> userRoleList = sysUserRoleMapper.selectList(Wrappers.<SysUserRole>lambdaQuery().in(SysUserRole::getRoleId, targetIdList));
                    userRoleList.forEach(item -> sysUserIdList.add(item.getUserId()));
                } else { //机构
                    List<SysUserDepart> userDepartList = sysUserDepartMapper.selectList(Wrappers.<SysUserDepart>lambdaQuery().in(SysUserDepart::getDepId, targetIdList));
                    userDepartList.forEach(item -> sysUserIdList.add(item.getUserId()));
                }
                receiveMail = selectUserEmail(sysUserIdList);
                if (StringUtils.isNotBlank(receiveMail)) {
                    receiveMailList.add(receiveMail);
                }
            });
        }
        if (CollectionUtil.isNotEmpty(receiveMailList)) {
            String title = parseAlertType(alert.getAlertType());
            BuMailSendMessage message = new BuMailSendMessage()
                    .setId(UUIDGenerator.generate())
                    .setContent(alert.getAlertContent())
                    .setFromId(alert.getId())
                    .setSendStatus(0)
                    .setTitle(title)
                    .setReceiveMail( receiveMailList.stream().distinct().collect(Collectors.joining(",")));
            saveMailSendMessageList.add(message);

        }
    }

    private String parseAlertType(Integer alertType) {
        String title = "架大修管理系统";
        switch (alertType) {
            case 1:
                title += "物资库存预警";
                break;

            case 2:
                title += "物资到期预警";
                break;

            case 3:
                title += "工器具送检预警";
                break;

            case 4:
                title += "部件质保预警";
                break;

            case 5:
                title += "测量异常预警";
                break;

            case 6:
                title += "委外逾期预警";
                break;

            case 7:
                title += "工单逾期预警";
                break;

            case 8:
                title += "故障处理预警";
                break;
            default:
                break;

        }

        return title;


    }


    private String selectUserEmail(List<String> userIdList) {
        List<SysUser> sysUsers = sysUserMapper.selectBatchIds(userIdList);
        return sysUsers.stream().map(SysUser::getEmail).filter(StringUtils::isNotBlank).collect(Collectors.joining(","));

    }

}
